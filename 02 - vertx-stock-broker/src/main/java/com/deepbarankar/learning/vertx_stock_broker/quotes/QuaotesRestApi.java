package com.deepbarankar.learning.vertx_stock_broker.quotes;

import com.deepbarankar.learning.vertx_stock_broker.assets.Asset;
import com.deepbarankar.learning.vertx_stock_broker.assets.AssetsRestApi;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Pool;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuaotesRestApi {

  public static void attach(Router parent, Pool db) {

    // Now we have an in memory store that quote end points use
    final Map<String, Quote> cachedQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    parent.get("/quotes/:asset") // :asset is a PATH parameter. It will be dynamic
      .handler(new GetQuoteHandler(cachedQuotes));
    parent.get("/pg/quotes/:asset").handler(new GetQuoteFromDatabaseHandler(db));
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
      .asset(new Asset(assetParam))
      .bid(randomValue())
      .ask(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
