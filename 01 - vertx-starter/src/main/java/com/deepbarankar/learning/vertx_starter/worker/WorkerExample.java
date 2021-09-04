package com.deepbarankar.learning.vertx_starter.worker;

import com.deepbarankar.learning.vertx_starter.eventloops.EventLoopExample;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerExample extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EventLoopExample.class);

  public static void main(String[] args) {
    final var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Running a Blocking Operation using Worker Verticle
    vertx.deployVerticle(new WorkerVerticle(),
      new DeploymentOptions()
        .setWorker(true) // The Verticle will be deployed on a Worker Thread Pool
        .setWorkerPoolSize(1) // Maximum number of Worker threads that can be allocated by this vertx object.
        .setWorkerPoolName("my-worker-verticle") // without this, the worker verticles deployed the vertx object will be deployed on the standard worker thread pool
      ); // my-worker-verticle-0

    startPromise.complete();

    // Running a Blocking Operation using vertx.executeBlocking()
    executeBlockingCode();
  }

  private void executeBlockingCode() {
    vertx.executeBlocking(event -> { // This is an event handler
      // This code will now be executed in the blocking thread
      LOG.debug("Executing blocking code"); // vert.x-worker-thread-0
      try {
        Thread.sleep(5000);
        event.complete(); // The event callback successfully completed.
        // It is possible that the code in the worker thread failed and returns an error [instead of throwing an exception].
        // We can simulate this in the following way
//        event.fail("Force Fail!");
      } catch (InterruptedException e) {
        LOG.debug("Failed: ", e);
        event.fail(e); // The event callback failed.
      }
      // event is actually an asynchronous callback that can be complete [.complete()] or fail [.fail()]
    }, result -> { // This is also an asynchronous handler.
      // When the blocking code is done executing then the code should return on the correct event loop.
      // This second handler will exactly achieve it.
      if(result.succeeded()) { // event.complete() was executed
        LOG.debug("Blocking call done"); // vert.x-eventloop-thread-0
      } else { // event.fail() was executed
        LOG.debug("Blocking call failed due to: ", result.cause());
      }
    });
    // This will schedule the Blocking operation on the verticle thread pool. So, it's fine to run blocking operations here.
    /*
    Workign of vertx.executeBlocking():
    1. The first part is a handler (event) which is executed on the Verticle Thread 0 (vert.x-worker-thread-0)
    2. The second part is, the result is executes on the event loop thread 0 (vert.x-eventloop-thread-0)
    That means, if you have some code in your verticle that is blocking the event loop, make sure to use vertx.executeBlocking() to schedule the blocking call on the worker thread.
    With this we can gurantee that the event loop will work as expected.
     */
  }
}
