package com.kodewerk.safepoint;

import com.kodewerk.safepoint.aggregator.ApplicationRuntimeSummary;
import com.kodewerk.safepoint.aggregator.SafepointSummary;

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

    public abstract void load();

    public Path getSafepointLogFile() {
        return this.safepointLogFile;
    }

    public ApplicationRuntimeSummary getApplicationRuntimeSummary() {
        return applicationRuntimeSummary;
    }

    public SafepointSummary getSafepointSummary() {
        return safepointSummary;
    }
}
