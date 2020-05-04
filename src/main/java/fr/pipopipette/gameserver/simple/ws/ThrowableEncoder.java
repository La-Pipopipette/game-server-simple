package fr.pipopipette.gameserver.simple.ws;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ThrowableEncoder implements Encoder.Text<Throwable> {

    private static final Jsonb JSONB = JsonbBuilder.create();

    @Override
    public String encode(Throwable throwable) {
        return JSONB.toJson(throwable);
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
