package com.deepbarankar.learning.mutiny.vertx_mutiny;

import io.smallrye.mutiny.Multi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class HelloMulti {

  private static final Logger LOG = LoggerFactory.getLogger(HelloMulti.class);

  public static void main(String[] args) {
    // Multi represents a stream of data. A stream can emit 0, 1, n, or an infinite number of items.
    Multi.createFrom().items(IntStream.rangeClosed(0, 10).boxed())
      .onItem().transform(value -> value * 2)
      //.onFailure().recoverWithItem(-1) // If failure occurs then we will only get -1
      .onFailure().invoke(failure -> LOG.error("Transformation failed with: ", failure)) // Failure handling that may occur due to the transform
      .onItem().transform(String::valueOf)
      .select().first(4) // Take only first 4 entries | .select.last(n) takes only last n entries
      .subscribe().with(
        LOG::info, // info -> Log("{}", info);
        failure -> LOG.error("Failed with: ", failure)
      );
  }
}
