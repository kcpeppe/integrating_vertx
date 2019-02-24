package com.kodewerk.safepoint.io;

import io.vertx.core.AbstractVerticle;

import java.io.IOException;

public class DataSourcePublisher<T> extends AbstractVerticle {

    private String outbox;

    public DataSourcePublisher(String outbox) {
        this.outbox = outbox;
    }

    public void publish(DataSource<T> dataSource) {
        try {
            dataSource.stream().forEach(entry -> vertx.eventBus().publish(outbox, entry));
            vertx.eventBus().publish(outbox, dataSource.eosToken());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
}
