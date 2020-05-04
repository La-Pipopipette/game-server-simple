package fr.pipopipette.gameserver.simple.io;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnInput {

    private Integer playerId;
    private Integer x;
    private Integer y;
    private Boolean vertical;
}
