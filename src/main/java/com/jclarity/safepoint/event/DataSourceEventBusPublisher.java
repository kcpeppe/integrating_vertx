package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;
import io.vertx.core.AbstractVerticle;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataSourceEventBusPublisher<T> extends AbstractVerticle implements DataSourcePublisher<T> {

    private EventBus<T> eventBus;
    private ExecutorService singleThread;

    public DataSourceEventBusPublisher(EventBus<T> eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void publish(DataSource<T> dataSource) {
        singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(() -> {
            try {
                dataSource.stream().forEach(eventBus::publish);
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        });
        singleThread.shutdown();
    }

    public void awaitCompletion() {
        try {
            singleThread.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}