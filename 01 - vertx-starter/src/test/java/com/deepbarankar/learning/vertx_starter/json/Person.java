package com.deepbarankar.learning.vertx_starter.json;

public class Person {

  private Integer id;
  private String name;
  private boolean loveVertx;

  public Person() {
    // Default Constructor for Jackson
  }

  public Person(Integer id, String name, boolean loveVertx) {
    this.id = id;
    this.name = name;
    this.loveVertx = loveVertx;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isLoveVertx() {
    return loveVertx;
  }

  public void setLoveVertx(boolean loveVertx) {
    this.loveVertx = loveVertx;
  }
}
