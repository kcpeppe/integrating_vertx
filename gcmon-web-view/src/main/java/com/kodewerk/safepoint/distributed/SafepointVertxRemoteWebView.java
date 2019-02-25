package com.kodewerk.safepoint.distributed;

import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.event.JVMEventCodec;
import com.kodewerk.safepoint.web.WebView;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class SafepointVertxRemoteWebView {

    public static void main(String[] args) {
        SafepointVertxRemoteWebView view = new SafepointVertxRemoteWebView();
        view.load();
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
                deployView(cluster.result());
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }

    private Future<Void> deployView(Vertx vertx) {
        Future<Void> future = Future.future();
        WebView webView = new WebView();
        vertx.deployVerticle(webView, s -> future.handle(s.mapEmpty()));
        return future;
    }

}
