package com.deepbarankar.learning.vertx_starter.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectExample {

  @Test
  void jsonObjectVanBeMapped() {
    final JsonObject myJsonObject = new JsonObject();
    myJsonObject.put("id", 1);
    myJsonObject.put("name", "Alice");
    myJsonObject.put("loves_vertx", true);

    String encoded = myJsonObject.encode();
    assertEquals("{\"id\":1,\"name\":\"Alice\",\"loves_vertx\":true}", encoded); // For more complex types we may have to configure custom checks.

    final JsonObject decodedJsonObject = new JsonObject(encoded);
    assertEquals(myJsonObject, decodedJsonObject);
  }

  // Vert.x JsonObject is similar to a Java Hashmap.

  @Test
  void jsonObjectCanBeCreatedFromMap() {
    final Map<String, Object> myMap = new HashMap<>();
    myMap.put("id", 1);
    myMap.put("name", "Alice");
    myMap.put("loves_vertx", true);
    final JsonObject asJsonObject = new JsonObject(myMap);
    assertEquals(myMap, asJsonObject.getMap());

    assertEquals(1, asJsonObject.getInteger("id"));
    assertEquals("Alice", asJsonObject.getString("name"));
    assertEquals(true, asJsonObject.getBoolean("loves_vertx"));
  }

  @Test
  void jsonArrayCanBeMapped() {
    final JsonArray myJsonArray = new JsonArray();
    myJsonArray
      .add(new JsonObject().put("id", 1))
      .add(new JsonObject().put("name", "Alice"))
      .add(new JsonObject().put("loves_vertx", true))
      .add("randomValue");

    assertEquals("[{\"id\":1},{\"name\":\"Alice\"},{\"loves_vertx\":true},\"randomValue\"]", myJsonArray.encode());
  }

  @Test
  void canMapJavaObjects() {
    final Person person = new Person(1, "Alice", true); //Java Object
    final JsonObject alice = JsonObject.mapFrom(person); // Converting a Java Object to a Vert.x JSON Object
    assertEquals("{\"id\":1,\"name\":\"Alice\",\"loveVertx\":true}", alice.encode());

    // To convert a JsonObject back to a Java object, the Java Object must have a Default Constructor
    final Person person2 = alice.mapTo(Person.class);
    assertEquals(person.getId(), person2.getId());
    assertEquals(person.getName(), person2.getName());
    assertEquals(person.isLoveVertx(), person2.isLoveVertx());
  }

}
