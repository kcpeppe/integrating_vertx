package com.kodewerk.safepoint.io;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class SafepointLogLineCodec extends VertxBaseCodec<SafepointLogLine> implements MessageCodec<SafepointLogLine,SafepointLogLine> {

    private static final String KEY = "SafepointLogLine";

    public static String key() {
        return KEY;
    }
    @Override
    public void encodeToWire(Buffer buffer, SafepointLogLine logLine) {
        super.encodeToWire(buffer, logLine);
    }

    @Override
    public SafepointLogLine decodeFromWire(int i, Buffer buffer) {
        return super.decodeFromWire(i, buffer);
    }

    @Override
    public SafepointLogLine transform(SafepointLogLine logLine) {
        return logLine;
    }

    @Override
    public String name() {
        return KEY;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
