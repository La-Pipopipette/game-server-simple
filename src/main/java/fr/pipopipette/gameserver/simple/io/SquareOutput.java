package fr.pipopipette.gameserver.simple.io;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SquareOutput {

    private boolean h;
    private boolean v;
    private Integer filledBy;

}
