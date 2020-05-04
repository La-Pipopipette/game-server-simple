package fr.pipopipette.gameserver.simple.service;

import fr.pipopipette.gameserver.simple.game.Game;
import fr.pipopipette.gameserver.simple.io.GameInput;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class GameService {

    private final Map<UUID, Game> games = new HashMap<>();

    public Uni<Game> create(GameInput gameInput) {
        final UUID uuid = UUID.randomUUID();
        final Game game = new Game(uuid, gameInput.getNbPlayers(), gameInput.getWidth(), gameInput.getHeight());
        games.put(uuid, game);
        return Uni.createFrom().item(game);
    }

    public Uni<Game> getById(UUID id) {
        return Uni.createFrom().item(games.get(id));
    }
}
