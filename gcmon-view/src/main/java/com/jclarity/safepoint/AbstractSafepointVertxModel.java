package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import com.kodewerk.safepoint.event.DataSourceVerticlePublisher;
import com.kodewerk.safepoint.event.EventSourcePublisher;
import com.kodewerk.safepoint.parser.SafepointParser;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.nio.file.Path;

public abstract class AbstractSafepointVertxModel extends AbstractSafepointModel {

    private final Future<Void> done = Future.future();

    public AbstractSafepointVertxModel(Path path) {
        super(path);
    }

    /**
     * @return a future indicating when the processing has been completed.
     */
    public Future<Void> done() {
        return done;
    }

    Future<Void> setupTermination(Vertx vertx) {
        Future<Void> registered = Future.future();
        vertx.eventBus()
                .consumer("termination", msg -> done.tryComplete())
                .completionHandler(x -> registered.handle(x.mapEmpty()));
        return registered;
    }

    Future<Void> deployParser(Vertx vertx) {
        Future<Void> future = Future.future();
        EventSourcePublisher publisher = new EventSourcePublisher("parser-inbox", "aggregator-inbox", new SafepointParser());
        vertx.deployVerticle(publisher, s -> future.handle(s.mapEmpty()));
        return future;
    }

    Future<Void> deployAndStartEventSource(Vertx vertx, DataSourceVerticlePublisher<String> dataSourcePublisher, Runnable run) {
        Future<Void> future = Future.future();
        vertx.deployVerticle(dataSourcePublisher, s -> {
            if (s.failed()) {
                future.fail(s.cause());
            } else {
                future.complete();
                run.run();
            }
        });
        return future;
    }

    Future<Void> deployAggregator(Vertx vertx) {
        Future<Void> future = Future.future();
        AggregatorSet aggregators = new AggregatorSet("aggregator-inbox");
        aggregators.addAggregators(getSafepointSummary(),getApplicationRuntimeSummary());
        vertx.deployVerticle(aggregators, s -> future.handle(s.mapEmpty()));
        return future;
    }

}
