package fr.pipopipette.gameserver.simple.resource;

import fr.pipopipette.gameserver.simple.converter.GameConverter;
import fr.pipopipette.gameserver.simple.game.Player;
import fr.pipopipette.gameserver.simple.io.GameOutput;
import fr.pipopipette.gameserver.simple.io.TurnInput;
import fr.pipopipette.gameserver.simple.service.GameService;
import fr.pipopipette.gameserver.simple.ws.GameOutputEncoder;
import fr.pipopipette.gameserver.simple.ws.ThrowableEncoder;
import fr.pipopipette.gameserver.simple.ws.TurnInputDecoder;
import io.smallrye.jwt.auth.principal.DefaultJWTTokenParser;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(
        value = "/{gameId}/{userId}",
        encoders = {GameOutputEncoder.class, ThrowableEncoder.class},
        decoders = {TurnInputDecoder.class})
@ApplicationScoped
@Log
public class GameSocket {

    @ConfigProperty(name = "mp.jwt.verify.publickey.location") String publicKeyLocation;
    @ConfigProperty(name = "mp.jwt.verify.issuer") String issuedBy;

    @Inject GameService gameService;
    @Inject GameConverter gameConverter;

    private final DefaultJWTTokenParser parser = new DefaultJWTTokenParser();

    Map<String, Map<Integer, Session>> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("gameId") String gameId, @PathParam("userId") Integer userId) {

        // JWT check
        String query = session.getQueryString();
        if (query != null) {

            String rawToken = query.split("=")[1];

            try {
                JwtContext jwtContext = parser.parse(rawToken, new JWTAuthContextInfo(publicKeyLocation, issuedBy));

                if (jwtContext == null || jwtContext.getJwtClaims() == null) {
                    throw new RuntimeException("Invalid JWT");
                }
                final JwtClaims jwtClaims = jwtContext.getJwtClaims();

                final String jwtUserId = jwtClaims.getClaimValueAsString("userId");
                if (!userId.toString().equals(jwtUserId)) {
                    throw new RuntimeException("ID from URL (" + userId + ") does not match ID from JWT (" + jwtUserId);
                }
                final String userName = jwtClaims.getClaimValueAsString("preferred_username");

                if (!sessions.containsKey(gameId)) {
                    sessions.put(gameId, new ConcurrentHashMap<>());
                }
                sessions.get(gameId).put(userId, session);

                log.info("User " + userId + " joins game " + gameId);

                gameService.getById(UUID.fromString(gameId))
                        .map(game -> game.join(new Player(userId, userName)))
                        .await().indefinitely();

                broadcast(gameId);

            } catch (Exception e) {
                log.severe(e.getMessage());
            }
        } else {
            log.info("Missing jwt query param"); // TODO throw error to user?
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("gameId") String gameId, @PathParam("userId") Integer userId) {

        log.info("User " + userId + " leaves game " + gameId);

        gameService.getById(UUID.fromString(gameId))
                .map(game -> game.leave(userId))
                .await().indefinitely();

        if (sessions.containsKey(gameId)) {
            sessions.get(gameId).remove(userId);
        }

        broadcast(gameId);
    }

    @OnError
    public void onError(Session session, @PathParam("gameId") String gameId, @PathParam("userId") Integer userId, Throwable throwable) {
        sessions.get(gameId).get(userId).getAsyncRemote().sendObject(throwable);
    }

    @OnMessage
    public void onMessage(TurnInput turnInput, @PathParam("gameId") String gameId, @PathParam("userId") Integer userId) {

        gameService.getById(UUID.fromString(gameId))
                .map(game -> game.playTurn(turnInput))
                .await().indefinitely();

        broadcast(gameId);
    }

    private void broadcast(String gameId) {

        log.info("Broadcast game " + gameId);

        GameOutput gameOutput = gameService.getById(UUID.fromString(gameId))
                .map(game -> gameConverter.toGameOutput(game))
                .await().indefinitely();
        sessions.get(gameId).values().forEach(session -> session.getAsyncRemote().sendObject(
                gameOutput,
                sendResult -> {
                    if (sendResult.getException() != null) {
                        log.info("Unable to send message:" + sendResult.getException());
                    }
                })
        );
    }
}
