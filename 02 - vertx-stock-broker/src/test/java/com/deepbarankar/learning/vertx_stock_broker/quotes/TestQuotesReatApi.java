package com.deepbarankar.learning.vertx_stock_broker.quotes;

import com.deepbarankar.learning.vertx_stock_broker.AbstractRestApiTest;
import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesReatApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Test
  void returns_quotes_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = getWebClient(vertx);
    client.get("/quotes/AMZN") // Testing for AMZN asset
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals("{\"name\":\"AMZN\"}", json.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void returns_not_found_unknown_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient client = getWebClient(vertx);
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response: {}", json);
        assertEquals(404, response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWN not available!\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
        testContext.completeNow();
      }));
  }

  private WebClient getWebClient(Vertx vertx) {
    return WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
  }
}
