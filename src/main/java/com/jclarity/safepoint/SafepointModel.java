package com.jclarity.safepoint;

import com.jclarity.safepoint.event.DataSourcePublisher;
import com.jclarity.safepoint.event.EventSourceConsumer;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.EventSink;
import com.jclarity.safepoint.event.EventBus;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.io.DataSource;
import com.jclarity.safepoint.io.SafepointLogFile;
import com.jclarity.safepoint.parser.SafepointParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class SafepointModel {

    private ExecutorService threadPool = Executors.newFixedThreadPool(3);
    final private Path safepointLogFile;

    public SafepointModel( Path path) {
        this.safepointLogFile = path;
    }

    public void load() {

        EventBus<JVMEvent> eventBus = new EventBus<>();
        EventSink<JVMEvent> eventSink = event -> System.out.println(event);
        EventSourceConsumer<JVMEvent> consumer = new EventSourceConsumer<>(eventBus,eventSink);
        EventBus<String> parserInbox = new EventBus<>();
        consumer.consume();

        EventSourcePublisher parserPublisher = new EventSourcePublisher(eventBus);
        parserPublisher.publish(parserInbox);
        SafepointLogFile logFile = new SafepointLogFile(safepointLogFile);
        DataSourcePublisher<String> dataSourcePublisher = new DataSourcePublisher<>(parserInbox);
        try {
            dataSourcePublisher.publish(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startAggregators() {}

    private void startParser() {}

    private void load(DataSource<String> dataSource, EventSink<String> eventSink) {
        threadPool.submit( () -> {
            try {
                dataSource.stream().forEach(eventSink::accept);
            } catch (IOException ioe) {
            }
        });
    }

    public static void main(String[] args) {
        SafepointModel model = new SafepointModel(new File("logs/safepoint.log").toPath());
        model.load();
    }

}
