package com.jclarity.safepoint;

import com.jclarity.safepoint.event.DataSourceVerticlePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import com.jclarity.safepoint.io.SafepointLogFile;
import io.vertx.core.Vertx;

import java.nio.file.Path;

public class SafepointVertxModel extends AbstractSafepointVertxModel {

    public SafepointVertxModel(Path path) {
        super(path);
    }

    public void load() {
        // TODO - Create vert.x
        Vertx vertx = null;
        initVertxCodec(vertx);

        SafepointLogFile logFile = new SafepointLogFile(getSafepointLogFile());
        DataSourceVerticlePublisher<String> dataSourcePublisher = new DataSourceVerticlePublisher<>("parser-inbox");
        // This function trigger the reading of the log file and so start the event emission.
        Runnable readAction = () -> load(dataSourcePublisher, logFile);

        // TODO Starting sequence
        // TODO setupTermination
        // TODO aggregator
        // TODO parser
        // TODO deploy and start the event source

    }

    private void load(DataSourceVerticlePublisher<String> dataSourcePublisher, SafepointLogFile log) {
        dataSourcePublisher.publish(log);
    }

    private void initVertxCodec(Vertx vertx) {
        vertx.eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
    }
}
