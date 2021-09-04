package com.deepbarankar.learning.vertx_stock_broker.quotes;

import com.deepbarankar.learning.vertx_stock_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder // Creates a Builder pattern
public class Quote {
  Asset asset;
  BigDecimal bid;
  BigDecimal ask;
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
