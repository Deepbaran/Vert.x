package com.deepbarankar.learning.vertx_stock_broker;

import com.deepbarankar.learning.vertx_stock_broker.config.ConfigLoader;
import com.deepbarankar.learning.vertx_stock_broker.db.config.FlywayMigration;
import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
//  public static final int PORT = 8888;

  public static void main(String[] args) {
//     Setting up a system property
//    System.setProperty(ConfigLoader.SERVER_PORT, "8888");

    final Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled: ", error);
    });

//    vertx.deployVerticle(new MainVerticle(), ar -> {
//      if(ar.failed()) {
//        LOG.error("Failed to deploy: ", ar.cause());
//      } else {
//        LOG.info("Deployed {}!", MainVerticle.class.getName());
//      }
//    });

    vertx.deployVerticle(new MainVerticle())
      .onFailure(err -> LOG.error("Failed to deploy: ", err))
      .onSuccess(id -> LOG.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new VersionInfoVerticle())
      .onFailure(startPromise::fail)
      .onSuccess(id -> LOG.info("Deployed {} with id {}", VersionInfoVerticle.class.getSimpleName(), id))
      .compose(next -> migrateDatabase())
      .onFailure(startPromise::fail)
      .onSuccess(id -> LOG.info("Migrated db schema to latest version!"))
      .compose(next -> deployRestApiVerticle(startPromise));

  }

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig());
      });
  }

  private Future<String> deployRestApiVerticle(Promise<Void> startPromise) {
    // Scaling the Application by deploying it on multiple servers
    return vertx.deployVerticle(RestApiVerticle.class.getName(),
        new DeploymentOptions().setInstances(processors())
        // As the Web Server start up is now in one separate verticle, so we can scale it easily by creating multiple instances
      ) // deployVerticle returns a Future
      .onFailure(startPromise::fail)
      .onSuccess(id -> { // id of the deployed verticle. Each verticle gets a unique id when it is deployed.
        LOG.info("Deployed {} with id {}", RestApiVerticle.class.getSimpleName(), id);
        startPromise.complete();
      });
  }

  private int processors() {
    // Runtime.getRuntime().availableProcessors() -> Number of available CPU processors available for the JVM.
    // In cloud environments it is possible to set the CPU credits for containers and this could lead to issues depending on what Java version you are using.
    // Scaling it for maximum resource efficiency
    return Math.max(1, Runtime.getRuntime().availableProcessors()); // By setting 1 as minimum we ensure that at least 1 REST API Verticle is deployed.
  }
}
