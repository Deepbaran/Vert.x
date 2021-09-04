package com.deepbarankar.learning.vertx_stock_broker.quotes;

import com.deepbarankar.learning.vertx_stock_broker.assets.Asset;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteEntitiy {

  Asset asset;
  BigDecimal bid;
  BigDecimal ask;
  @JsonProperty("last_price") // Define how an Object should be serialized. Use this whenever naming a column anything other than camelCase.
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }

}
