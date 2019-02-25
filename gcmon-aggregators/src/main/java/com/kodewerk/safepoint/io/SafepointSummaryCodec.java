package com.kodewerk.safepoint.io;

import com.kodewerk.safepoint.aggregator.SafepointSummary;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;


public class SafepointSummaryCodec extends VertxBaseCodec<SafepointSummary> implements MessageCodec<SafepointSummary,SafepointSummary> {

    @Override
    public void encodeToWire(Buffer buffer, SafepointSummary message) {
        super.encodeToWire(buffer,message);
    }

    @Override
    public SafepointSummary decodeFromWire(int i, Buffer buffer) {
        return super.decodeFromWire(i,buffer);
    }

    @Override
    public SafepointSummary transform(SafepointSummary message) {
        return message;
    }

    @Override
    public String name() {
        return "SafepointSummary";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
