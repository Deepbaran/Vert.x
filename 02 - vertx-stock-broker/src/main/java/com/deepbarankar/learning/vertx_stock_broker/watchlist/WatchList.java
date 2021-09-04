package com.deepbarankar.learning.vertx_stock_broker.watchlist;

import com.deepbarankar.learning.vertx_stock_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Value
// We need an object to be constructed form a JSON Object. So, WatchList cannot be immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {
  private List<Asset> assets;

  JsonObject toJsonOnject() {
    return JsonObject.mapFrom(this);
  }
}
