package com.iot4pwc.verticles;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.tables.QueryTable;
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

      // Start the dbHelper
      dbHelper = new DBHelper();      
      
      // Receive a single message.
      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        
        // Prepare document to be inserted (JSON) and targetTable.
        JsonObject structuredDataJSON = new JsonObject((String)message.body());
        QueryTable targetTable = new QueryTable(
          SensorHistory.tableName,
          SensorHistory.record_id,
          SensorHistory.recorded_time,
          SensorHistory.sensor_id,
          SensorHistory.value_content
        );
        
        // Print success/failure of insertion
        boolean result = dbHelper.insert(structuredDataJSON, targetTable); 
        
        // TODO Use log4j
        System.out.println(DataService.class.getName()+": Insertion success: " + result);
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
