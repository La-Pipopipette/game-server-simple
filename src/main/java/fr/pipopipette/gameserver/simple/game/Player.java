package fr.pipopipette.gameserver.simple.game;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Player {
    private int id;
    private String name;
    private int score;
    private boolean active = true;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void increaseScore() {
        this.score++;
    }
}
