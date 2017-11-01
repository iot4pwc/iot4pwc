package com.iot4pwc.verticles;

import com.iot4pwc.components.helpers.DBHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


public class ActuatorController extends AbstractVerticle {
  Logger logger = LogManager.getLogger(ActuatorController.class);
  private DBHelper dbHelper;
  
  @Override
  public void start() {
    EventBus eb = vertx.eventBus();

    vertx.executeBlocking(future -> {
      dbHelper = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM);
      future.complete();
    }, response -> {
      //message in certain json format: {app_id:app_id, sensor_id:sensor_id, action_id:action_id}
      eb.consumer(ConstLib.ACTUATOR_ADDRESS, message -> {
        logger.info("got message [" + message.body() + "]");
        JsonObject command = new JsonObject((String) message.body());
        JsonObject authentication = new JsonObject();

        String actionId = command.getString("action_id");
        String appId = command.getString("app_id");
        authentication.put(ConstLib.PAYLOAD_FIELD_APP_ID, appId);
        authentication.put(ConstLib.PAYLOAD_FIELD_ACTION_ID, actionId);

        eb.send(ConstLib.APP_AUTHENTICATOR_ADDRESS, authentication.toString(), auth -> {
          logger.info("message send to authenticator");
          if (auth.succeeded()) {
            logger.info("got message from authenticator " + auth.result().body());
            if ((boolean) auth.result().body()) {
              message.reply("Success");
            } else {
              message.reply("Failed");
            }
          } else {
            message.reply("Failed");
          }
        });
      });
    });

  }

  @Override
  public void stop() {
    dbHelper.closeDatasource();
  }
  
  // TODO: test this method
  private boolean sendRequest(JsonObject command) {
    WebClient client = WebClient.create(vertx);

    String tokens = "";
    client
      .get(443, "cmu.udoo.cloud", "/ext/sensors/write/" + ":gatewayId/:deviceId/:sensorType/:sensorId/:pin/:value")
      .putHeader("Authorization", "JWT " + tokens)
      .send(ar -> {
        if (ar.succeeded()) {
          // Obtain response
          HttpResponse<Buffer> response = ar.result();
          logger.info("Received response with status code" + response.statusCode());
        } else {
          logger.error("Something went wrong " + ar.cause().getMessage());
        }
      });

    return false;
  }
}
