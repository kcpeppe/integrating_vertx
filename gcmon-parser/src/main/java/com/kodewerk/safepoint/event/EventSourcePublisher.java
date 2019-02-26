package com.kodewerk.safepoint.event;

import com.kodewerk.safepoint.io.SafepointLogLine;
import com.kodewerk.safepoint.parser.SafepointParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventSourcePublisher extends AbstractVerticle {

    private SafepointParser parser;

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
                vertx.eventBus().publish(outbox, event, options);
            }
        } catch (Exception t) {
            System.out.println(t.getMessage());
        }
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <SafepointLogLine>consumer(inbox, message -> {
                    try {
                        SafepointLogLine body = message.body();
                        if (body == null) {
                            return;
                        }
                        parser.parse(body);
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                    }
                })
                .completionHandler(v -> future.complete());
    }
}
