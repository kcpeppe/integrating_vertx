package com.jclarity.safepoint.web;

import com.jclarity.safepoint.event.JVMEvent;
import com.jclarity.safepoint.event.Safepoint;
import com.jclarity.safepoint.event.SafepointCause;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WebView {

    private Map<SafepointCause, Integer> causes = new HashMap<>();

    public Future<Void> initialize(Vertx vertx, Runnable reload) {
        Future<Void> future = Future.future();
        Router router = Router.router(vertx);

        vertx.eventBus().<JVMEvent>consumer("aggregator-inbox", msg -> {
            JVMEvent event = msg.body();
            if (event instanceof Safepoint) {
                SafepointCause cause = ((Safepoint) event).getSafepointCause();
                causes.put(cause, causes.getOrDefault(cause, 0) + 1);
            }
        });

        vertx.setPeriodic(100, x -> {
            JsonObject json = new JsonObject();
            causes.forEach((c, i) -> json.put(c.name(), i));
            vertx.eventBus().publish("safepoints", json);
        });

        BridgeOptions options = new BridgeOptions();
        options.setOutboundPermitted(Collections.singletonList(new PermittedOptions()
                .setAddress("safepoints")));
        router.route("/reload").handler(rc -> {
            causes.forEach((c, v) -> causes.put(c, 0));
            // Just here to clear the graph to better see the event flow
            vertx.setTimer(1000, x -> {
                reload.run();
                rc.response().end("Replaying events");
            });
        });
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options));
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080, ar -> future.handle(ar.mapEmpty()));
        return future;
    }


}
