package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.event.DataSourceEventBusPublisher;
import com.jclarity.safepoint.event.EventBus;
import com.jclarity.safepoint.event.EventSourceConsumer;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.io.SafepointLogFile;

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
