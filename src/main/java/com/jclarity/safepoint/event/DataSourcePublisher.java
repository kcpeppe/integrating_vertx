package com.jclarity.safepoint.event;

import com.jclarity.safepoint.io.DataSource;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataSourcePublisher<T> {

    private EventBus<T> eventBus;
    private ExecutorService singleThread;

    public DataSourcePublisher(EventBus<T> eventBus) {
        this.eventBus = eventBus;
    }

    public void publish(DataSource<T> dataSource) {
        singleThread = Executors.newSingleThreadExecutor();
        singleThread.submit(() -> {
            try {
                dataSource.stream().forEach(eventBus::publish);
            } catch(IOException ioe) {
                System.out.println(ioe.getMessage());
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

}
