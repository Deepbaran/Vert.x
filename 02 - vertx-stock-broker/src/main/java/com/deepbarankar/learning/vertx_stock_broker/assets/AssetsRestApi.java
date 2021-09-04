package com.deepbarankar.learning.vertx_stock_broker.assets;

import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  // Defining list of valid lists that can be requested [Created a In memory store]
  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA");

  public static void attach(Router parent, Pool db) {
    // Creating a Route for GET request
    parent.get("/assets").handler(new GetAssetsHandler());
    parent.get("/pg/assets").handler(new GetAssetsFromDatabaseHandler(db));
  }
}
