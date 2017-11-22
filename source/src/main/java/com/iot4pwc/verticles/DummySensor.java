package com.iot4pwc.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import com.iot4pwc.constants.ConstLib;
import java.util.Date;
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
    timerID = vertx.setPeriodic(ConstLib.DUMMY_DATA_INTERVAL, id -> {
      JsonObject payload = generateData();
//      eb.send(ConstLib.PUBLISHER_ADDRESS, payload);
//      eb.send(ConstLib.DATA_SERVICE_ADDRESS, payload);
    });
  }

  public void stop() {
    vertx.cancelTimer(timerID);
  }

  private JsonObject generateData() {
    JsonObject payloads = new JsonObject();
    payloads.put("timestamp", new Date().getTime());
    payloads.put("sensor_pk_id", "" + BASE_ID + random.nextInt(6));
    payloads.put("value_key", "Dummy Sensor, dumm dumm!");
    payloads.put("value_content", Integer.toString(BASE_PAYLOAD + random.nextInt(50)));
    return payloads;
  }
}
