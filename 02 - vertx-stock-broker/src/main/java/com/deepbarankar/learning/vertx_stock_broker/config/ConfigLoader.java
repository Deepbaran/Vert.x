package com.deepbarankar.learning.vertx_stock_broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ConfigLoader {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_DATABASE = "DB_DATABASE";
  public static final String DB_USER = "DB_USER";
  public static final String DB_PASSWORD = "DB_PASSWORD";
  static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(
    SERVER_PORT,
    DB_HOST,
    DB_PORT,
    DB_DATABASE,
    DB_USER,
    DB_PASSWORD
  );
  public static final String CONFIG_FILE = "application.yml";

  public static Future<BrokerConfig> load(Vertx vertx) {
    // environment store
    final var exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
    LOG.debug("Fetch Configuration for {}", exposedKeys.encode());

    var envStore = new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("keys", exposedKeys));
    // By default all environmental application would be exposed to our application, also the ones from the system.
    // To have only specific environmental variables we need to define the configuration to limit the amount of exposed environment variables.

    var propertyStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cache", false));

    var yamlStore = new ConfigStoreOptions()
      .setType("file") // YAML is a file type property
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", CONFIG_FILE));

    // Load the config variable
    var retriever = ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions()
        .addStore(yamlStore) // Added YAML property
        .addStore(propertyStore) // Added System Property
        .addStore(envStore) // Added Environment property
      );

    return retriever.getConfig().map(BrokerConfig::from); // It returns a Future. So the Config Loading is asynchronous.
  }
}
