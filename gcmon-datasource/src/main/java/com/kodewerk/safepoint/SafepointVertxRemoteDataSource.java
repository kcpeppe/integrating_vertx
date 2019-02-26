package com.kodewerk.safepoint;

import com.kodewerk.safepoint.io.DataSourcePublisher;
import com.kodewerk.safepoint.io.SafepointLogLineCodec;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxRemoteDataSource {

    public static void main(String[] args) {
        new SafepointVertxRemoteDataSource().load();
    }

    Future<Void> deployAndStartEventSource(Vertx vertx) {
        Future<Void> future = Future.future();
        DataSourcePublisher publisher = new DataSourcePublisher("datasource", "parser-inbox");
        vertx.deployVerticle(publisher, s -> future.handle(s.mapEmpty()));
        vertx.eventBus().registerCodec(new SafepointLogLineCodec());
        return future;
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                deployAndStartEventSource(cluster.result());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
