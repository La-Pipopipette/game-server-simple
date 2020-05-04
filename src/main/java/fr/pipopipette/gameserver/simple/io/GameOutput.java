package fr.pipopipette.gameserver.simple.io;

import fr.pipopipette.gameserver.simple.game.Player;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class GameOutput {
    private final UUID id;
    private final int nbPlayers;
    private final int width;
    private final int height;

    private final Map<Integer, Player> players;
    private final SquareOutput[][] board;
    private final Integer playerIdToPlay;
    private final boolean started;
    private final boolean finished;
    private final Set<Integer> winnersIds;
    private final TurnInput lastPlayedTurn;
}
