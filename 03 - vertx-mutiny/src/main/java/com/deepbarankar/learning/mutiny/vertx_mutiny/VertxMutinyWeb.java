package com.deepbarankar.learning.mutiny.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxMutinyWeb extends AbstractVerticle {
  // All requests are handled on the Vert.x Event-loop thread.

  private static final Logger LOG = LoggerFactory.getLogger(VertxMutinyWeb.class);

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new VertxMutinyWeb())
      .subscribe().with(id -> LOG.info("Started: {}", id));
  }

  @Override
  public Uni<Void> asyncStart() {
    var router = Router.router(vertx);
    router.route().failureHandler(this::failureHandler);
    router.get("/users").respond(this::getUsers);

    return vertx.createHttpServer()
      //.requestHandler(req -> req.response().endAndForget("Hello!"))
      .requestHandler(router)
      .listen(8111) // return Uni of HTTP type
      .replaceWithVoid(); // return the Uni after transforming it to type Void. It is similar to mapEmpty of Future.
  }

  private Uni<JsonArray> getUsers(RoutingContext routingContext) {
    final var responseBody = new JsonArray();
    responseBody.add(new JsonObject().put("name", "Alice"));
    responseBody.add(new JsonObject().put("name", "Bob"));
    return Uni.createFrom().item(responseBody);
  }

  private void failureHandler(RoutingContext routingContext) {
    routingContext.response().setStatusCode(500).endAndForget("Something went wrong :(");
  }
}
