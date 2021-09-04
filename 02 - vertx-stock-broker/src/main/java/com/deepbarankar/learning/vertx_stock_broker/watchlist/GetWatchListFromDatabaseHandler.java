package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import com.deepbarankar.learning.vertx_stock_broker.DbResponse;
import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class GetWatchListFromDatabaseHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler.class);
  private final Pool db;

  public GetWatchListFromDatabaseHandler(final Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    SqlTemplate.forQuery(db,
      "SELECT w.asset FROM broker.watchlist w where w.account_id=#{account_id}")
      .mapTo(Row::toJson) // This will transform the Rows that are returned from the onSuccess into a JsonOnject.
      .execute(Collections.singletonMap("account_id", accountId))
      .onFailure(DbResponse.errorHandler(context, "Failed to fetch watchlist for accountId: " + accountId))
      .onSuccess(assets -> {
        //final RowSet<JsonObject> rows = assets; // AS we will need to return JsonObject to the HTTP request, so having JsonObject Rows saves time.
        if(!assets.iterator().hasNext()) {
          DbResponse.notFoundResponse(context, "Watchlist for accountId " + accountId + " is not available!");
          return;
        }
        var response = new JsonArray();
        assets.forEach(response::add);
        LOG.info("Path {} responds with  {}", context.normalizedPath(), response.encode());
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });
  }
}
