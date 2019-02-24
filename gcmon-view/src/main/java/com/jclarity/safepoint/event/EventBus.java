package com.kodewerk.safepoint.event;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventBus<E> implements EventSink<E> {

    private LinkedBlockingQueue<E> transferQueue = new LinkedBlockingQueue<>();

    public EventBus() {
    }

    public void publish(E event) {
        transferQueue.offer(event);
    }

    public E read() {
        try {
            return transferQueue.poll( 1000L,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void accept(E event) {
        publish(event);
    }
}
