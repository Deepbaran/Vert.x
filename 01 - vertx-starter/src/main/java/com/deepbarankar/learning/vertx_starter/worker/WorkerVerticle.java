package com.deepbarankar.learning.vertx_starter.worker;

import com.deepbarankar.learning.vertx_starter.eventloops.EventLoopExample;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(WorkerVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Running a Blocking Operation using Worker Verticle
    // All code inside the Worker Verticle will be executed on a Worker Thread.
    LOG.debug("Deployed as worker verticle");
    startPromise.complete();
    Thread.sleep(5000);
    LOG.debug("Blocking operation done");
  }
}
