package com.iot4pwc.verticles;

import java.util.HashMap;
import java.util.Map;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.tables.SensorHistory;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle {
  Logger logger = LogManager.getLogger(DataService.class);
  public void start() {
    EventBus eb = vertx.eventBus();

    WorkerExecutor executor = vertx.createSharedWorkerExecutor(
      ConstLib.DATA_SERVICE_WORKER_POOL,
      ConstLib.DATA_SERVICE_WORKER_POOL_SIZE
    );

    eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
      JsonObject structuredDataJSON = (JsonObject)message.body();
      executor.executeBlocking (future -> {
        DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).insert(structuredDataJSON, SensorHistory.getInstance());
        future.complete();
      }, res -> {
        // logger.info("Stored data into sensor_history:" + structuredDataJSON.toString());
      });
    });
  }

  public void stop() {
    DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).closeDatasource();
  }
}
