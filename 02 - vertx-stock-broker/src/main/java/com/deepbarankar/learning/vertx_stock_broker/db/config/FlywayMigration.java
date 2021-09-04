package com.deepbarankar.learning.vertx_stock_broker.db.config;

import com.deepbarankar.learning.vertx_stock_broker.config.DbConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlywayMigration {

  private static final Logger LOG = LoggerFactory.getLogger(FlywayMigration.class);

  public static Future<Void> migrate(final Vertx vertx, final DbConfig dbConfig) {
    LOG.debug("DB Config: {}", dbConfig);

    // As Flyway uses JDBC client, so the code is blocking and we need to run it on a blocking thread
    return vertx.<Void>executeBlocking(promise -> {
      // Flyway migration is blocking => uses JDBC
      execute(dbConfig);
      promise.complete();
    })
      .onFailure(err -> LOG.error("Failed to migrate db schema with error: ", err));
  }

  private static void execute(DbConfig dbConfig) {
    final String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s",
      dbConfig.getHost(),
      dbConfig.getPort(),
      dbConfig.getDatabase()
      );
    LOG.debug("Migrating DB schema using jdbc url: {}", jdbcUrl);

    // Migrate
    final Flyway flyway = Flyway.configure()
      .dataSource(jdbcUrl, dbConfig.getUser(), dbConfig.getPassword())
      .schemas("broker")
      .defaultSchema("broker")
      .load();

    var current = Optional.ofNullable(flyway.info().current()); // Gives the current version of the database and if no database is assigned then returns null
    current.ifPresent(info -> LOG.info("db schema is at version: {}", info.getVersion()));

    // To see pending migrations. This gives valuable information on what was migrated on start up of the application
    var pendingMigrations = flyway.info().pending();
    LOG.debug("Pending migrations are: {}", printMigrations(pendingMigrations));

    // Migrating the Schema
    flyway.migrate(); // Flyway keeps a version history in the database, and if a new version is available then flyway will make sure to migrate it to the latest database version
  }

  private static String printMigrations(final MigrationInfo[] pending) {
    if(Objects.isNull(pending)) {
      return "[]";
    }
    return Arrays.stream(pending)
      .map(each -> each.getVersion() + " - " + each.getDescription())
      .collect(Collectors.joining(",", "[", "]"));
  }

}
