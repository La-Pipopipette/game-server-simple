package fr.pipopipette.gameserver.simple.game;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameTestCreation {

    @Test void gameCreationOk() {
        final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        final int nbPlayers = 2;
        final int width = 5;
        final int height = 7;

        Game game = new Game(uuid, nbPlayers, width, height);

        // Trivialities
        assertEquals(uuid, game.getId());
        assertEquals(nbPlayers, game.getNbPlayers());
        assertEquals(width, game.getWidth());
        assertEquals(height, game.getHeight());

        // Default values
        assertEquals(0, game.getPlayerToPlayIndex());
        assertEquals(0, game.getPlayerIdToPlay());
        assertFalse(game.isStarted());
        assertFalse(game.isFinished());

        // No players
        assertEquals(Collections.emptyMap(), game.getPlayers());

        // Board
        assertEquals(height + 1, game.getBoard().length);
        assertEquals(width + 1, game.getBoard()[0].length);
        for (Square[] line : game.getBoard()) {
            for (Square square : line) {
                assertNull(square.getFilledBy());
            }
        }
    }

}