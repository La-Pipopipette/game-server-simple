package fr.pipopipette.gameserver.simple.converter;

import fr.pipopipette.gameserver.simple.game.Square;
import fr.pipopipette.gameserver.simple.io.SquareOutput;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class BoardConverter {

    @Inject SquareConverter squareConverter;

    public SquareOutput[][] toBoardOutput(Square[][] board) {
        return Arrays.stream(board)
                .map(this::convertLine)
                .toArray(SquareOutput[][]::new);
    }

    private SquareOutput[] convertLine(Square[] line) {
        return Arrays.stream(line)
                .map(square -> squareConverter.toSquareOutput(square))
                .toArray(SquareOutput[]::new);
    }
}
