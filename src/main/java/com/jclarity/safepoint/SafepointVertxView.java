package com.jclarity.safepoint;

import javafx.scene.control.TabPane;

import java.io.File;

public class SafepointVertxView extends SafepointSharedView {

    public static void main(String[] args) {
        launch();
    }

    SafepointVertxModel model;
    void openModel(TabPane tabPane) {
        model = new SafepointVertxModel(new File("./logs/safepoint.log").toPath());
        model.load();
        buildViews(tabPane, model.getSafepointSummary(), model.getApplicationRuntimeSummary());
    }
}
