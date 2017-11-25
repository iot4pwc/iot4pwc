package com.iot4pwc.verticles;

import com.iot4pwc.components.helpers.DBHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;


public class ActuatorController extends AbstractVerticle {
	Logger logger = LogManager.getLogger(ActuatorController.class);
	Logger actuatorLogger = LogManager.getLogger("com.iot4pwc.actuator.controller");
	private DBHelper dbHelper;
	private String token;
	@Override
	public void start() {
		EventBus eb = vertx.eventBus();

		vertx.executeBlocking(future -> {
			dbHelper = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM);
			future.complete();
		}, response -> {
			eb.consumer(ConstLib.UDOO_TOKEN_ADDRESS, message -> {
				token = (String)message.body();
			});
			//message in certain json format: {app_id:app_id, sensor_id:sensor_id, action_id:action_id}
			eb.consumer(ConstLib.ACTUATOR_ADDRESS, message -> {
				logger.info("got message [" + message.body() + "]");
				JsonObject command = new JsonObject((String) message.body());
				JsonObject authentication = new JsonObject();

				String actionId = command.getString("action_id");
				String appId = command.getString("app_id");
                String actId = command.getString("sensor_id");
				authentication.put(ConstLib.PAYLOAD_FIELD_APP_ID, appId);
				authentication.put(ConstLib.PAYLOAD_FIELD_ACTION_ID, actionId);
                authentication.put(ConstLib.PAYLOAD_FIELD_ACTUATOR_ID, actId);

				eb.send(ConstLib.APP_AUTHENTICATOR_ADDRESS, authentication.toString(), auth -> {
					logger.info("message send to authenticator");
					if (auth.succeeded()) {
						logger.info("got message from authenticator " + auth.result().body());
						if ((boolean) auth.result().body()) {
							actuatorLogger.info("[Authenticated] Action [" + actionId + "] on sensor #" + 
									actId + " from application " + 
									appId);
							//send out request
							sendRequest(command);
							message.reply("Success");
						} else {
							actuatorLogger.info("[Unauthenticated] Action [" + actionId + "] on sensor #" + 
									actId + " from application " + 
									appId);
							message.reply("Authentication Failed");
						}
					} else {
						actuatorLogger.info("[Failure] Action [" + actionId + "] on sensor #" + 
								command.getString("sensor_id") + " from application " + 
								appId);
						message.reply("Failed");
					}
				});
			});
		});

	}

	private void sendRequest(JsonObject command) {
		//fetch the info related to the sensor
		String query = String.format("SELECT * FROM actuator WHERE act_pk_id = '%s'", command.getString("sensor_id"));
    JsonObject info = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query).get(0);
    String gateway_id = info.getString("gateway_id");
    String device_id = info.getString("device_id");
    String sensor_type = info.getString("act_type");
    String sensor_id = info.getString("act_id");
    
		WebClient client = WebClient.create(vertx);
		String action = command.getString("action_id");
		String url = ConstLib.UDOO_ACTUATE_ENDPOINT+ gateway_id + "/" + device_id + "/" + sensor_type + "/" + sensor_id +"/" + action;
		client
		.getAbs(url)
		.putHeader("Authorization", "JWT " + token)
		.as(BodyCodec.jsonObject())
		.send(ar -> {
			if (ar.succeeded()) {
				// Obtain response
				HttpResponse<JsonObject> response = ar.result();
				logger.info("Received response with status code" + response.statusCode());
				if(ar.result().body().containsKey("status") && ar.result().body().getString("status").equals("ok")){
					actuatorLogger.info("Action executed successfully " + ar.result().body().toString());
				}else{
					actuatorLogger.info("Action excecution failed " + ar.result().body().toString());
				}
			} else {
				logger.error("Something went wrong " + ar.cause().getMessage());
			}
		});
	}
}