package com.iot4pwc.verticles;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.helpers.RFIDDataPoller;
import com.iot4pwc.components.helpers.NormalDataPoller;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.codec.BodyCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a poller that polls data from the UDOO platform. 
 */

public class DataPoller extends AbstractVerticle {
  private WebClient client;
  private String token;
  Logger logger = LogManager.getLogger(DataPoller.class);
  Date RFIDLastTime;
  Date normalLastTime;
  
  public void start() {
	try {
		RFIDLastTime = new SimpleDateFormat("yyyyMMddHHmm").parse("201710010000");
		normalLastTime = new SimpleDateFormat("yyyyMMddHHmm").parse("201710010000");
	} catch (ParseException e) {
		e.printStackTrace();
	}
	RFIDDataPoller.getInstance().getFrequency();
    vertx.executeBlocking(future -> {
      vertx.deployVerticle(new UdooTokenAcquirer(), delay -> future.complete("UdooTokenAcquirer Deployment Complete"));
    }, response -> {
      client = WebClient.create(vertx);
      EventBus eb = vertx.eventBus();
      eb.consumer(ConstLib.UDOO_TOKEN_ADDRESS, message -> {
    	token = (String)message.body();
    	logger.info("Received a udoo token: [" + message.body() + "] at time " + new Date());
    	pollData(RFIDDataPoller.getInstance().getQuery(), RFIDLastTime);
    	vertx.setPeriodic(RFIDDataPoller.getInstance().getFrequency(), id -> {
    		pollData(RFIDDataPoller.getInstance().getQuery(), RFIDLastTime);
    		RFIDLastTime = new Date();
    	});
//    	pollData(NormalDataPoller.getInstance().getQuery(), normalLastTime);
//    	vertx.setPeriodic(NormalDataPoller.getInstance().getFrequency(), id -> {
//    		pollData(NormalDataPoller.getInstance().getQuery(), normalLastTime);
//    	    normalLastTime = new Date();
//    	});
      });
    });

  }

  public void pollData(String query, Date lastTime) {
//	List<JsonObject> result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
	List<JsonObject> result = new LinkedList<>();
	JsonObject object = new JsonObject();
	object.put("gateway_id", "33a84bf0ab9724cf90d20f464496d60aea877f3c4db8ccf465cf52f8e4d10f90");
	object.put("device_id", "neo-231ab9d4e3167ab8");
	object.put("sensor_type", "virtual");
	object.put("sensor_id", "usbRFID");
	result.add(object);
	for (JsonObject jo: result) {
      String gateway_id = jo.getString("gateway_id");
      String device_id = jo.getString("device_id");
	  String sensor_type = jo.getString("sensor_type");
	  String sensor_id = jo.getString("sensor_id");
      getSensorHistoryValue(gateway_id, device_id, sensor_type, sensor_id, lastTime);
    }
//	System.out.println("time at " +lastTime);
  }

  public void getSensorHistoryValue(String gatewayId, String deviceId, String sensorType, String sensorId, Date lastTime) {
    // This call return the historical sensor value connected to A9 core,(Udoo bricks).
    // It requires the <gatewayId>, deviceId, sensorType, sensor id.
	client.getAbs(ConstLib.UDOO_ENDPOINT + "/ext/sensors/history/realtime/" + gatewayId +"/" + deviceId +"/" + sensorType +"/" +sensorId)
	      .putHeader("Authorization", "JWT " + token)
	      .as(BodyCodec.jsonObject())
	      .send(ar -> {
	        if (ar.succeeded()) {
	          HttpResponse<JsonObject> response = ar.result();
	          JsonObject body = response.body();
	          body.put("sensor_id", sensorId);
	          body.put("lastTime", new SimpleDateFormat("yyyyMMddHHmm").format(lastTime));
	          EventBus eb = vertx.eventBus();
	          eb.send(ConstLib.PARSER_ADDRESS, body);
	        } else {
	          logger.error("Something went wrong " + ar.cause().getMessage());
	        }
    });
  }

}
