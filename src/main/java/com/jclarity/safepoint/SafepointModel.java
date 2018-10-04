package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.aggregator.ApplicationRuntimeSummary;
import com.jclarity.safepoint.aggregator.SafepointSummary;
import com.jclarity.safepoint.event.DataSourcePublisher;
import com.jclarity.safepoint.event.EventBus;
import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.EventSourceConsumer;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.io.DataSource;
import com.jclarity.safepoint.io.SafepointLogFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SafepointModel {

    private ExecutorService threadPool = Executors.newFixedThreadPool(3);
    final private Path safepointLogFile;
    private SafepointSummary safepointSummary;
    private ApplicationRuntimeSummary applicationRuntimeSummary;

    public SafepointModel( Path path) {
        this.safepointLogFile = path;
    }

    public void load() {

        AggregatorSet aggregators = new AggregatorSet();
        safepointSummary = new SafepointSummary();
        applicationRuntimeSummary = new ApplicationRuntimeSummary();
        aggregators.addAggregator( safepointSummary);
        aggregators.addAggregator(applicationRuntimeSummary);

        EventBus<JVMEvent> eventBus = new EventBus<>();
        EventSourceConsumer<JVMEvent> queryEngine = new EventSourceConsumer<>(eventBus,aggregators);
        EventBus<String> parserInbox = new EventBus<>();
        queryEngine.consume();

        EventSourcePublisher parserPublisher = new EventSourcePublisher(eventBus);
        parserPublisher.publish(parserInbox);
        SafepointLogFile logFile = new SafepointLogFile(safepointLogFile);
        DataSourcePublisher<String> dataSourcePublisher = new DataSourcePublisher<>(parserInbox);
        try {
            dataSourcePublisher.publish(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryEngine.awaitTermination();

    }

    private void startAggregators() {}

    private void startParser() {}

    private void load(DataSource<String> dataSource, EventSink<String> eventSink) {
        threadPool.submit( () -> {
            try {
                dataSource.stream().forEach(eventSink::accept);
            } catch (IOException ioe) {
            }
        });
    }

    public ApplicationRuntimeSummary getApplicationRuntimeSummary() {
        return applicationRuntimeSummary;
    }

    public SafepointSummary getSafepointSummary() {
        return safepointSummary;
    }
}
