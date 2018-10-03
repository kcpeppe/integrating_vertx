package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventSourceConsumer<T> {

    private EventBus<T> eventBus;
    private EventSink<T> eventSink;
    ExecutorService singleThread = Executors.newSingleThreadExecutor();


    public EventSourceConsumer(EventBus<T> eventBus, EventSink<T> eventSink) {
        this.eventBus = eventBus;
        this.eventSink = eventSink;
    }

    public void consume() {
        singleThread.submit(() -> {
            T event;
            while ( (event = eventBus.read()) != null) {
                eventSink.accept(event);
            }
        });
        singleThread.shutdown();
    }
}
