package com.iot4pwc.verticles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.helpers.RFIDDataPoller;
import com.iot4pwc.components.helpers.SittingDataPoller;
import com.iot4pwc.components.helpers.NormalDataPoller;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.PemTrustOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.codec.BodyCodec;

/**
 * This is a poller that polls data from the UDOO platform with an interval. 
 */

public class DataPoller extends AbstractVerticle {
  Logger logger = LogManager.getLogger(DataPoller.class);
  private WebClient client;
  private String token;
  private Date RFIDLastTime;
  private Date normalLastTime;
  private Date sittingLastTime;

  /**
   * Start the vercitle to retrieve data from Udoo platform.
   * Deploy the UdooTokenAcquirer verticle and start listening to the event bus for messages.
   * Once received token, poll data with intervals.
   */
  public void start() {
    try {
      RFIDLastTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(ConstLib.INITIAL_LAST_TIME);
	    normalLastTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(ConstLib.INITIAL_LAST_TIME);
      sittingLastTime = new SimpleDateFormat("yyyyMMddHHmmss").parse(ConstLib.INITIAL_LAST_TIME);
	  } catch (ParseException e) {
      logger.error("Error happend when parsing date " + e);
	  }

    vertx.executeBlocking(future -> {
      /**
       * First, deploy UdooTokenAcquirer verticle
       */
      vertx.deployVerticle(new UdooTokenAcquirer(), delay -> future.complete("UdooTokenAcquirer Deployment Complete"));
    }, response -> {
      client = WebClient.create(vertx,
        	    new WebClientOptions()
        	        .setTrustAll(true)
        	        .setSsl(true)
        	        .setPemTrustOptions(new PemTrustOptions().addCertPath(ConstLib.UDOO_CLOUD_CERT))
        	        .setFollowRedirects(true)
        	   );
      EventBus eb = vertx.eventBus();

      /**
       * Receive token and poll data with the token
       */
      eb.consumer(ConstLib.UDOO_TOKEN_ADDRESS, message -> {
        token = (String)message.body();
        logger.info("Received a udoo token: [" + message.body() + "] at time " + new Date());
    	  
        pollData(RFIDDataPoller.getInstance().getQuery(), RFIDLastTime);
        // poll rfid data on an interval of 1 minute
    	  long RFIDTimer = vertx.setPeriodic(RFIDDataPoller.getInstance().getFrequency(), id -> {
    	    pollData(RFIDDataPoller.getInstance().getQuery(), RFIDLastTime);
    	    RFIDLastTime = new Date();
    	  });
    	  
    	  pollData(NormalDataPoller.getInstance().getQuery(), normalLastTime);
        // poll other data on an interval of 30 seconds
    	  long normalTimer = vertx.setPeriodic(NormalDataPoller.getInstance().getFrequency(), id -> {
    	    pollData(NormalDataPoller.getInstance().getQuery(), normalLastTime);
    	    normalLastTime = new Date();
    	  });
    	  
        pollData(SittingDataPoller.getInstance().getQuery(), sittingLastTime);
        // poll sitting data on an interval of 1 minute
      	long sittingTimer = vertx.setPeriodic(SittingDataPoller.getInstance().getFrequency(), id -> {
      	  pollData(SittingDataPoller.getInstance().getQuery(), sittingLastTime);
      	  sittingLastTime = new Date();
      	});
      	  
        /**
         * will receive new token after one day, so cancel timer of this period
         */
      	vertx.setPeriodic(ConstLib.ONEDAY, id -> {
      	  vertx.cancelTimer(RFIDTimer);
      	  vertx.cancelTimer(normalTimer);
      	  vertx.cancelTimer(sittingTimer);
      	});
      });
    });

  }

