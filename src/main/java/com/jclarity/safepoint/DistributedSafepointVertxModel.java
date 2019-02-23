package com.jclarity.safepoint;

import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.nio.file.Path;

public class DistributedSafepointVertxModel extends AbstractSafepointVertxModel {

    public DistributedSafepointVertxModel(Path path) {
        super(path);
    }

    public void load() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(options, cluster -> {
            if (cluster.succeeded()) {
                cluster.result().eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
                setupTermination(cluster.result())
                        .compose(v -> deployAggregator(cluster.result()));
            } else {
                System.out.println("Cluster up failed: " + cluster.cause());
            }
        });
    }
}
