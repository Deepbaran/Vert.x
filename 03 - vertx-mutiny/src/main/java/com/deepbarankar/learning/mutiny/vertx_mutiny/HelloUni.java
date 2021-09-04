package com.deepbarankar.learning.mutiny.vertx_mutiny;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloUni {

  private static final Logger LOG = LoggerFactory.getLogger(HelloUni.class);

  public static void main(String[] args) {
    // Uni represents a stream that can only emit either an item or a failure event.
    // Creating a Uni Object
    Uni.createFrom().item("Hello")
      .onItem().transform(item -> item + " Mutiny!")
      .onItem().transform(String::toUpperCase)
      .subscribe().with(
        item -> LOG.info("Item: {}", item)
      );

    Uni.createFrom().item("Ignored due to failure")
      .onItem().castTo(Integer.class) // We are trying to cast a String item to Integer and that will give a failure
      .subscribe().with(
        item -> LOG.info("Item: {}", item), // 1st consumer
        failure -> LOG.error("Failed with: ", failure) // 2nd consumer
      )
    ;
  }

}
