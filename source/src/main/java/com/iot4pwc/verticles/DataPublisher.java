package com.iot4pwc.verticles;

<<<<<<< HEAD
import com.iot4pwc.constants.ConstLib;
=======
import com.iot4pwc.constants.AddressName;
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * This is a publisher that subscribes to an event published by DataParser and pass the
 * data to the MQTT under certain topic.
 */
public class DataPublisher extends AbstractVerticle{
  public void start() {
    EventBus eb = vertx.eventBus();

<<<<<<< HEAD
    eb.consumer(ConstLib.PUBLISHER_ADDRESS, message -> {
=======
    eb.consumer(AddressName.PUBLISHER_ADDRESS, message -> {
>>>>>>> d94fd3ecb1fa00a5f8cda841f5f87d6f5d0f1c92
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
