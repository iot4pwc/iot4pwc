package com.iot4pwc.verticles;


import com.iot4pwc.constants.ConstLib;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


public class ActuatorController extends AbstractVerticle{

	@Override
	public void start() {
		EventBus eb = vertx.eventBus();
		//message in certain json format: {app_id:app_id, sensor_id:sensor_id, action_id:action_id}
		eb.consumer(ConstLib.ACTUATOR_ADDRESS, message -> {
			System.out.println(ActuatorController.class.getName()+" : got message [" + message.body() + "]");
			JsonObject command = new JsonObject((String) message.body());
			JsonObject authentication = new JsonObject();
			//String app = command.getString("app_id");
			String action = command.getString("action_id");
			authentication.put(ConstLib.PAYLOAD_FIELD_APP_ID, command.getString("app_id"));
			authentication.put(ConstLib.PAYLOAD_FIELD_ACTION_ID, command.getString("action_id"));
			eb.send(ConstLib.APP_AUTHENTICATOR_ADDRESS, authentication.toString(), auth ->{
				System.out.println(ActuatorController.class.getName()+" : message send to authenticator");
				if(auth.succeeded()){
					System.out.println(ActuatorController.class.getName()+" : got message from authenticator " + auth.result().body());
					if((boolean) auth.result().body()){
						//if(sendRequest(command)){
						message.reply("Success");
						//}
					}
				}else{
					message.reply("Failed");
				}
			});
		});

	}

	@Override
	public void stop() {

	}
	
	
	private boolean sendRequest(JsonObject command){
		//this part is note tested, not called as well
		WebClient client = WebClient.create(vertx);
		
		String tokens = "";
		client
		  .get(443, "cmu.udoo.cloud", "/ext/sensors/write/"+":gatewayId/:deviceId/:sensorType/:sensorId/:pin/:value")
		  .putHeader("Authorization", "JWT "+tokens)
		  .send(ar -> {
		    if (ar.succeeded()) {
		      // Obtain response
		      HttpResponse<Buffer> response = ar.result();
		      System.out.println("Received response with status code" + response.statusCode());
		    } else {
		      System.out.println("Something went wrong " + ar.cause().getMessage());
		    }
		  });
		
		return false;
		
	}
	
	



}
