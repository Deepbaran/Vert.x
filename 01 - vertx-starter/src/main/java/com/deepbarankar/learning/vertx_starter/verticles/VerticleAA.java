package com.deepbarankar.learning.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleAA extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VerticleAA.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.debug("Start {}", getClass().getName());

    //Complete the promise
    startPromise.complete(); // This is an Asynchronous callback that must be completed to signal that the Verticle was started.
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception { // Undeploys or stops a Verticle when manually called
    LOG.debug("Stop {}", getClass().getName());
    stopPromise.complete(); // Signals that the Verticle was undeployed.
  }
}
