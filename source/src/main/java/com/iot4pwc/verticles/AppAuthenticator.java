package com.iot4pwc.verticles;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * This is an authenticator that checks whether an application has access to certain hardware
 */
public class AppAuthenticator extends AbstractVerticle {
  
  Logger logger = LogManager.getLogger(AppAuthenticator.class);
  private DBHelper dbHelper;
  
  @Override
  public void start() {
    EventBus bus = vertx.eventBus();

    vertx.executeBlocking(future -> {
      dbHelper = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM);
      future.complete();
    }, response -> {
      bus.consumer(ConstLib.APP_AUTHENTICATOR_ADDRESS, message -> {
        String data = (String) message.body();
        logger.info("got message " + data);
        vertx.executeBlocking(future -> {
          Boolean result = verifyAuthenticity(data);
          logger.info("send message " + result);
          future.complete(result);
        }, res -> message.reply(res.result()));
      });
    });

  }

  @Override
  public void stop() {
  }
  
  private Boolean verifyAuthenticity(String data) {
    JsonObject dataObj = new JsonObject(data);
    String appId = dataObj.getString(ConstLib.PAYLOAD_FIELD_APP_ID);
    String actionId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTION_ID);
    String actuatorId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTUATOR_ID);

    /**
     * Query the database to get if appId (the application) is authorized to control actionId (actuator's action).
     * Creating a dbHelper every time and closing the connection because it might be wasteful to have a live connection and not use it.
     * May be changed when authenticator has to scale.
     */
    String query = "SELECT COUNT(*) AS CNT FROM app_action_map JOIN actuator_action_map USING(record_id) " +
      "WHERE app_id = " + appId + " AND act_pk_id = '" + actuatorId + "' AND action_code = " + actionId;

    List<JsonObject> records = dbHelper.select(query);
    if (records != null) {
      int count = Integer.parseInt(records.get(0).getString("CNT"));
      if (count > 0) {
        return true;
      }
    }
    return false;
  }
}