  /**
   * For each record in database, poll data with relavant information
   * @params
   * query: String, the query String
   * lastTime: Date, the time indicating the last time retrieving data
   */  
  public void pollData(String query, Date lastTime) {
    List<JsonObject> result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
	  for (JsonObject jo: result) {
      String gateway_id = jo.getString("gateway_id");
      String device_id = jo.getString("device_id");
	    String sensor_type = jo.getString("sensor_type");
	    String sensor_id = jo.getString("sensor_id");
      String sensor_pk_id = jo.getString("sensor_pk_id");
      String topic = jo.getString("topic");
      if (topic == null) {
        getSensorHistoryValue(gateway_id, device_id, sensor_type, sensor_id, lastTime, sensor_pk_id, "normal");
      } else {
        if (topic.equals(ConstLib.SITTING_SENSOR_TOPIC)) {
          getSensorMinuteHistoryValue(gateway_id, device_id, sensor_type, sensor_id, lastTime, sensor_pk_id, topic);
        } else {
          getSensorHistoryValue(gateway_id, device_id, sensor_type, sensor_id, lastTime, sensor_pk_id, topic);    
        }       
      }
    }
  }

  /**
   * Get sensor realtime history values for one sensor and send to data parser
   * @params
   * gatewayId: String, the gateway_id for the sensor
   * deviceId: String, the device_id for the sensor
   * sensorType: String, the sensor_type for the sensor
   * sensorId: String, the sensor_id for the sensor
   * lastTime: Date, the time indicating the last time retrieving data
   * sensor_pk_id: String, the sensor_pk_id for the sensor
   * topic: String, the topic for the sensor
   */ 
  public void getSensorHistoryValue(String gatewayId, String deviceId, String sensorType, String sensorId, Date lastTime, String sensor_pk_id, String topic) {
	  client.getAbs(ConstLib.UDOO_ENDPOINT + "/ext/sensors/history/realtime/" + gatewayId +"/" + deviceId +"/" + sensorType +"/" +sensorId)
	        .putHeader("Authorization", "JWT " + token)
	        .as(BodyCodec.jsonObject())
	        .send(ar -> {
	          if (ar.succeeded()) {
	            HttpResponse<JsonObject> response = ar.result();
	            JsonObject body = response.body();
	            body.put("lastTime", new SimpleDateFormat("yyyyMMddHHmmss").format(lastTime));
	            body.put("sensor_pk_id", sensor_pk_id);
              body.put("topic", topic);
	            EventBus eb = vertx.eventBus();
	            eb.send(ConstLib.PARSER_ADDRESS, body);
	          } else {
	            logger.error("Something went wrong " + "in getSensorHistoryValue " + ar.cause().getMessage() + ". gatewayId: " +gatewayId + ", deviceId: " + deviceId + ", sensorType: " + sensorType + ", sensorId: " + sensorId);
	          }
          });
  }

  /**
   * Get sensor history values in minute format for one sensor and send to data parser
   * @params
   * gatewayId: String, the gateway_id for the sensor
   * deviceId: String, the device_id for the sensor
   * sensorType: String, the sensor_type for the sensor
   * sensorId: String, the sensor_id for the sensor
   * lastTime: Date, the time indicating the last time retrieving data
   * sensor_pk_id: String, the sensor_pk_id for the sensor
   * topic: String, the topic for the sensor
   */ 
  public void getSensorMinuteHistoryValue(String gatewayId, String deviceId, String sensorType, String sensorId, Date lastTime, String sensor_pk_id, String topic) {
	client.getAbs(ConstLib.UDOO_ENDPOINT + "/ext/sensors/history/minute/" + gatewayId +"/" + deviceId +"/" + sensorType +"/" +sensorId)
		  .putHeader("Authorization", "JWT " + token)
		  .as(BodyCodec.jsonObject())
		  .send(ar -> {
		    if (ar.succeeded()) {
		      HttpResponse<JsonObject> response = ar.result();
		      JsonObject body = response.body();
		      body.put("lastTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(lastTime.getTime()-60000)));
		      body.put("sensor_pk_id", sensor_pk_id);
          body.put("topic", topic);
		      EventBus eb = vertx.eventBus();
		      eb.send(ConstLib.PARSER_ADDRESS, body);
		    } else {
		      logger.error("Something went wrong " + "in getSensorMinuteHistoryValue " + ar.cause().getMessage() + ". gatewayId: " +gatewayId + ", deviceId: " + deviceId + ", sensorType: " + sensorType + ", sensorId: " + sensorId);
		    }
	      });
  }
  
}