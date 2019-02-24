package com.kodewerk.safepoint.event;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class JVMEventCodec implements MessageCodec<JVMEvent,JVMEvent> {

    @Override
    public void encodeToWire(Buffer buffer, JVMEvent jvmEvent) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(baos);
            stream.writeObject(jvmEvent);
            buffer.appendBytes(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JVMEvent decodeFromWire(int i, Buffer buffer) {
        byte[] serializedJVMEvent = buffer.getBytes(i, buffer.length());
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedJVMEvent);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (JVMEvent) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return null;
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
