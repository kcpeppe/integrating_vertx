package com.kodewerk.safepoint;

import com.kodewerk.safepoint.io.ApplicationRuntimeSummaryCodec;
import com.kodewerk.safepoint.io.SafepointSummaryCodec;
import com.kodewerk.safepoint.web.Web;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxRemoteWeb {

    public static void main(String[] args) {
        SafepointVertxRemoteWeb view = new SafepointVertxRemoteWeb();
        view.load();
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerCodec(new ApplicationRuntimeSummaryCodec());
                cluster.result().eventBus().registerCodec(new SafepointSummaryCodec());
                deployView(cluster.result());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }

    private Future<Void> deployView(Vertx vertx) {
        Future<Void> future = Future.future();
        Web webView = new Web();
        vertx.deployVerticle(webView, s -> future.handle(s.mapEmpty()));
        return future;
    }

}
