package com.deepbarankar.learning.vertx_stock_broker.assets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Value // This lombok annotation creates an immutable class (No setters). It adds a constructor and getters for all fields.
// We need an object to be constructed form a JSON Object. So, Asset cannot be immutable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
  String name;
}

//  public Asset(final String name) {
//    this.name = name;
//  }
//
//  public String getName() {
//    return name;
//  }
//}
