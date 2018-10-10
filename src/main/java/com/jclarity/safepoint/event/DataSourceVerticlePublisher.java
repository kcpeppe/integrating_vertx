package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;
import io.vertx.core.AbstractVerticle;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataSourceVerticlePublisher<T> extends AbstractVerticle implements DataSourcePublisher<T> {


    private String outbox;

    public DataSourceVerticlePublisher(String outbox) {
        this.outbox = outbox;
    }

    @Override
    public void publish(DataSource<T> dataSource) {
            try {
                dataSource.stream().forEach(entry -> vertx.eventBus().publish(outbox, entry));
            } catch(IOException ioe) {
                System.out.println(ioe.getMessage());
            }
    }

    CountDownLatch completed = new CountDownLatch(1);
    CountDownLatch started = new CountDownLatch(1);
    public void awaitCompletion() {
        try {
            completed.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void awaitDeployment() {
        try {
            started.await();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {
        started.countDown();
    }
}
