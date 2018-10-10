package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.aggregator.ApplicationRuntimeSummary;
import com.jclarity.safepoint.aggregator.SafepointSummary;
import com.jclarity.safepoint.event.DataSourceEventBusPublisher;
import com.jclarity.safepoint.event.DataSourceVerticlePublisher;
import com.jclarity.safepoint.event.EventBus;
import com.jclarity.safepoint.event.EventSourceConsumer;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import com.jclarity.safepoint.io.SafepointLogFile;
import com.jclarity.safepoint.parser.SafepointParser;
import io.vertx.core.Vertx;

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
        DataSourceEventBusPublisher<String> dataSourcePublisher = new DataSourceEventBusPublisher<>(parserInbox);
        dataSourcePublisher.publish(logFile);
        dataSourcePublisher.awaitCompletion();
        queryEngine.awaitTermination();

    }

    public void loadVertx() {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec( JVMEvent.class, new JVMEventCodec());
        AggregatorSet aggregators = new AggregatorSet("aggregator-inbox");
        safepointSummary = new SafepointSummary();
        applicationRuntimeSummary = new ApplicationRuntimeSummary();
        aggregators.addAggregator( safepointSummary);
        aggregators.addAggregator(applicationRuntimeSummary);
        vertx.deployVerticle(aggregators);
        aggregators.awaitDeployment();

        EventSourcePublisher publisher = new EventSourcePublisher("parser-inbox", "aggregator-inbox",new SafepointParser());
        vertx.deployVerticle(publisher);
        publisher.awaitDeployment();

        SafepointLogFile logFile = new SafepointLogFile(safepointLogFile);
        DataSourceVerticlePublisher<String> dataSourcePublisher = new DataSourceVerticlePublisher<>("parser-inbox");
        vertx.deployVerticle(dataSourcePublisher);
        dataSourcePublisher.awaitDeployment();
        dataSourcePublisher.publish(logFile);
        dataSourcePublisher.awaitCompletion();
        aggregators.awaitCompletion();
    }

    public ApplicationRuntimeSummary getApplicationRuntimeSummary() {
        return applicationRuntimeSummary;
    }

    public SafepointSummary getSafepointSummary() {
        return safepointSummary;
    }
}
