package com.deepbarankar.learning.vertx_stock_broker;

import com.deepbarankar.learning.vertx_stock_broker.assets.AssetsRestApi;
import com.deepbarankar.learning.vertx_stock_broker.quotes.QuaotesRestApi;
import com.deepbarankar.learning.vertx_stock_broker.config.*;
import com.deepbarankar.learning.vertx_stock_broker.watchlist.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends DBPool {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved Configuration: {}", configuration);
        startHttpServerAndAttachRoutes(startPromise, configuration);
      });

//    startHttpServerAndAttachRoutes(startPromise);
  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, final BrokerConfig configuration) {
    // One Pool for each Rest Api Verticle
    // Create DB Pool
    final Pool db = DBPool.createDbPoolPgsql(configuration, vertx); // Pool is the postgres implementation of the Reactive Client

    // Crating a Router
    final Router restApi = Router.router(vertx);

    // Setting up a failure handler for our Router to check if HTTP request fails or not
    restApi.route()
      .handler(BodyHandler.create()) // BodyHandler is registered to the parent router. We can chain multiple handlers together. We can customize the BodyHandler
      .failureHandler(handleFailure());

    // Preparing the "/assets" route for the router
    AssetsRestApi.attach(restApi, db);

    // Preparing the "/assets/{quotes}" route for the router
    QuaotesRestApi.attach(restApi, db);

    WatchListRestApi.attach(restApi, db);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if(errorContext.response().ended()) {
        // Ignore completed response
        // Client stopped the request
        return;
      }
      LOG.error("Route Error: ", errorContext.failure());
      errorContext
        .response()
        .setStatusCode(500) // We are using status code 500 because we do not know what happened
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
