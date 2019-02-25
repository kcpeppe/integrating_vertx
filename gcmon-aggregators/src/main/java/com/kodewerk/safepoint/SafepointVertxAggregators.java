package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.AggregatorSet;
import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.SafepointSummary;
import com.kodewerk.safepoint.event.JVMEventAggregators;
import com.kodewerk.safepoint.io.ApplicationRuntimeSummaryCodec;
import com.kodewerk.safepoint.io.JVMEventCodec;
import com.kodewerk.safepoint.io.SafepointSummaryCodec;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxAggregators {

    public static void main(String[] args) {
        AggregatorSet aggregators = new AggregatorSet();
        aggregators.addAggregator(new ApplicationRuntimeSummary());
        aggregators.addAggregator(new SafepointSummary());
        new SafepointVertxAggregators().load(aggregators);
    }

    Future<Void> deployAggregators(Vertx vertx, AggregatorSet aggregators) {
        Future<Void> future = Future.future();
        JVMEventAggregators consumer = new JVMEventAggregators("aggregator-inbox", "web-inbox", aggregators);
        vertx.deployVerticle(consumer, s -> future.handle(s.mapEmpty()));
        return future;
    }

    public void load(AggregatorSet aggregators) {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerCodec(new JVMEventCodec());
                cluster.result().eventBus().registerCodec(new ApplicationRuntimeSummaryCodec());
                cluster.result().eventBus().registerCodec(new SafepointSummaryCodec());
                deployAggregators(cluster.result(), aggregators);
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
