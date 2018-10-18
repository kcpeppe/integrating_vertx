package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;
import io.vertx.core.AbstractVerticle;

import java.io.IOException;

public class DataSourceVerticlePublisher<T> extends AbstractVerticle implements DataSourcePublisher<T> {

    private String outbox;

    public DataSourceVerticlePublisher(String outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publish(DataSource<T> dataSource) {
        try {
            dataSource.stream().forEach(entry -> vertx.eventBus().publish(outbox, entry));
            vertx.eventBus().publish(outbox, dataSource.eosToken());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @Override
    public void awaitCompletion() {
        // Does nothing, non-blocking!
    }
}
