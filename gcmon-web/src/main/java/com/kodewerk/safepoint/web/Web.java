package com.kodewerk.safepoint.web;

import com.kodewerk.safepoint.aggregator.Aggregator;
import com.kodewerk.safepoint.event.JVMEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Web extends AbstractVerticle {

    private final static Logger LOG = Logger.getLogger(Web.class.getName());

    private Map<JVMEvent, Integer> causes = new ConcurrentHashMap<>();

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final int BAD_REQUEST_ERROR_CODE = 400;

    @Override
    public void start(Future<Void> future) {

        vertx.eventBus().<Aggregator>consumer("web-inbox", msg -> {
            System.out.println(msg.body().toString());
        });

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.get("/").handler(getContext -> {
            getContext.request().response().sendFile("index.html");
        });

        router.post("/form").handler(BodyHandler.create().setMergeFormAttributes(true));
        router.post("/form")
                .handler(routingContext -> {

                    Set<FileUpload> fileUploadSet = routingContext.fileUploads();
                    fileUploadSet.stream().
                            map(FileUpload::uploadedFileName).
                            forEach( fileUpload -> {
                                vertx.eventBus().send("datasource", fileUpload);
                                System.out.println("Uploading -> " + fileUpload);
                            });
                    routingContext.response().end("<html><body>uploaded: " + "fileUpload.fileName()" + ".</body></html>");
                });

        httpServer.requestHandler(router::accept).listen(8080);

    }

    private static String buildContent(Map<JVMEvent, Integer> causes) {
        return "<ul><li>"
                + causes.entrySet().stream()
                .map(e -> buildEvent(e.getKey()) + ": " + e.getValue())
                .collect(Collectors.joining("</li><li>"))
                + "</li></ul>";
    }

    private static String buildEvent(JVMEvent event) {
        return event.toString() + ", " + event.getDuration() + ", " + event.getEventTime();
    }


}
