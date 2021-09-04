package com.deepbarankar.learning.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleA extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(VerticleA.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.debug("Start {}", getClass().getName());

    // Deploying the children Verticles of VerticleA
    vertx.deployVerticle(new VerticleAA(), whenDeployed -> {
      LOG.debug("Deployed {}", VerticleAA.class.getName());
      // This is the stop method that can be used to to clean up the resources properly when an application shuts down.
      // This will be executed once VerticleAA is undeployed.
      // This is an asynchronous callback, and when this one completes, we can do vertx.undeploy() and we can manually undeploy a verticle.
      vertx.undeploy(whenDeployed.result()); // This will call the overloaded stop method in the VerticleAA
      //Manually undeploying verticle. For this we need the ID of the verticle.
      // whenDeployed.result() is returning the ID of the verticle, VerticleAA()
    });
    vertx.deployVerticle(new VerticleAB(), whenDeployed -> {
      LOG.debug("Deployed {}", VerticleAB.class.getName());
    });

    //Complete the promise
    startPromise.complete(); // This is an Asynchronous callback that must be completed to signal that the Verticle was started.
  }
}

/*
In a real application, there is no need to undeploy a verticle manually.
But often there is a need to clean up resources like database connections or others before the application shuts down.
 */
