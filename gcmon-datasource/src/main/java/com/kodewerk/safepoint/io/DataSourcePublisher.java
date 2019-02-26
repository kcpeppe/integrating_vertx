package com.kodewerk.safepoint.io;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class DataSourcePublisher extends AbstractVerticle {

    private String inbox;
    private String outbox;

    DeliveryOptions options = new DeliveryOptions();

    public DataSourcePublisher(String inbox, String outbox) {
        this.inbox = inbox;
        this.outbox = outbox;
        options.setCodecName(SafepointLogLineCodec.key());
    }

    public void publish(String sessionID, DataSource<String> dataSource) {
        try {
            vertx.eventBus().publish(outbox,new SafepointLogLine(sessionID,dataSource.startToken()), options);
            dataSource.stream().
                    map(entry -> new SafepointLogLine(sessionID, entry)).
                    forEach(logLine -> vertx.eventBus().publish(outbox, logLine, options));
            vertx.eventBus().publish(outbox, new SafepointLogLine(sessionID, dataSource.eosToken()), options);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().
                <String>consumer(inbox, message -> {
                    try {
                        String body = message.body().trim();
                        if (body.isEmpty()) {
                            return;
                        }
                        Path logfilePath = new File( body).toPath();
                        String sessionID = logfilePath.getFileName().toString();
                        SafepointLogFile logFile = new SafepointLogFile(new File( body).toPath());
                        this.publish(sessionID, logFile);
                    } catch (Exception t) {
                        System.out.println(t.getMessage());
                    }
                })
                .completionHandler(v -> future.complete());
    }
}
