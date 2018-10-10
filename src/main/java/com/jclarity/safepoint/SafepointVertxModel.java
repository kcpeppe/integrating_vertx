package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.event.DataSourceVerticlePublisher;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import com.jclarity.safepoint.io.SafepointLogFile;
import com.jclarity.safepoint.parser.SafepointParser;
import io.vertx.core.Vertx;

import java.nio.file.Path;

public class SafepointVertxModel extends AbstractSafepointModel {

    public SafepointVertxModel(Path path) {
        super(path);
    }

    public void load() {

        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec( JVMEvent.class, new JVMEventCodec());

        // deploy the queries
        AggregatorSet aggregators = new AggregatorSet("aggregator-inbox");
        aggregators.addAggregator( getSafepointSummary());
        aggregators.addAggregator(getApplicationRuntimeSummary());
        vertx.deployVerticle(aggregators);
        aggregators.awaitDeployment();

        // deploy the parser
        EventSourcePublisher publisher = new EventSourcePublisher("parser-inbox", "aggregator-inbox",new SafepointParser());
        vertx.deployVerticle(publisher);
        publisher.awaitDeployment();

        // deploy the event source
        SafepointLogFile logFile = new SafepointLogFile(getSafepointLogFile());
        DataSourceVerticlePublisher<String> dataSourcePublisher = new DataSourceVerticlePublisher<>("parser-inbox");
        vertx.deployVerticle(dataSourcePublisher);
        dataSourcePublisher.awaitDeployment();

        // start the stream...
        dataSourcePublisher.publish(logFile);

        // make sure it's all done
        aggregators.awaitCompletion();
        vertx.close();
    }
}
