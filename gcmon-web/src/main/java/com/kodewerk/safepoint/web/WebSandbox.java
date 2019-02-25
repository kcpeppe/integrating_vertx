package com.kodewerk.safepoint.web;

import com.kodewerk.safepoint.event.JVMEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebSandbox extends AbstractVerticle {

    private final static Logger LOG = Logger.getLogger(WebSandbox.class.getName());

    private Map<JVMEvent, Integer> causes = new ConcurrentHashMap<>();


    private EventBus eventBus;

    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final int BAD_REQUEST_ERROR_CODE = 400;

    @Override
    public void start() {

        HttpServer httpServer = vertx.createHttpServer();
        eventBus = vertx.eventBus();
        Router router = Router.router(vertx);

        router.get("/").handler(getContext -> {
            getContext.request().response().sendFile("index.html");
        });

        router.post("/form").handler(BodyHandler.create().setMergeFormAttributes(true));
        router.post("/form")
                .handler(routingContext -> {

                    Set<FileUpload> fileUploadSet = routingContext.fileUploads();
                    Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
                    while (fileUploadIterator.hasNext()){
                        FileUpload fileUpload = fileUploadIterator.next();

                        // To get the uploaded file do
                        Buffer uploadedFile = vertx.fileSystem().
                                readFileBlocking(fileUpload.uploadedFileName());
                        System.out.println(uploadedFile.length());

                        // Uploaded File Name
                        try {
                            String fileName = URLDecoder.decode(fileUpload.fileName(), "UTF-8");
                            System.out.println(fileName);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        // Use the Event Bus to dispatch the file now
                        // Since Event Bus does not support POJOs by default so we need to create a MessageCodec implementation
                        // and provide methods for encode and decode the bytes
                    }
                });

        httpServer.requestHandler(router::accept).listen(8080);
    }

    private JsonObject getRequestParams(MultiMap params){

        JsonObject paramMap = new JsonObject();
        for( Map.Entry entry: params.entries()){
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            if(value instanceof List){
                value = (List<String>) entry.getValue();
            }
            else{
                value = entry.getValue();
            }
            paramMap.put(key, value);
        }
        return paramMap;
    }

    private static JsonObject getQueryMap(String query)
    {
        String[] params = query.split("&");
        JsonObject map = new JsonObject();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = "";
            try {
                value = URLDecoder.decode(param.split("=")[1], "UTF-8");
            } catch (Exception e) {
            }
            map.put(name, value);
        }
        return map;
    }

    //    @Override
//    public void start(Future<Void> future) {
//        Router router = Router.router(vertx);
//
//        vertx.eventBus().<Aggregator>consumer("web-inbox", msg -> {
//            System.out.println(msg.body().toString());
////            JVMEvent event = msg.body();
////            causes.put(event, causes.getOrDefault(event, 0) + 1);
//        });
//
//        router.route("/events/*").handler(routingContext -> {
//            HttpServerResponse response = routingContext.response();
//            final String content = buildContent(causes);
//            response.putHeader("content-type", "text/html").end("<h1>Events</h1><br>" + content);
//
//        });
//
//        vertx.createHttpServer()
//                .requestHandler(router::accept)
//                .listen(8080, result -> {
//                    if (result.succeeded()) {
//                        LOG.info("HTTP server running on port 8080");
//                        future.complete();
//                    } else {
//                        LOG.severe(() -> "Could not start a HTTP server: " + result.cause());
//                        future.fail(result.cause());
//                    }
//                });
//    }

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

/*


  private EventBus eventBus;

  private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final int BAD_REQUEST_ERROR_CODE = 400;


  @Override
  public void start() {

    HttpServer httpServer = vertx.createHttpServer();
    eventBus = vertx.eventBus();
    Router router = Router.router(vertx);

    router.get("/").handler(getContext -> {
      getContext.request().response().sendFile("index.html");
    });

    router.post("/form").handler(BodyHandler.create().setMergeFormAttributes(true));
    router.post("/form")
      .handler(routingContext -> {

        Set<FileUpload> fileUploadSet = routingContext.fileUploads();
        Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
        while (fileUploadIterator.hasNext()){
          FileUpload fileUpload = fileUploadIterator.next();

          // To get the uploaded file do
          Buffer uploadedFile = vertx.fileSystem().readFileBlocking(fileUpload.uploadedFileName());

          // Uploaded File Name
          try {
            String fileName = URLDecoder.decode(fileUpload.fileName(), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }

          // Use the Event Bus to dispatch the file now
          // Since Event Bus does not support POJOs by default so we need to create a MessageCodec implementation
          // and provide methods for encode and decode the bytes
        }


      });

    httpServer.requestHandler(router::accept).listen(8080);
  }


    private JsonObject getRequestParams(MultiMap params){

      JsonObject paramMap = new JsonObject();
      for( Map.Entry entry: params.entries()){
        String key = (String)entry.getKey();
        Object value = entry.getValue();
        if(value instanceof List){
          value = (List<String>) entry.getValue();
        }
        else{
          value = (String) entry.getValue();
        }
        paramMap.put(key, value);
      }
      return paramMap;
    }

    private static JsonObject getQueryMap(String query)
    {
      String[] params = query.split("&");
      JsonObject map = new JsonObject();
      for (String param : params) {
        String name = param.split("=")[0];
        String value = "";
        try {
          value = URLDecoder.decode(param.split("=")[1], "UTF-8");
        } catch (Exception e) {
        }
        map.put(name, value);
      }
      return map;
    }
}
 */
