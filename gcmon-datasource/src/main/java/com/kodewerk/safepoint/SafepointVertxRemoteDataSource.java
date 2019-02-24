package com.kodewerk.safepoint;

import com.kodewerk.safepoint.io.DataSourcePublisher;
import com.kodewerk.safepoint.io.SafepointLogFile;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.File;
import java.nio.file.Path;

public class SafepointVertxRemoteDataSource {

    public static void main(String[] args) {
        new SafepointVertxRemoteDataSource().load();
    }

    Future<Void> deployAndStartEventSource(Vertx vertx, DataSourcePublisher<String> dataSourcePublisher, Runnable run) {
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

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                Path path = new File("./logs/safepoint.log").toPath();
                SafepointLogFile logFile = new SafepointLogFile(path);

                DataSourcePublisher<String> dataSourcePublisher = new DataSourcePublisher<>("parser-inbox");
                // This function trigger the reading of the log file and so start the event emission.
                Runnable readAction = () -> dataSourcePublisher.publish(logFile);
                deployAndStartEventSource(cluster.result(), dataSourcePublisher, readAction);
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
