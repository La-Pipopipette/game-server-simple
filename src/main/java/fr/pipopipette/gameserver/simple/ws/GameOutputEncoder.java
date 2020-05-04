package fr.pipopipette.gameserver.simple.ws;

import fr.pipopipette.gameserver.simple.io.GameOutput;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class GameOutputEncoder implements Encoder.Text<GameOutput> {

    private static final Jsonb JSONB = JsonbBuilder.create();


    @Override
    public String encode(GameOutput gameOutput) {
        return JSONB.toJson(gameOutput);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Nothing to do
    }

    @Override
    public void destroy() {
        // Nothing to do
    }
}
