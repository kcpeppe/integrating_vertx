package com.jclarity.safepoint;

import com.jclarity.safepoint.aggregator.AggregatorSet;
import com.jclarity.safepoint.aggregator.ApplicationRuntimeSummary;
import com.jclarity.safepoint.aggregator.SafepointSummary;
import com.jclarity.safepoint.event.DataSourceEventBusPublisher;
import com.jclarity.safepoint.event.EventBus;
import com.jclarity.safepoint.event.EventSourceConsumer;
import com.jclarity.safepoint.event.EventSourcePublisher;
import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.io.SafepointLogFile;

import java.nio.file.Path;

public abstract class AbstractSafepointModel {

    final private Path safepointLogFile;
    private SafepointSummary safepointSummary;
    private ApplicationRuntimeSummary applicationRuntimeSummary;

    public AbstractSafepointModel(Path path) {
        this.safepointLogFile = path;
        safepointSummary = new SafepointSummary();
        applicationRuntimeSummary = new ApplicationRuntimeSummary();
    }

    abstract public void load();

    public Path getSafepointLogFile() { return this.safepointLogFile; }

    public ApplicationRuntimeSummary getApplicationRuntimeSummary() {
        return applicationRuntimeSummary;
    }

    public SafepointSummary getSafepointSummary() {
        return safepointSummary;
    }
}
