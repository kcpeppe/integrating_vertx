package com.jclarity.safepoint;

import javafx.scene.control.TabPane;
import java.io.File;

public class SafepointView extends SafepointSharedView {

    public static void main(String[] args) {
        launch();
    }

    private SafepointEventBusModel model;
    void openModel(TabPane tabPane) {
        model = new SafepointEventBusModel(new File("./logs/safepoint.log").toPath());
        model.load();
        buildViews(tabPane, model.getSafepointSummary(), model.getApplicationRuntimeSummary());
    }
}
