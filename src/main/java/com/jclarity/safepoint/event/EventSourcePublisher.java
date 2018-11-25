package com.jclarity.safepoint.event;

import com.jclarity.safepoint.parser.SafepointParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventSourcePublisher {

    private SafepointParser parser;

    public EventSourcePublisher(EventBus<JVMEvent> outbox) {
        parser = new SafepointParser(outbox::publish);
    }

    public void publish(EventBus<String> inbox) {
        ExecutorService singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(() -> {
            String event;
            while ((event = inbox.read()) != null) {
                parser.parse(event);
            }
        });
        singleThread.shutdown();
    }
}
