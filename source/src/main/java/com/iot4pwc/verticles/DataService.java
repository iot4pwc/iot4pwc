package com.iot4pwc.verticles;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.tables.SensorHistory;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle {
  private DBHelper dbHelper;

  public void start() {
    EventBus eb = vertx.eventBus();
    
    WorkerExecutor executor = vertx.createSharedWorkerExecutor(ConstLib.DATA_SERVICE_WORKER_POOL);
    executor.executeBlocking (future -> {
      dbHelper = new DBHelper();

      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        JsonObject structuredDataJSON = new JsonObject((String)message.body());
        dbHelper.insert(structuredDataJSON, SensorHistory.getInstance());
      });
      future.complete();
    }, res -> {
      // TODO: add response
    });
  }

  public void stop() {
    dbHelper.closeConnection();
  }
}
