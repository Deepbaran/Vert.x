package com.deepbarankar.learning.vertx_starter.eventbus.customcodec;

public class Ping {

  private String message;
  private boolean enabled;

  public Ping() {
    // Default Constructor
  }

  public Ping(String message, boolean enabled) {
    this.message = message;
    this.enabled = enabled;
  }

  public String getMessage() {
    return message;
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String toString() {
    return "Ping{" +
      "message='" + message + '\'' +
      ", enabled=" + enabled +
      '}';
  }
}
