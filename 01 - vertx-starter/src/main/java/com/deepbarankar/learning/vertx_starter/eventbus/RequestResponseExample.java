package com.deepbarankar.learning.vertx_starter.eventbus;

import com.deepbarankar.learning.vertx_starter.eventloops.EventLoopExample;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExample extends AbstractVerticle {

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVerticle());
    vertx.deployVerticle(new ResponseVerticle());
  }

  static class RequestVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(RequestVerticle.class);
    static final String ADDRESS = "my.request.address"; // This is the address associated with the particular message that is being sent
    // Make sure the ADDRESS is the class name (ClassName.class.getName()). As that will clearly point to the Verticle that sent the message.

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      String message = "Hello World!";
      var eventBus = vertx.eventBus();
      LOG.debug("Sending: {}", message);
//      eventBus.request(ADDRESS, message, reply -> {
      eventBus.<String>request(ADDRESS, message, reply -> { // <String> defines the type of message that will be replied back
        // This callback will be called when the message arrives
        LOG.debug("Response: {}", reply.result().body()); // reply.result().body() is the message that the receiver replied after receiving the sent message.
        // If no one received or listened, it will give null
      });
    }
  }

  static class ResponseVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
//      vertx.eventBus().consumer(RequestVerticle.ADDRESS, message -> {
      vertx.eventBus().<String>consumer(RequestVerticle.ADDRESS, message -> { // adding <String> is not mandatory but is a good practice is to know the type of the message it will receive
        // This is a handler that receives the message
        LOG.debug("Received Message: {}", message.body()); // message is the reference to the message and body() helps to get the value at that reference
        message.reply("Received your message"); // Reply back to the same ADDRESS that the message came from. We can do additional tweaking using DeliveryOptions()
        // Default "new DeliveryOptions().setSendTimeout()" is set as 30 seconds
        // Lower the event bus timeout for faster replies.
      });
    }
  }
}
