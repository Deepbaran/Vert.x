package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import com.deepbarankar.learning.vertx_stock_broker.AbstractRestApiTest;
import com.deepbarankar.learning.vertx_stock_broker.assets.Asset;
import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListReatApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Test
  void adds_and_returns_watchList_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = getWebClient(vertx);
    var accountId = UUID.randomUUID();
    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(body())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response PUT: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
//        testContext.completeNow(); // Second handler will complete it
      }))
      .compose(next -> { // We can compose tasks after asynchronous callbacks. The composed task returns a Future. Adding another client request.
        client.get("/account/watchlist/" + accountId.toString()) // Creating a GET request after the PUT request
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            LOG.info("Response GET: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
            testContext.completeNow();
          }));
        return Future.succeededFuture(); // Returning it to show that the Future succeeded and the second request/task worked
      });
  }

  @Test
  void adds_and_deletes_watchList_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = getWebClient(vertx);
    var accountId = UUID.randomUUID();
    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(body())
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response PUT: {}", json);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
        assertEquals(200, response.statusCode());
//        testContext.completeNow(); // Second handler will complete it
      }))
      .compose(next -> {
        client.delete("/account/watchlist/" + accountId.toString())
          .send()
          .onComplete(testContext.succeeding(response -> {
            var json = response.bodyAsJsonObject();
            LOG.info("Response DELETE: {}", json);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", json.encode());
            assertEquals(200, response.statusCode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });
  }

  private WebClient getWebClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }

  private JsonObject body() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA"))
    )
      .toJsonOnject();
  }
}
