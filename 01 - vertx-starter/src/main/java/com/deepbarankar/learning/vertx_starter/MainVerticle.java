package com.deepbarankar.learning.vertx_starter;

import com.deepbarankar.learning.vertx_starter.worker.WorkerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle { // Each Verticle is extending from AbstractVerticle and then we can deploy it with Vertx instance.

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) { // Run this method to start the application
    // Creating Vert.x object | This is now the main control section.
    final var vertx = Vertx.vertx(); //Use final, as there can only be one Vertx object.
    // Normally only one Vertx object is needed in each application.
    // Make sure not to create multiple instances on Vertx object in an application
    // and always rely on the instance references from the different Verticles.

    // Deploy MainVerticle | Start the application / Deploy the Application
    vertx.deployVerticle(new MainVerticle());
    // Vertx is deploying this application internally and the method start() is called.
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception { // Start the application after deployed
    // A new HTTP Server is created
    // The vertx instance here is coming from AbstractVerticle Class.
    // So each verticle that we are deploying has a reference to the vertx instance
    // A new server is crated and then vertx is heavily using a builder pattern.
    // So, when the vertx.createHTTPServer() is called, immediately afterwards, .requestHandler() is called and in there we have a callback.
    // The first parameter is request and then for the request, .response() is called, then .putHeader() and .end().
    // .putHeader() is creating a HTTP response with content-type text/plain.
    // And .end() is printing the message. It is responsible to return the correct response.
    // After this, the .listen() method is called on port 8888 and this starting an HTTP server.
    // When the HTTP server is started, a message is logged. If not, it fails.
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello World!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete(); // This startPromise is an asynchronous callback.
        System.out.println("HTTP server started on port 8888"); // This message is logged when the HTTP server successfully runs on port 8888
      } else {
        startPromise.fail(http.cause());
      }
    });
    vertx.setPeriodic(500, id -> {
      LOG.debug("Redeployed...");
    });
  }
}
