package fr.pipopipette.gameserver.simple.converter;

import fr.pipopipette.gameserver.simple.game.Square;
import fr.pipopipette.gameserver.simple.io.SquareOutput;

import javax.inject.Singleton;

@Singleton
public class SquareConverter {

    public SquareOutput toSquareOutput(Square square) {
        return SquareOutput.builder()
                .h(square.isHorizontal())
                .v(square.isVertical())
                .filledBy(square.getFilledBy())
                .build();
    }
}
