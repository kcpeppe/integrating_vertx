package com.jclarity.safepoint;

import com.jclarity.safepoint.event.DataSourceVerticlePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.JVMEventCodec;
import com.jclarity.safepoint.io.SafepointLogFile;
import com.jclarity.safepoint.web.WebView;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.nio.file.Path;

public class SafepointVertxWebModel extends AbstractSafepointVertxModel {

    public SafepointVertxWebModel(Path path) {
        super(path);
    }

    public void load() {
        Vertx vertx = Vertx.vertx();
        vertx.eventBus().registerDefaultCodec(JVMEvent.class, new JVMEventCodec());
        SafepointLogFile logFile = new SafepointLogFile(getSafepointLogFile());

        DataSourceVerticlePublisher<String> dataSourcePublisher = new DataSourceVerticlePublisher<>("parser-inbox");
        // This function trigger the reading of the log file and so start the event emission.
        Runnable readAction = () -> reload(dataSourcePublisher, logFile);
        setupTermination(vertx)
                .compose(v -> startWebView(vertx, readAction))
                .compose(v -> deployAggregator(vertx))
                .compose(v -> deployParser(vertx))
                .compose(v -> deployAndStartEventSource(vertx, dataSourcePublisher, readAction));
    }

    private Future<Void> startWebView(Vertx vertx, Runnable reload) {
        return new WebView().initialize(vertx, reload);
    }

    private void reload(DataSourceVerticlePublisher<String> dataSourcePublisher, SafepointLogFile log) {
        dataSourcePublisher.publish(log);
    }
}
