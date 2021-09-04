package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import com.deepbarankar.learning.vertx_stock_broker.MainVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final String PATH = "/account/watchlist/:accountId";

  public static void attach(Router parent, Pool db) {
    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    // GET endpoint
    parent.get(PATH).handler(new GetWatchListHandler(watchListPerAccount));

    // PUT endpoint
    parent.put(PATH).handler(new PutWatchListHandler(watchListPerAccount));

    // DELETE endpoint
    parent.delete(PATH).handler(new DeleteWatchListHandler(watchListPerAccount));

    final String pgPath = "/pg/account/watchlist/:accountId";
    parent.get(pgPath).handler(new GetWatchListFromDatabaseHandler(db));
    parent.put(pgPath).handler(new PutWatchListDatabaseHandler(db));
    parent.delete(pgPath).handler(new DeleteWatchListDatabaseHandler(db));
  }

  static String getAccountId(RoutingContext context) { // The function is stateless. So, we can use it without private modifier.
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
