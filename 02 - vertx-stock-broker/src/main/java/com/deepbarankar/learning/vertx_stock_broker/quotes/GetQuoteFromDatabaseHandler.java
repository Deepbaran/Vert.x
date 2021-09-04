package com.deepbarankar.learning.vertx_stock_broker.quotes;

import com.deepbarankar.learning.vertx_stock_broker.DbResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetQuoteFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler.class);
  private final Pool db;

  public GetQuoteFromDatabaseHandler(final Pool db) {
    this.db = db;
  }


  @Override
  public void handle(final RoutingContext context) {
    final String assetParam = context.pathParam("asset");
    LOG.debug("Asset parameter: {}", assetParam);
    SqlTemplate.forQuery(db,
      "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume from broker.quotes q where asset=#{asset}"
      )
      .mapTo(QuoteEntitiy.class) // Map the response that the Future returns onSuccess to the QuoteEntity Object
      .execute(Collections.singletonMap("asset", assetParam))
      .onFailure(DbResponse.errorHandler(context, "Failed to get quote for asset "+ assetParam +" from db!"))
      .onSuccess(quotes -> {
        if(!quotes.iterator().hasNext()) {
          // No entry
          DbResponse.notFoundResponse(context, "quote for asset "+ assetParam +" not available!");
          return;
        }
        var response = quotes.iterator().next().toJsonObject();
        LOG.info("Path {} responds with  {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
