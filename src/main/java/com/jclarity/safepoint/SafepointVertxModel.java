package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.event.DataSourceVerticlePublisher;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import com.jclarity.safepoint.io.SafepointLogFile;
import com.jclarity.safepoint.parser.SafepointParser;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.nio.file.Path;

public class SafepointVertxModel extends AbstractSafepointModel {

    private final Future<Void> done = Future.future();

    public SafepointVertxModel(Path path) {
        super(path);
    }

    /**
     * @return a future indicating when the processing has been completed.
     */
    Future<Void> done() {
        return done;
    }

    public void load() {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
        SafepointLogFile logFile = new SafepointLogFile(getSafepointLogFile());

        setupTermination(vertx)
                .compose(v -> deployAggregator(vertx))
                .compose(v -> deployParser(vertx))
                .compose(v -> deployAndStartEventSource(vertx, logFile));
    }

    private Future<Void> setupTermination(Vertx vertx) {
        Future<Void> registered = Future.future();
        vertx.eventBus()
                .consumer("termination", msg -> {
                    done.complete();
                    vertx.close();
                })
                .completionHandler(x -> registered.handle(x.mapEmpty()));
        return registered;
    }

    private Future<Void> deployParser(Vertx vertx) {
        Future<Void> future = Future.future();
        EventSourcePublisher publisher = new EventSourcePublisher("parser-inbox", "aggregator-inbox", new SafepointParser());
        vertx.deployVerticle(publisher, s -> future.handle(s.mapEmpty()));
        return future;
    }

    private Future<Void> deployAndStartEventSource(Vertx vertx, SafepointLogFile log) {
        Future<Void> future = Future.future();
        DataSourceVerticlePublisher<String> dataSourcePublisher = new DataSourceVerticlePublisher<>("parser-inbox");
        vertx.deployVerticle(dataSourcePublisher, s -> {
            if (s.failed()) {
                future.fail(s.cause());
            } else {
                future.complete();
                dataSourcePublisher.publish(log);
            }
        });
        return future;
    }

    private Future<Void> deployAggregator(Vertx vertx) {
        Future<Void> future = Future.future();
        AggregatorSet aggregators = new AggregatorSet("aggregator-inbox");
        aggregators.addAggregator(getSafepointSummary());
        aggregators.addAggregator(getApplicationRuntimeSummary());
        vertx.deployVerticle(aggregators, s -> future.handle(s.mapEmpty()));
        return future;
    }
}
