package com.deepbarankar.learning.vertx_stock_broker.assets;

import com.deepbarankar.learning.vertx_stock_broker.AbstractRestApiTest;
import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
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
public class TestAssetsRestApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Test
  void returns_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    // Vert.x WebClient is an asynchronous HTTP client that can be used for the testing purpose and also in the application itself.
    // It can be used as client for other REST APIs or also as proxy.
    WebClient client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));
    client.get("/assets")
      .send() // fluent API of the web client that returns a Future containing HTTP response object
      .onComplete(testContext.succeeding(response -> { // Unlike onSuccess and onFailure, success and failure are not defined for onComplete. So, we are using testContext.succeeding(). It returns an asynchronous result.
        // This is a handler that verifies our expectation of success.
        var json = response.bodyAsJsonArray();
        LOG.info("Response: {}", json);
        assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"FB\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}]", json.encode());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        assertEquals("my-value", response.getHeader("my-header"));
        testContext.completeNow();
      }));
  }
}
