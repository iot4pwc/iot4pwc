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
  
  /**
   * Start the verticle to authenticate any application's request to control actuator.
   * Instantiate the DbHelper, and start listening to the event bus for messages.
   * Once the RESTfulDBService sends an authentication request, it checks and sends a boolean reply.
   */
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

  /**
   * Perform any operations on the stop of the verticle.
   */
  @Override
  public void stop() {
  }
  
  /**
   * Method to verify if the app action is authorized or not.
   * @param data A JSON object containing the following fields: application id, action id of the action requested, and the actuator id.
   * @return A boolean value. True, if the action is authenticated; false, otherwise.
   */
  private Boolean verifyAuthenticity(String data) {
    JsonObject dataObj = new JsonObject(data);
    String appId = dataObj.getString(ConstLib.PAYLOAD_FIELD_APP_ID);
    String actionId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTION_ID);
    String actuatorId = dataObj.getString(ConstLib.PAYLOAD_FIELD_ACTUATOR_ID);

    /**
     * Query the database to get if appId (the application) is authorized to control actionId (actuator's action).
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

