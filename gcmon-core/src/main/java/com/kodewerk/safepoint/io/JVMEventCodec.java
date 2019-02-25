package com.kodewerk.safepoint.io;

import com.kodewerk.safepoint.event.JVMEvent;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class JVMEventCodec extends VertxBaseCodec<JVMEvent> implements MessageCodec<JVMEvent,JVMEvent> {

    @Override
    public void encodeToWire(Buffer buffer, JVMEvent jvmEvent) {
        super.encodeToWire(buffer,jvmEvent);
    }

    @Override
    public JVMEvent decodeFromWire(int i, Buffer buffer) {
        return super.decodeFromWire(i,buffer);
    }

    @Override
    public JVMEvent transform(JVMEvent jvmEvent) {
        return jvmEvent;
    }

    @Override
    public String name() {
        return "JVMEvent";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
