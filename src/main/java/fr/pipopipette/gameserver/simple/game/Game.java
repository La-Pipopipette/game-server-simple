package fr.pipopipette.gameserver.simple.game;

import fr.pipopipette.gameserver.simple.io.TurnInput;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Log
public class Game {

    private final UUID id;
    private final int nbPlayers;
    private final int width;
    private final int height;

    private final LinkedHashMap<Integer, Player> players;
    private final Square[][] board;

    private final List<Integer> turnOrder;
    private int playerToPlayIndex = 0;
    private boolean started = false;
    private Set<Integer> winnersIds = Collections.emptySet();
    private TurnInput lastPlayedTurn;

    public Game(UUID uuid, int nbPlayers, int width, int height) {
        // TODO limits
        this.id = uuid;
        this.nbPlayers = nbPlayers;
        this.width = width;
        this.height = height;

        players = new LinkedHashMap<>();
        board = new Square[height + 1][width + 1];
        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                board[y][x] = new Square(y != 0, x != 0);
            }
        }

        turnOrder = new ArrayList<>(nbPlayers);
    }
    public Game join(Player player) {
        if (started) {
            throw new RuntimeException("Can't join a game already started");
        }
        if (players.size() == nbPlayers) {
            throw new RuntimeException("Game full");
        }

        if (players.containsKey(player.getId())) {
            log.info("Player already in game → do nothing");
        } else {
            players.put(player.getId(), player);
            turnOrder.add(player.getId());
        }
        return this;
    }

    public Game leave(Integer playerId) {
        if (playerId != null && players.containsKey(playerId)) {
            if (started) {
                if (!isFinished()) {
                    leaveWhilePlaying(playerId);
                }
            } else {
                leaveBeforeStart(playerId);
            }
        }
        return this;
    }

    private void leaveWhilePlaying(Integer playerId) {
        if (getPlayerIdToPlay().equals(playerId)) {
            nextPlayerToPlay();
        }
        Integer playerIdToPlay = getPlayerIdToPlay();
        turnOrder.remove(playerId);
        // Fix playerToPlayIndex as turnOrder list has been modified
        playerToPlayIndex = turnOrder.indexOf(playerIdToPlay);
        // Only one player → automatic win
        if (turnOrder.size() == 1) {
            winnersIds.add(turnOrder.get(0));
        }

        players.get(playerId).setActive(false);
    }

    private void leaveBeforeStart(Integer playerId) {
        players.remove(playerId);
        turnOrder.remove(playerId);
    }

    public Game playTurn(TurnInput turnInput) {
        if (isFinished()) {
            throw new RuntimeException("Cannot play on a finished game");
        }
        if (!areAllPlayersHere()) {
            throw new RuntimeException("Cannot play before all player joined");
        }

        if (turnInput == null) {
            throw new RuntimeException("No turn data");
        }

        if (!Objects.equals(getPlayerIdToPlay(), turnInput.getPlayerId())) {
            throw new RuntimeException(String.format("Bad player to play, expected %d, actual %d", getPlayerIdToPlay(), turnInput.getPlayerId()));
        }
        if (!players.containsKey(turnInput.getPlayerId())) {
            throw new RuntimeException(String.format("Player ID %d does not play this game.", turnInput.getPlayerId()));
        }

        final Integer x = turnInput.getX();
        final Integer y = turnInput.getY();
        if (x < 0 || x > width || y < 0 || y > height) {
            throw new RuntimeException(String.format("Can't play at position (%d, %d) as it is outside of board (size %d x %d)", x, x, width, height));
        }

        started = true;

        board[y][x].play(turnInput.getVertical());

        lastPlayedTurn = turnInput;

        // Check if some square have been filled
        boolean hasFilledSquare  = fillSquareIfNotAlreadyFilled(x, y, turnInput.getPlayerId());
        boolean hasFilledNextVertical = turnInput.getVertical()
                && x + 1 <= width
                && fillSquareIfNotAlreadyFilled(x + 1, y, turnInput.getPlayerId());
        boolean hasFilledNextHorizontal = !turnInput.getVertical()
                && y + 1 <= height
                && fillSquareIfNotAlreadyFilled(x, y + 1, turnInput.getPlayerId());
        if (!hasFilledSquare && !hasFilledNextHorizontal && !hasFilledNextVertical) {
            nextPlayerToPlay();
        }

        checkWinners();

        return this;
    }

    private boolean allSquaresFilled() {
        return (width * height) == players.values().stream()
                .map(Player::getScore)
                .reduce(0, Integer::sum);
    }

    private boolean fillSquareIfNotAlreadyFilled(int x, int y, int playerId) {
        if (board[y][x].canBeFilled()
                && board[y][x].getFilledBy() == null
                && board[y][x].isVertical()
                && board[y][x].isHorizontal()
                && board[y][x - 1].isVertical()
                && board[y - 1][x].isHorizontal()
        ) {
            board[y][x].fillBy(playerId);
            players.get(playerId).increaseScore();
            return true;
        }
        return false;
    }

    private void checkWinners() {
        if (allSquaresFilled()) {
            players.values().stream()
                    .mapToInt(Player::getScore)
                    .max()
                    .ifPresent(maxScore -> winnersIds = players.values().stream()
                            .filter(player -> player.getScore() == maxScore)
                            .map(Player::getId)
                            .collect(Collectors.toSet()));
        } else {
            final int winningScore = (width * height) / 2;
            winnersIds = players.values().stream()
                    .filter(player -> player.getScore() > winningScore)
                    .map(Player::getId)
                    .collect(Collectors.toSet());

        }
    }

    public Integer getPlayerIdToPlay() {
        return (!areAllPlayersHere() || turnOrder.isEmpty() || isFinished())
                ? null
                : turnOrder.get(playerToPlayIndex);
    }

    private boolean areAllPlayersHere() {
        return players.size() == nbPlayers;
    }

    public boolean isFinished() {
        return !winnersIds.isEmpty();
    }

    private void nextPlayerToPlay() {
        playerToPlayIndex = (playerToPlayIndex + 1) % turnOrder.size();
    }

}
