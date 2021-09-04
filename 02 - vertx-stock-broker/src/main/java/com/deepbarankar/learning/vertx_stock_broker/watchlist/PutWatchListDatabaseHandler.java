package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import com.deepbarankar.learning.vertx_stock_broker.DbResponse;
import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PutWatchListDatabaseHandler implements Handler<RoutingContext> {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private final Pool db;

  public PutWatchListDatabaseHandler(Pool db) {
    this.db = db;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);

    var json = context.getBodyAsJson();
    var watchList = json
      .mapTo(WatchList.class); // Mapping a vertx Json object to a WatchList class object

//    watchList.getAssets().forEach(asset -> {
//      final HashMap<String, Object> parameters = new HashMap<>();
//      parameters.put("account_id", accountId);
//      parameters.put("asset", asset.getName());
//      SqlTemplate.forUpdate(db,
//          "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset}")
//        .execute(parameters)
//        .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"))
//        .onSuccess(result -> {
//          if(!context.response().ended()) {
//            // If the previous request did not take place then the next request will not happen and wait. This will happen for all the requests.
//            // This is a strange behavior, because a HTTP response is sent before all the assets are inserted.
//            // To overcome this issue we should use Batch update.
//            context.response()
//              .setStatusCode(HttpResponseStatus.NO_CONTENT.code()) // Sending an empty response as all the assets are still not inserted yet
//              .end();
//          }
//        });
//    });

    // Rather than iterating through each asset in watchlist one by one, a better way would be to perform batch update.
    // Batch Insert - Insert all entries on one go.
    var parameterBatch = watchList.getAssets().stream().map(asset -> {
      final Map<String, Object> parameters = new HashMap<>();
      parameters.put("account_id", accountId);
      parameters.put("asset", asset.getName());
      return parameters;
    }).collect(Collectors.toList());


//    SqlTemplate.forUpdate(db,
//        "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset}"
//      + " ON CONFLICT (account_id, asset) DO NOTHING"
//      )
//      .executeBatch(parameterBatch)
//      .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"))
//      .onSuccess(result ->
//        // Executing all inserts at once. Here we will first insert all the data in the database first, before responding to the HTTP request.
//          context.response()
//            .setStatusCode(HttpResponseStatus.NO_CONTENT.code()) // Sending an empty response as all the assets are still not inserted yet
//            .end()
//      );

    // Transaction
    db.withTransaction(client -> {
      // 1 - Delete all for account_id
      return SqlTemplate.forUpdate(client, "DELETE FROM broker.watchlist w where w.account_id = #{account_id}")
        .execute(Collections.singletonMap("account_id", accountId))
        .onFailure(DbResponse.errorHandler(context, "Failed to clear watchlist for accountId " + accountId))
        .compose(deletionDone -> {
          // 2 - add all for account_id
          return addAllForAccountId(client, context, parameterBatch);
        });// Using .compose() we can chain vertx Futures together
    })
    .onFailure(DbResponse.errorHandler(context, "Failed to update watchlist for accountId " + accountId))
    .onSuccess(result ->
      // 3 - Both succeeded (Only send a HTTP response if both Database statements are executed successfully)
      context.response()
        .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
        .end()
    );

  }

  private Future<SqlResult<Void>> addAllForAccountId(SqlConnection client, RoutingContext context, List<Map<String, Object>> parameterBatch) {
    return SqlTemplate.forUpdate(client,
        "INSERT INTO broker.watchlist VALUES (#{account_id},#{asset}"
          + " ON CONFLICT (account_id, asset) DO NOTHING"
      )
      .executeBatch(parameterBatch)
      .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist"))
      ;
  }
}
