package com.jclarity.safepoint.event;

import com.jclarity.safepoint.parser.EventConsumer;
import com.jclarity.safepoint.parser.SafepointParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//public class EventSourcePublisher {
public class EventSourcePublisher extends AbstractVerticle {

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

    // Vert.x
    private String inbox, outbox;
    private DeliveryOptions options = new DeliveryOptions().setCodecName("JVMEvent");

    public EventSourcePublisher(String inbox, String outbox, SafepointParser parser) {
        this.inbox = inbox;
        this.outbox = outbox;
        this.parser = parser;
        parser.setEventConsumer(event -> record(event));
    }

    public void record(JVMEvent event) {
        try {
            if (event != null) {
                vertx.eventBus().publish(outbox, event, options);
            } else {
                System.out.println("Event is null, ignored!");
            }
        } catch (Error t) {
            System.out.println(t.getMessage());
        }
    }

    private CountDownLatch deployed = new CountDownLatch(1);

    public void awaitDeployment() {
        try {
            deployed.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start() {
        vertx.eventBus().
                consumer(inbox, message -> {
                    try {
                        String body = ((String) message.body()).trim();
                        if (body.isEmpty()) return;
                        parser.parse(body);
                    } catch (Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });
        deployed.countDown();
    }
}
