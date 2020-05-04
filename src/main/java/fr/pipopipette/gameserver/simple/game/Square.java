package fr.pipopipette.gameserver.simple.game;

import lombok.Getter;

@Getter
public class Square {

    private final boolean verticalAllowed;
    private final boolean horizontalAllowed;

    private boolean vertical;
    private boolean horizontal;
    private Integer filledBy;

    public Square(boolean verticalAllowed, boolean horizontalAllowed) {
        this.verticalAllowed = verticalAllowed;
        this.horizontalAllowed = horizontalAllowed;
    }

    public void play(boolean verticalPlayed) {
        if (verticalPlayed) {
            if (!verticalAllowed) {
                throw new RuntimeException("Playing vertical here is not allowed.");
            }
            if (vertical) {
                throw new RuntimeException("Vertical has already been played here.");
            }
            vertical = true;
        } else {
            if (!horizontalAllowed) {
                throw new RuntimeException("Playing horizontal here is not allowed.");
            }
            if (horizontal) {
                throw new RuntimeException("Horizontal has already been played here.");
            }
            horizontal = true;
        }
    }

    boolean canBeFilled() {
        return verticalAllowed && horizontalAllowed;
    }

    public void fillBy(int playerId) {
        filledBy = playerId;
    }
}
