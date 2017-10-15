package com.iot4pwc.verticles;

import com.iot4pwc.constants.AddressName;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class DataService extends AbstractVerticle{
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(AddressName.DATA_SERVICE_ADDRESS, message -> {
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
