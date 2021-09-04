package com.deepbarankar.learning.vertx_stock_broker.quotes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
  private final Map<String, Quote> cachedQuotes;

  public GetQuoteHandler(Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(final RoutingContext context) {
    // context -> {
      final String assetParam = context.pathParam("asset");
      LOG.debug("Asset parameter: {}", assetParam);

      // Using the builder pattern
//        var quote = initRandomQuote(assetParam);
//        final JsonObject response = quote.toJsonObject();

      var maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam)); // We are using Optional.ofNullable because assetParam that is passed might not be valid. We are basically doing a Null check.
      if(maybeQuote.isEmpty()) {
        // Page not found error
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code()) // 404 response
          .end(new JsonObject()
            .put("message", "quote for asset " + assetParam + " not available!")
            .put("path", context.normalizedPath())
            .toBuffer()
          );
        return;
      }

      final JsonObject response = maybeQuote.get().toJsonObject();

      LOG.info("Path {} responds with  {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());
    //}
  }
}
