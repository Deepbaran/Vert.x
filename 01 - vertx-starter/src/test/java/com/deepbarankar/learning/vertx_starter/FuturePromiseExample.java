package com.deepbarankar.learning.vertx_starter;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class) // Defining VertxExtension to the class to enhance the test with vertx functionality.
// It will help to get access to the vertx instance when the test is executed.
public class FuturePromiseExample {

  private static final Logger LOG = LoggerFactory.getLogger(FuturePromiseExample.class);

  @Test
  void promise_sucess(Vertx vertx, VertxTestContext context) {
    // With vertx we have access to event loops, event pass and also timers.
    final Promise<String> promise = Promise.promise();
    LOG.debug("Start"); // "Test worker" Thread
    vertx.setTimer(500, id -> { // The timer will be executed Asynchronously.
      promise.complete("Success"); // "vert.x-eventloop-thread-0" Thread
      LOG.debug("Success");
      context.completeNow(); // Needed to complete the Unit test
    });
    LOG.debug("End"); // "Test worker" Thread

//    23:16:51.787 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - Start
//    23:16:51.796 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - End
//    23:16:52.304 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Success
  }

  @Test
  void promise_fail(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOG.debug("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Failed!"));
      LOG.debug("Failed");
      context.completeNow(); // Test will complete but the promise will fail
    });
    LOG.debug("End");

//    23:24:01.202 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - Start
//    23:24:01.212 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - End
//    23:24:01.727 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Failed
  }

  @Test
  void future_success(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOG.debug("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      LOG.debug("Timer done.");
    });
    final Future<String> future = promise.future();
    future
      .onSuccess(result -> { // This will be called immediately after the promise is executed
      LOG.debug("Result: {}", result);
      context.completeNow();
    })
      .onFailure(context::failNow);
    LOG.debug("End");

//    23:30:53.601 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - Start
//    23:30:53.610 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - End
//    23:30:54.117 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Result: Success
//    23:30:54.119 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Timer done.
  }

  @Test
  void future_failure(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOG.debug("Start");
    vertx.setTimer(500, id -> {
      promise.fail(new RuntimeException("Failed!"));
      LOG.debug("Timer done.");
    });
    final Future<String> future = promise.future();
    future
      .onSuccess(context::failNow)
      .onFailure(error -> {
        LOG.debug("Error: {}", error.getMessage());
        context.completeNow();
      });
    LOG.debug("End");

//    23:35:33.233 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - Start
//    23:35:33.240 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - End
//    23:35:33.753 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Error: Failed!
//    23:35:33.754 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Timer done.
  }

  @Test
  void future_map(Vertx vertx, VertxTestContext context) {
    final Promise<String> promise = Promise.promise();
    LOG.debug("Start");
    vertx.setTimer(500, id -> {
      promise.complete("Success");
      LOG.debug("Timer done.");
    });
    final Future<String> future = promise.future();
    future
      .map(asString -> { // Changing the type
        LOG.debug("Map String to JsonObject");
        return new JsonObject().put("key", asString); // Changing the String Object to JsonObject
      })
      .map(jsonObject -> new JsonArray().add(jsonObject))
      .onSuccess(result -> {
        LOG.debug("Result: {} of type {}", result, result.getClass().getSimpleName());
        context.completeNow();
      })
      .onFailure(context::failNow);
    LOG.debug("End");

    /*
    We can chain .map() multiple times.
    .mapEmpty() is used to change the Object to a Void Object
   */

//    23:44:44.204 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - Start
//    23:44:44.214 [Test worker] DEBUG c.d.l.v.FuturePromiseExample - End
//    23:44:44.712 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Map String to JsonObject
//    23:44:44.925 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Result: [{"key":"Success"}] of type JsonArray
//    23:44:44.926 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Timer done.
  }

  @Test
  void future_coordination(Vertx vertx, VertxTestContext context) {
    vertx.createHttpServer()
      .requestHandler(request -> LOG.debug("{}", request))
      .listen(10_000)
      .compose(server -> {
        LOG.info("Another task");
        return Future.succeededFuture(server); // Returns a Future success immediately
      })
      .compose(server -> {
        LOG.info("Even More");
        return Future.succeededFuture(server);
      })
      .onFailure(context::failNow)
      .onSuccess(server -> {
        LOG.debug("Server started on port {}", server.actualPort());
        context.completeNow();
      });
    // We can see that everything is chained together with fluent API and the return types are futures.
    // This allows the coordination of asynchronous tasks in a sequential order.
    // .listen() returns a Future.
    // The chaining of .compose() can go on unlimited times to do various tasks. This is how we can do multiple tasks in a sequential order.
    // Don't execute blocking codes here as all the chained tasks are performed on the same thread.

//    23:54:53.045 [vert.x-eventloop-thread-0] INFO  c.d.l.v.FuturePromiseExample - Another task
//    23:54:53.049 [vert.x-eventloop-thread-0] INFO  c.d.l.v.FuturePromiseExample - Even More
//    23:54:53.051 [vert.x-eventloop-thread-0] DEBUG c.d.l.v.FuturePromiseExample - Server started on port 10000
  }

  @Test
  void future_composition(Vertx vertx, VertxTestContext context) {
    var one = Promise.<Void>promise();
    var two = Promise.<Void>promise();
    var three = Promise.<Void>promise();

    var futureOne = one.future();
    var futureTwo = two.future();
    var futureThree = three.future();

    CompositeFuture.all(futureOne, futureTwo, futureThree)
      .onFailure(context::failNow)
      .onSuccess(result -> {
        LOG.debug("Success");
        context.completeNow();
      });

    // Complete futures
    vertx.setTimer(500, id -> {
      one.complete();
      two.complete();
//      three.fail("Three Failed!");
      three.complete();
    });

    /*
    .all() - all futures need to succeed for onSuccess [MOST USED]
    .any() - at least one future needs to succeed
    .join() - Before reporting the results, all futures have to be returned, and then it has the same behavior as .all()
     */
  }

}
