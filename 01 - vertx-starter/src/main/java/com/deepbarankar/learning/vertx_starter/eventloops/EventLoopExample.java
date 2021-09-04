package com.deepbarankar.learning.vertx_starter.eventloops;

import com.deepbarankar.learning.vertx_starter.verticles.MainVerticle;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EventLoopExample.class);

  public static void main(String[] args) {
    // Vert.x Verticles are deployed on event loops
    // While creating a new Vertx object we can configure it with some options using VertxOptions()
    final var vertx = Vertx.vertx(
      new VertxOptions()
        .setMaxEventLoopExecuteTime(500) // Sets the value of max event loop execute time
        .setMaxEventLoopExecuteTimeUnit(TimeUnit.MILLISECONDS) // Sets the unit of setMaxEventLoopExecuteTime.
        // Here the max event loop execute time is 500 milliseconds
        // This is the value that sets how long the Event Loop can be blocked, before the blocked thread checker will throw a warning.
        // The default time is 2 seconds.
        .setBlockedThreadCheckInterval(1) // Sets the interval in which the blocked thread is checked, to see if it's still blocked or not.
        .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS) // Sets the unit of setBlockedThreadCheckInterval.
        // Here the interval in which the blocked thread is checked, to see if it's still blocked or not is set to 1 second
        // So, every 1 second, the thread is checked if it still blocked or not.
        // The default time is 1 second.
        .setEventLoopPoolSize(2) // Sets the maximum number of Event Loop Threads this vertx instance can use. [NOT RECOMMENDED EXCEPT SOME RARE USE CASES]
        // If there are limited resources or if for some reason, we need to restrict the number of event loops, then we can use it.
    );

//    vertx.deployVerticle(new EventLoopExample());
    vertx.deployVerticle(EventLoopExample.class.getName(),
      new DeploymentOptions()
        .setInstances(4)
      );
    // If there are multiple Verticle instances, then they are all deployed on different event loops.
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.debug("Start {}", getClass().getName());
    startPromise.complete();

    // NEVER BLOCK EVENT LOOP
    // Do not do this inside a Verticle as it is a blocking code and we should never add a blocking code inside a verticle.
    Thread.sleep(5000); // Thread of this Verticle will be sleeping / blocked for 5000 milliseconds.
    // We will get a warning
  }
}
