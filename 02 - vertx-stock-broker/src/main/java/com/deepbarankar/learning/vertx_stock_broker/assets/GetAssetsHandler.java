package com.deepbarankar.learning.vertx_stock_broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(final RoutingContext context) {
    // context -> { // handler() is a async handler
      // context means the Routing context that gives us access to request related information
      // context is a routing context

      // Creating a JsonArray response to send through this GET route
      final JsonArray response = new JsonArray();

      // We are mapping all Stings in ASSETS to a new Asset Object, and we are adding each one of them to response
      AssetsRestApi.ASSETS.stream().map(Asset::new).forEach(response::add);

//      response
//        .add(new Asset("AAPL"))
//        .add(new Asset("AMZN"))
//        .add(new Asset("NFLX"))
//        .add(new Asset("TSLA"));

      LOG.info("Path {} responds with  {}", context.normalizedPath(), response.encode());

      // Returning a response [context -> response in context of the GET request; response() -> returns an HTTP response & end() -> pass a buffer or string]
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON) // Adding a Header of content type APPLICATION_JSON
        .putHeader("my-header", "my-value") // Custom Header
        .end(response.toBuffer());
    //});
  }
}
