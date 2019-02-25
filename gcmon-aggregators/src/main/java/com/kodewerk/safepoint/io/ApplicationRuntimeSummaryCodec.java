package com.kodewerk.safepoint.io;

import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.SafepointSummary;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;


public class ApplicationRuntimeSummaryCodec extends VertxBaseCodec<ApplicationRuntimeSummary> implements MessageCodec<ApplicationRuntimeSummary,ApplicationRuntimeSummary> {

    @Override
    public void encodeToWire(Buffer buffer, ApplicationRuntimeSummary message) {
        super.encodeToWire(buffer,message);
    }

    @Override
    public ApplicationRuntimeSummary decodeFromWire(int i, Buffer buffer) {
        return super.decodeFromWire(i,buffer);
    }

    @Override
    public ApplicationRuntimeSummary transform(ApplicationRuntimeSummary message) {
        return message;
    }

    @Override
    public String name() {
        return "ApplicationRuntimeSummary";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
