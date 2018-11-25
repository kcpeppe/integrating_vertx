package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;
import io.vertx.core.AbstractVerticle;

public class DataSourceVerticlePublisher<T> extends AbstractVerticle implements DataSourcePublisher<T> {

    private String outbox;

    public DataSourceVerticlePublisher(String outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publish(DataSource<T> dataSource) {
        // TODO Publish each item from data source to outbox
        // TODO publish to outbox datasource.eosToken()
    }

    @Override
    public void awaitCompletion() {
        // Does nothing, non-blocking!
    }
}
