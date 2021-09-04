package com.deepbarankar.learning.vertx_stock_broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;
  DbConfig dbConfig;

  public static BrokerConfig from(final JsonObject config) {

    final Integer serverPort = config.getInteger(ConfigLoader.SERVER_PORT); ///////////////////////////////////////////////////////////////////////////////
    if(Objects.isNull(serverPort)) { ///////////////////////////////////////////////////////////    Creating a Better Error Message
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!"); ///////////////    When Server Port is not configured
    } /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    final String version = config.getString("version");
    if(Objects.isNull(version)) {
      throw new RuntimeException("version is not configured in config file!");
    }

    return BrokerConfig.builder()
      .serverPort(config.getInteger(ConfigLoader.SERVER_PORT)) // added serverPort value
      .version(version) // added version value
      .dbConfig(parseDbConfig(config)) // Added the Database Configurations
      .build(); // Building the BrokerConfig Object and returning
  }

  private static DbConfig parseDbConfig(final JsonObject config) {
    return DbConfig.builder() // Building a DbConfig object using the Builder pattern
      .host(config.getString(ConfigLoader.DB_HOST))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .user(config.getString(ConfigLoader.DB_USER))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .build();
  }
}
