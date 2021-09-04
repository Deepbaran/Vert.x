package com.deepbarankar.learning.mutiny.vertx_mutiny.db;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class EmbeddedPostgres {

  static final String DATABASE_NAME = "users";
  static final String USERNAME = "postgres";
  static final String PASSWORD = "secret";

  static int startPostgres() {
    // Using test containers to spin up a Postgres DB
    var pg = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3-alpine"))
      .withDatabaseName(DATABASE_NAME)
      .withUsername(USERNAME)
      .withPassword(PASSWORD)
      .withInitScript("db/setup.sql"); // Execute this script on start up
    pg.start();
    return pg.getFirstMappedPort();
  }
}
