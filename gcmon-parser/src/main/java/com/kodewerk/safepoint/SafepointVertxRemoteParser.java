package com.kodewerk.safepoint;

import com.kodewerk.safepoint.event.EventSourcePublisher;
import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.event.JVMEventCodec;
import com.kodewerk.safepoint.parser.SafepointParser;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxRemoteParser {

    public static void main(String[] args) {
        new SafepointVertxRemoteParser().load();
    }

    Future<Void> deployParser(Vertx vertx) {
        Future<Void> future = Future.future();
        EventSourcePublisher publisher = new EventSourcePublisher("parser-inbox", "aggregator-inbox", new SafepointParser());
        vertx.deployVerticle(publisher, s -> future.handle(s.mapEmpty()));
        return future;
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
                deployParser(cluster.result());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
