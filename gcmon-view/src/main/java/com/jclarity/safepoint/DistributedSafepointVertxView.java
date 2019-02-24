package com.kodewerk.safepoint;

import javafx.application.Platform;
import javafx.scene.control.TabPane;

import java.io.File;

public class DistributedSafepointVertxView extends SafepointSharedView {

    public static void main(String[] args) {
        launch();
    }

    private DistributedSafepointVertxModel model;

    void openModel(TabPane tabPane) {
        model = new DistributedSafepointVertxModel(new File("./logs/safepoint.log").toPath());
        model.load();
        // Wait until the model is computed.
        model.done().setHandler(x ->
                Platform.runLater(() ->
                        buildViews(tabPane, model.getSafepointSummary(), model.getApplicationRuntimeSummary())
                ));
    }
}
