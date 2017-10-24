package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * This is a parser that will subscribe to an event published by DataPoller/DataListener.
 * It will also publish formatted data to DataService for DB persistence and DataPublisher
 * for MQTT publishing.
 */
public class DataParser extends AbstractVerticle {
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.PARSER_ADDRESS, message -> {
      String data = (String)message.body();
      /**
       * implement business logic here.
       * reconstruct data to proper format for publishing and persisting.
       * suppose the result is a JSON string named structuredData.
       */
      String structuredData = data;

      eb.send(ConstLib.DATA_SERVICE_ADDRESS, structuredData);
      eb.send(ConstLib.PUBLISHER_ADDRESS, structuredData);
    });
  }
}
