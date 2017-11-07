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
import io.vertx.ext.web.codec.BodyCodec;


public class ActuatorController extends AbstractVerticle {
	Logger logger = LogManager.getLogger(ActuatorController.class);
	Logger actuatorLogger = LogManager.getLogger("com.iot4pwc.actuator.controller");
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
							actuatorLogger.info("[Authenticated] Action [" + actionId + "] on sensor #" + 
									command.getString("sensor_id") + " from application " + 
									appId);
							//send out request
							sendRequest(command);
							message.reply("Success");
						} else {
							actuatorLogger.info("[Unauthenticated] Action [" + actionId + "] on sensor #" + 
									command.getString("sensor_id") + " from application " + 
									appId);
							message.reply("Failed");
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
		WebClient client = WebClient.create(vertx);
		//http://udoo-iot-beta.cleverapps.io
		String tokens = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU5ZThjNjgxN2UzMzNmMDliNjE1MDk2NCIsImlhdCI6MTUxMDA3MjYyMH0.wTREIR_sqEDW1KfHJs150VHKrRp2BTS9N03NgwDmnaE";
		//1: on   0:off
		String action = command.getString("action_id");
		client
		.get(8080, ConstLib.UDOO_ACTUATE_ENDPOINT, "/ext/sensors/write/82ccd7c9f70f23cbe570d1644f60a7293603fe95c5c51cabc6ee0de72f0df61d/ttyMCC-2125c1d4df669959/digital/13/"+action)
		.putHeader("Authorization", "JWT " + tokens)
		.as(BodyCodec.jsonObject())
		.send(ar -> {
			if (ar.succeeded()) {
				// Obtain response
				HttpResponse<JsonObject> response = ar.result();
				JsonObject res = response.bodyAsJsonObject();
				logger.info("Received response with status code" + response.statusCode());
				if(res.getString("status").equals("ok")){
					actuatorLogger.info("Action executed successfully " + res.toString());
				}else{
					actuatorLogger.info("Action excecution failed " + res.toString());
				}
			} else {
				logger.error("Something went wrong " + ar.cause().getMessage());
			}
		});
	}
}
