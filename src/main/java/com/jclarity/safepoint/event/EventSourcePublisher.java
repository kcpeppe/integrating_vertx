package com.jclarity.safepoint.event;

import com.jclarity.safepoint.parser.SafepointParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    public void awaitCompletion(int waitTime, TimeUnit units) {
        try {
            singleThread.awaitTermination(waitTime, units);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        singleThread.shutdownNow();
    }
}
