package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * This is a publisher that subscribes to an event published by DataParser and pass the
 * data to the MQTT under certain topic.
 */
public class DataPublisher extends AbstractVerticle{
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.PUBLISHER_ADDRESS, message -> {
      // structuredData is a JSON string
      String structuredData = (String)message.body();
      /**
       * implement business logic here. Publisher should pass the data to the MQTT
       */
    });
  }

  public void stop() {
    /**
     * clear up, feel free to delete this method if you think it's unnecessary
     */
  }
}
