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

  @Override
  public void start() {
    EventBus eb = vertx.eventBus();

    vertx.executeBlocking(future -> {
      dbHelper = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM);
      future.complete();
    }, response -> {
      WorkerExecutor executor = vertx.createSharedWorkerExecutor(
              ConstLib.DATA_SERVICE_WORKER_POOL,
              ConstLib.DATA_SERVICE_WORKER_POOL_SIZE
      );

      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        JsonObject structuredDataJSON = new JsonObject((String) message.body());
        executor.executeBlocking(future -> {
          dbHelper.insert(structuredDataJSON, SensorHistory.getInstance());
          future.complete();
        }, res -> {
          // TODO: add response
        });
      });
    });
  }

  @Override
  public void stop() {
    dbHelper.closeDatasource();
  }
}
