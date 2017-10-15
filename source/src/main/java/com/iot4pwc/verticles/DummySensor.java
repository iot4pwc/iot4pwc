package com.iot4pwc.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
<<<<<<< HEAD
import com.iot4pwc.constants.ConstLib;
=======
import com.iot4pwc.constants.AddressName;
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This is a dummy sensor that generates random numeric data every 5s
 */
public class DummySensor extends AbstractVerticle {
  private static final int BASE_ID = 1200;
  private static final int BASE_PAYLOAD = 50;
  private Random random;
  private long timerID;

  public void start() {
    random = new Random();

    EventBus eb = vertx.eventBus();
    timerID = vertx.setPeriodic(500, id -> {
      String payload = generateData();
<<<<<<< HEAD
      eb.publish(ConstLib.PARSER_ADDRESS, payload);
=======
      eb.publish(AddressName.PARSER_ADDRESS, payload);
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92
      System.out.println(payload);
    });
  }

  public void stop() {
    vertx.cancelTimer(timerID);
  }

  private String generateData() {
    Map<String, Object> payloads = new HashMap<>();
    payloads.put("sensorID", BASE_ID + random.nextInt(50));
    payloads.put("payload", BASE_PAYLOAD + random.nextInt(50));
    payloads.put("time", Instant.now().toEpochMilli());
    payloads.put("topic", "temperature");
    JsonObject jsonObject = new JsonObject(payloads);
    return jsonObject.encode();
  }
}
