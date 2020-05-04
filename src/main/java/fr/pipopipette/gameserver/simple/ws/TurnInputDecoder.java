package fr.pipopipette.gameserver.simple.ws;

import fr.pipopipette.gameserver.simple.io.TurnInput;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class TurnInputDecoder implements Decoder.Text<TurnInput> {

    private static final Jsonb JSONB = JsonbBuilder.create();

    @Override
    public TurnInput decode(String turnInputStr) {
        return JSONB.fromJson(turnInputStr, TurnInput.class);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
