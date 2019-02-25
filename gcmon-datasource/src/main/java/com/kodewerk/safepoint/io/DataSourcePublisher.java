package com.kodewerk.safepoint.io;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class DataSourcePublisher<T> extends AbstractVerticle {

    private String inbox;
    private String outbox;

    public DataSourcePublisher(String inbox, String outbox) {
        this.inbox = inbox;
        this.outbox = outbox;
    }

    public void publish(DataSource<T> dataSource) {
        try {
            dataSource.stream().forEach(entry -> {
                System.out.println(entry);
                vertx.eventBus().publish(outbox, entry);
            });
            vertx.eventBus().publish(outbox, dataSource.eosToken());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <String>consumer(inbox, message -> {
                    try {
                        String body = message.body().trim();
                        if (body.isEmpty()) {
                            return;
                        }
                        System.out.println("Streaming -> " + body);
                        SafepointLogFile logFile = new SafepointLogFile(new File( body).toPath());
                        this.publish((DataSource<T>) logFile);
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                    }
                })
                .completionHandler(v -> future.complete());
    }
}
