package com.iot4pwc.verticles;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.helpers.DataPollerHelper;
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

public class RFIDPoller extends AbstractVerticle implements DataPollerHelper {
  private WebClient client;
  private String token;
  Logger logger = LogManager.getLogger(RFIDPoller.class);
  Date lastTime;
  public static final long HOUR = 3600*1000; // in milli-seconds.
  
  public void start() {
	lastTime = new Date();
    vertx.executeBlocking(future -> {
      vertx.deployVerticle(new UdooTokenAcquirer(), delay -> future.complete("UdooTokenAcquirer Deployment Complete"));
    }, response -> {
      client = WebClient.create(vertx);
      EventBus eb = vertx.eventBus();
      eb.consumer(ConstLib.UDOO_TOKEN_ADDRESS, message -> {
    	token = (String)message.body();
    	logger.info("Received a udoo token: [" + message.body() + "] at time " + new Date());
//    	vertx.setPeriodic(300000, id -> {
//    		pollData();
//    	});
    	
    	getSensorHistoryValue("33a84bf0ab9724cf90d20f464496d60aea877f3c4db8ccf465cf52f8e4d10f90","neo-231ab9d4e3167ab8","virtual","usbRFID");
      
      });
    });

  }

  public void pollData() {
    String query = "select gateway_id, device_id, sensor_type, sensor_id from sensor;";
	List<JsonObject> result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
	for (JsonObject jo: result) {
      String gateway_id = jo.getString("gateway_id");
      String device_id = jo.getString("device_id");
	  String sensor_type = jo.getString("sensor_type");
	  String sensor_id = jo.getString("sensor_id");
	  if (sensor_type.equals("virtual")) {
        getSensorHistoryValue(gateway_id, device_id, sensor_type, sensor_id);
	  }
    }
	lastTime = new Date();
  }

  public void getSensorHistoryValue(String gatewayId, String deviceId, String sensorType, String sensorId) {
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
	          body.put("lastTime", new SimpleDateFormat("yyyyMMddHHmm").format(new Date(lastTime.getTime() + 4 * HOUR)));
	          EventBus eb = vertx.eventBus();
	          eb.send(ConstLib.PARSER_ADDRESS, body);
	        } else {
	          logger.error("Something went wrong " + ar.cause().getMessage());
	        }
    });
  }

}
