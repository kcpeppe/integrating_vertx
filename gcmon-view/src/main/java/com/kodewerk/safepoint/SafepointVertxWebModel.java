package com.kodewerk.safepoint;

import com.kodewerk.safepoint.event.JVMEvent;
import com.kodewerk.safepoint.io.JVMEventCodec;
import com.kodewerk.safepoint.io.DataSourcePublisher;
import com.kodewerk.safepoint.io.SafepointLogFile;
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

        DataSourcePublisher<String> dataSourcePublisher = new DataSourcePublisher<>("parser-inbox");
        // This function trigger the reading of the log file and so start the event emission.
        Runnable readAction = () -> reload(dataSourcePublisher, logFile);
        setupTermination(vertx)
                .compose(v -> startWebView(vertx, readAction))
                .compose(v -> deployAggregator(vertx))
                .compose(v -> deployParser(vertx))
                .compose(v -> deployAndStartEventSource(vertx, dataSourcePublisher, readAction));
    }

    private Future<Void> startWebView(Vertx vertx, Runnable reload) {
//        return new WebView().initialize(vertx, reload);
        return null;
    }

    private void reload(DataSourcePublisher<String> dataSourcePublisher, SafepointLogFile log) {
        dataSourcePublisher.publish(log);
    }
}
