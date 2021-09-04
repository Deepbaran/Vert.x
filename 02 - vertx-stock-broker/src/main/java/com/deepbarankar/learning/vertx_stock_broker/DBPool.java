package com.deepbarankar.learning.vertx_stock_broker;

import com.deepbarankar.learning.vertx_stock_broker.config.BrokerConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class DBPool extends AbstractVerticle {
  /**
   * For PostgreSQL
   * @param configuration
   * @param vertx
   * @return
   */
  public static PgPool createDbPoolPgsql(BrokerConfig configuration, Vertx vertx) {
    final var connectiOptions = new PgConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions()
      .setMaxSize(4);

    return PgPool.pool(vertx, connectiOptions, poolOptions);
  }

  /**
   * For MySQL
   * @param configuration
   * @return
   */
  public static MySQLPool createDbPoolMsql(BrokerConfig configuration, Vertx vertx) {
    final var connectiOptions = new MySQLConnectOptions()
      .setHost(configuration.getDbConfig().getHost())
      .setPort(configuration.getDbConfig().getPort())
      .setDatabase(configuration.getDbConfig().getDatabase())
      .setUser(configuration.getDbConfig().getUser())
      .setPassword(configuration.getDbConfig().getPassword());

    final var poolOptions = new PoolOptions()
      .setMaxSize(4);

    return MySQLPool.pool(vertx, connectiOptions, poolOptions);
  }
}
