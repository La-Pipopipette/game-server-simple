package fr.pipopipette.gameserver.simple.resource;

import fr.pipopipette.gameserver.simple.converter.GameConverter;
import fr.pipopipette.gameserver.simple.io.GameInput;
import fr.pipopipette.gameserver.simple.io.GameOutput;
import fr.pipopipette.gameserver.simple.io.TurnInput;
import fr.pipopipette.gameserver.simple.service.GameService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class GameResource {

    @Inject
    GameService gameService;
    @Inject
    GameConverter gameConverter;

    @POST
    @RolesAllowed({"identified"})
    public Uni<GameOutput> newGame(GameInput gameInput) {
        return gameService.create(gameInput)
                .map(game -> gameConverter.toGameOutput(game));
    }

    @PUT
    @Path("/{id}/turn")
    public Uni<GameOutput> playTurn(@PathParam UUID id, TurnInput turnInput) {
        return gameService.getById(id)
                .map(game -> game.playTurn(turnInput))
                .map(game -> gameConverter.toGameOutput(game));
    }
}