package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetWatchListHandler.class);
  private final Map<UUID, WatchList> watchListPerAccount;

  public PutWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
//    context -> {
      String accountId = WatchListRestApi.getAccountId(context);

      var json = context.getBodyAsJson();
      var watchList = json.mapTo(WatchList.class); // To be able to pass request bodies on the server side we need to register a body handler.
      watchListPerAccount.put(UUID.fromString(accountId), watchList);
      // There might be errors here: If the client sends wrong request it will fail the mapping of the watchList object; The UUID might be wrong. Use a proper error handling is needed.
      context.response().end(json.toBuffer());
//    }
  }
}
