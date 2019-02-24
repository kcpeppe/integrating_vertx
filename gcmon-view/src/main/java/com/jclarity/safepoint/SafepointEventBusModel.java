package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import com.kodewerk.safepoint.event.DataSourceEventBusPublisher;
import com.kodewerk.safepoint.event.EventBus;
import com.kodewerk.safepoint.event.EventSourceConsumer;
import com.kodewerk.safepoint.event.EventSourcePublisher;
import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.io.SafepointLogFile;

import java.nio.file.Path;

public class SafepointEventBusModel extends AbstractSafepointModel {

    public SafepointEventBusModel(Path path) {
        super(path);
    }

    public void load() {
        AggregatorSet aggregators = new AggregatorSet();
        aggregators.addAggregator( getSafepointSummary());
        aggregators.addAggregator( getApplicationRuntimeSummary());

        EventBus<JVMEvent> eventBus = new EventBus<>();
        EventSourceConsumer<JVMEvent> queryEngine = new EventSourceConsumer<>(eventBus,aggregators);
        EventBus<String> parserInbox = new EventBus<>();
        queryEngine.consume();

        EventSourcePublisher parserPublisher = new EventSourcePublisher(eventBus);
        parserPublisher.publish(parserInbox);
        SafepointLogFile logFile = new SafepointLogFile( getSafepointLogFile());
        DataSourceEventBusPublisher<String> dataSourcePublisher = new DataSourceEventBusPublisher<>(parserInbox);
        dataSourcePublisher.publish(logFile);
        dataSourcePublisher.awaitCompletion();
        queryEngine.awaitTermination();
    }
}
