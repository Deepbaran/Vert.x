package com.deepbarankar.learning.vertx_starter.eventbus.customcodec;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPongExample extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(PingPongExample.class);

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVerticle(), logOnError());
    vertx.deployVerticle(new PongVerticle(), logOnError());
  }

  private static Handler<AsyncResult<String>> logOnError() {
    return ar -> { // This is an asynchronous handler that handles the exception and gets triggered after the start() of PingVerticle and PongVerticle gets executed
      if (ar.failed()) {
        LOG.error("err: ", ar.cause());
      }
    };
  }

  static class PingVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(PingVerticle.class);
    static final String ADDRESS = PingVerticle.class.getName();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      // Defining a Codec
      var eventBus = vertx.eventBus().registerDefaultCodec(Ping.class, new LocalMessageCodec<>(Ping.class)); // ClassName, MessageCode
      // Need to register the default codec only once in the application before the message is sent.
      // Register it in the verticle where the message is being sent to keep an overview of the message.
      // Trying to register the codec multiple times will throw an exception.
//      var eventBus = vertx.eventBus();
      final Ping message = new Ping("Hello World", true); // It is an immutable object with two fields.
      LOG.debug("Sending: {}", message);
      eventBus.<Pong>request(ADDRESS, message, reply -> {
        if(reply.failed()) { // Getting the reply failed
          LOG.error("Failed: ", reply.cause());
          return;
        }
        LOG.debug("Response: {}", reply.result().body());
      });
      startPromise.complete(); // We make sure that the default codec is registered before the start completes.
    }
  }

  static class PongVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(PongVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      vertx.eventBus().registerDefaultCodec(Pong.class, new LocalMessageCodec<>(Pong.class)); // A new message codec for each object
      vertx.eventBus().<Ping>consumer(PingVerticle.ADDRESS, message -> {
        LOG.debug("Received Message: {}", message.body());
        message.reply(new Pong(0));
      }).exceptionHandler(error -> { // Exception while receiving the message
        LOG.error("Error: ", error);
      });
    }
  }
}
