package com.jclarity.safepoint.event;

import com.jclarity.safepoint.parser.SafepointParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventSourcePublisher {

    private ExecutorService singleThread;
    private SafepointParser parser;

    public EventSourcePublisher(EventBus<JVMEvent> outbox) {
        parser = new SafepointParser(event -> outbox.publish(event));
    }

    public void publish(EventBus<String> inbox) {
        singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(() -> {
            String event;
            while ( (event = inbox.read()) != null) {
                parser.parse(event);
            }
        });
        singleThread.shutdown();
    }
}
