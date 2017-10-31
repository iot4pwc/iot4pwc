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
  Logger logger = LogManager.getLogger();
  @Override
  public void start() {
    EventBus bus = vertx.eventBus();

    bus.consumer(ConstLib.APP_AUTHENTICATOR_ADDRESS, message -> {
      String data = (String) message.body();
      logger.info(AppAuthenticator.class.getName() + " : got message " + data);
      vertx.executeBlocking(future -> {
        Boolean result = verifyAuthenticity(data);
        logger.info(AppAuthenticator.class.getName() + " : send message " + result);
        future.complete(result);
      }, res -> message.reply(res.result()));
    });

  }

  private Boolean verifyAuthenticity(String data) {
    JsonObject dataObj = new JsonObject(data);
    String appId = dataObj.getString(ConstLib.PAYLOAD_FIELD_APP_ID);
    String actionId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTION_ID);

    /**
     * Query the database to get if appId (the application) is authorized to control actionId (actuator's action).
     * Creating a dbHelper everytime and closing the connection because it might be wasteful to have a live connection and not use it.
     * May be changed when authenticater has to scale.
     */
    String query = "SELECT COUNT(*) AS CNT FROM app_action_map " +
      "WHERE app_id = " + appId + " AND record_id = " + actionId;

    List<JsonObject> records = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
    if (records != null) {
      int count = Integer.parseInt(records.get(0).getString("CNT"));
      if (count > 0) {
        return true;
      }
    }
    return false;
  }
}

