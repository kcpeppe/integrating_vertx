package com.kodewerk.safepoint;

import javafx.application.Platform;
import javafx.scene.control.TabPane;

import java.io.File;

public class SafepointVertxView extends SafepointSharedView {

    public static void main(String[] args) {
        launch();
    }

    private SafepointVertxModel model;

    void openModel(TabPane tabPane) {
        model = new SafepointVertxModel(new File("./logs/safepoint.log").toPath());
        model.load();
        // Wait until the model is computed.
        model.done().setHandler(x ->
                Platform.runLater(() ->
                    buildViews(tabPane, model.getSafepointSummary(), model.getApplicationRuntimeSummary())
        ));
    }
}
