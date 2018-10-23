package com.jclarity.safepoint.event;

import com.jclarity.safepoint.parser.SafepointParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventSourcePublisher extends AbstractVerticle {

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

    // Vert.x
    private String inbox;
    private String outbox;
    private DeliveryOptions options = new DeliveryOptions().setCodecName("JVMEvent");

    public EventSourcePublisher(String inbox, String outbox, SafepointParser parser) {
        this.inbox = inbox;
        this.outbox = outbox;
        this.parser = parser;
        parser.setEventConsumer(this::record);
    }

    public void record(JVMEvent event) {
        try {
            if (event != null) {
                // TODO publish event to outbox with options
            }
        } catch (Exception t) {
            System.out.println(t.getMessage());
        }
    }

    @Override
    public void start(Future<Void> future) {
        // TODO Register consumer (String) on inbox (process)
        // TODO set completion handler

    }

    private void process(Message<String> message) {
        try {
            String body = message.body().trim();
            if (body.isEmpty()) {
                return;
            }
            parser.parse(body);
        } catch (Exception t) {
            System.out.println(t.getMessage());
        }
    }
}
