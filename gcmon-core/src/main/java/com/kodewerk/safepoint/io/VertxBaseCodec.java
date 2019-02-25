package com.kodewerk.safepoint.io;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class VertxBaseCodec<T> {

    public void encodeToWire(Buffer buffer, T message) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(baos);
            stream.writeObject(message);
            buffer.appendBytes(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public T decodeFromWire(int i, Buffer buffer) {
        byte[] serializedMessage = buffer.getBytes(i, buffer.length());
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return null;
    }
}
