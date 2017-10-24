package com.iot4pwc.verticles;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.util.List;

/**
 * This is an authenticator that checks whether an application has access to certain hardware
 */
public class AppAuthenticator extends AbstractVerticle {
    
  private DBHelper dbHelper;
  
  public void start() {
    EventBus bus = vertx.eventBus();
    
    bus.consumer(ConstLib.APP_AUTHENTICATOR_ADDRESS, message -> {
      String data = (String) message.body();
      
      vertx.executeBlocking(future -> {
        Boolean result = verifyAuthenticity(data);
        future.complete(result);
      }, res -> {
        message.reply(res.result());
      });
      
    });
    
  }
  
  public void stop() {

  }
  
  private Boolean verifyAuthenticity(String data) {
    JsonObject dataObj = new JsonObject(data);
    String appId = dataObj.getString(ConstLib.PAYLOAD_FIELD_APP_ID);
    String actionId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTION_ID);
    
    /*
    Query the database to get if appId (the application) is authorized to control actionId (actuator's action).
    Creating a dbHelper everytime and closing the connection because it might be wasteful to have a live connection and not use it.
    May be changed when authenticater has to scale.
    */
    this.dbHelper = new DBHelper();
    String query = "SELECT COUNT(*) AS CNT FROM app_action_map WHERE app_id = " + appId + " AND recordId = " + actionId;
    List<JsonObject> records = dbHelper.select(query);
    dbHelper.closeConnection();
    if(records != null) {
      int count = Integer.parseInt(records.get(0).getString("CNT"));
      if(count > 0) {
        return true;
      }
    }
    return false;
  }
  
}
