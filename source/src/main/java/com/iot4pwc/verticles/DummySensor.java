package com.iot4pwc.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import com.iot4pwc.constants.ConstLib;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This is a dummy sensor that generates random numeric data every 5s
 */
public class DummySensor extends AbstractVerticle {
  private static final int BASE_ID = 1;
  private static final int BASE_PAYLOAD = 50;
  private Random random;
  private long timerID;

  public void start() {
    random = new Random();

    EventBus eb = vertx.eventBus();
    timerID = vertx.setPeriodic(500, id -> {
      String payload = generateData();
      eb.publish(ConstLib.PARSER_ADDRESS, payload);
      System.out.println(String.format("Sent: %s", payload));
    });
  }

  public void stop() {
    vertx.cancelTimer(timerID);
  }

  private String generateData() {
    Map<String, Object> payloads = new HashMap<>();
    payloads.put("sensor_id", BASE_ID + random.nextInt(10));
    payloads.put("value_content", BASE_PAYLOAD + random.nextInt(50));
    payloads.put("recorded_time", Instant.now().toEpochMilli());
    JsonObject jsonObject = new JsonObject(payloads);
    return jsonObject.encode();
  }
}
