package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.SafepointSummary;
import com.kodewerk.safepoint.io.AggregatorCodec;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class AggregatorModel {

    public AggregatorModel() {
        configure();
    }

    private void configure() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerDefaultCodec(ApplicationRuntimeSummary.class, new AggregatorCodec<>());
                cluster.result().eventBus().registerDefaultCodec(SafepointSummary.class, new AggregatorCodec<>());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }


}
