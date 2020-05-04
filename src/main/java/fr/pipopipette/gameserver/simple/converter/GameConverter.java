package fr.pipopipette.gameserver.simple.converter;

import fr.pipopipette.gameserver.simple.game.Game;
import fr.pipopipette.gameserver.simple.io.GameOutput;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GameConverter {

    @Inject BoardConverter boardConverter;

    public GameOutput toGameOutput(Game game) {
        return GameOutput.builder()
                .id(game.getId())
                .nbPlayers(game.getNbPlayers())
                .width(game.getWidth())
                .height(game.getHeight())
                .players(game.getPlayers())
                .board(boardConverter.toBoardOutput(game.getBoard()))
                .playerIdToPlay(game.getPlayerIdToPlay())
                .started(game.isStarted())
                .finished(game.isFinished())
                .winnersIds(game.getWinnersIds())
                .lastPlayedTurn(game.getLastPlayedTurn())
                .build();
    }

}
