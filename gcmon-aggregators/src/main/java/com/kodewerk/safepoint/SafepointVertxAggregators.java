package com.kodewerk.safepoint;

import com.kodewerk.safepoint.io.ApplicationRuntimeSummaryCodec;
import com.kodewerk.safepoint.io.SafepointSummaryCodec;
import com.kodewerk.safepoint.io.JVMEventCodec;
import com.kodewerk.safepoint.event.JVMEventAggregators;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxAggregators {

    public static void main(String[] args) {
        new SafepointVertxAggregators().load();
    }

    Future<Void> deployAggregators(Vertx vertx) {
        Future<Void> future = Future.future();
        JVMEventAggregators consumer = new JVMEventAggregators("aggregator-inbox", "web-inbox");
        vertx.deployVerticle(consumer, s -> future.handle(s.mapEmpty()));
        return future;
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerCodec(new JVMEventCodec());
                cluster.result().eventBus().registerCodec(new ApplicationRuntimeSummaryCodec());
                cluster.result().eventBus().registerCodec(new SafepointSummaryCodec());
                deployAggregators(cluster.result());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
