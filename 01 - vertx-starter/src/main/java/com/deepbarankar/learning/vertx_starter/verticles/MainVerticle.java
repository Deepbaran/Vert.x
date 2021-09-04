package com.deepbarankar.learning.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {

  // Creating a Logger Object for Logging the Outputs.
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    // Creating entry point
    final var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
//    System.out.println("Start " + getClass().getName()); // This will print the name of the Verticle.
    LOG.debug("Start {}", getClass().getName());

    // As this MainVerticle is a parent of the VerticleA and VerticleB, so it must deploy the children Verticles.
    vertx.deployVerticle(new VerticleA());
    vertx.deployVerticle(new VerticleB());

    // To scale the VerticleN, we need to deploy it multiple times.
    // As we are deploying multiple instances, we cannot pass an object.
    // We need to pass name of the Class. And Vert.x will internally create the object of this class.
    // DeploymentOptions().setInstances(4) means that the Verticle, VerticleN is deployed 4 times.
    // As each verticle runs on their own separate thread, so each and every instance of VerticleN that is deployed will run on different threads of their own.
    vertx.deployVerticle(VerticleN.class.getName(),
      new DeploymentOptions() // Add new options while deploying the Verticle
        .setInstances(4) //Sets how many instances of the verticle needs to be deployed
        // .setConfig() lets us add some configuration to the Verticle while deploying.
        // With this JSON object can be passed to a new Verticle during initialization.
        .setConfig(new JsonObject() // Creates / Configures a JSON Object. This is the common way to operate with an JSON Object in the Vert.x world.
          // .put() lets us add fields to the JSON object.
          .put("id", UUID.randomUUID().toString()) // "id" field having random UUID / ID in the JSON object.
          // Every instance of this verticle will have the same UUID.
          // It is not possible to pass different kind of configs even if the Verticle is deployed multiple times.
          .put("name", VerticleN.class.getSimpleName()) // "name" field having the Verticle name
        )
    );

    startPromise.complete();
  }
}
