package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle{
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
      // structuredData is a JSON string
      String structuredData = (String)message.body();
      /**
       * implement business logic here. DataService should persist data to DB
       */
    });
  }

  public void stop() {
    /**
     * clear up, feel free to delete this method if you think it's unnecessary
     */
  }
}
