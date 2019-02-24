package com.kodewerk.safepoint;

import java.io.File;

public class SafepointVertxWebView {

    public static void main(String[] args) {
        SafepointVertxWebView view = new SafepointVertxWebView();
        view.openModel();
    }

    private SafepointVertxWebModel model;

    void openModel() {
        model = new SafepointVertxWebModel(new File("./logs/safepoint.log").toPath());
        model.load();
    }
}
