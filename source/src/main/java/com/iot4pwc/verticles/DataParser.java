package com.iot4pwc.verticles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * This is a parser that will subscribe to an event published by DataPoller.
 * It will also publish formatted data to DataService for DB persistence and DataPublisher
 * for MQTT publishing.
 */
public class DataParser extends AbstractVerticle {

  Logger logger = LogManager.getLogger(DataParser.class);

  /**
   * Start the verticle to listen to the event bus for messages.
   * Retrieve each specific value and send to data service and data publisher.
   */
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.PARSER_ADDRESS, message -> {
      JsonObject data = (JsonObject) message.body();
      JsonArray value_type = (JsonArray)data.getValue("value_type");
      JsonArray history = (JsonArray)data.getValue("history");
      String lastTime = data.getString("lastTime");
      String sensor_pk_id = data.getString("sensor_pk_id");
      String topic = data.getString("topic");

      /**
       * One sensor may contain many types of value
       * Map them to different pairs of key and content
       */
      List<String> types = new ArrayList<String>();
      for (int i=0; i<value_type.size(); i++) {
    	types.add(value_type.getJsonObject(i).getString("desc"));
      }
      
      /**
       * Parse data to map database columns
       */
      for (int i=0; i<history.size(); i++) {
        JsonObject structuredDataBase = new JsonObject();
    	  JsonObject jo = history.getJsonObject(i);
    	  String timestamp = jo.getString("timestamp");

        /**
         * sample timestamp: 20171101153736(yyyyMMddHHmmss) or 201711011537(yyyyMMddHHmm)
         * since we get the whole history of values
         * we only keep records that happen after last time
         */

        if (lastTime.compareTo(timestamp) < 0) {
	        if (timestamp.length() == 12) {
            try {
              structuredDataBase.put("timestamp", new SimpleDateFormat("yyyyMMddHHmm").parse(timestamp).getTime());
            } catch (ParseException e) {
              logger.error(e);
            }            
          } else {
            try {
              structuredDataBase.put("timestamp", new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp).getTime());
            } catch (ParseException e) {
              logger.error(e);
            }           
          }

	        structuredDataBase.put("sensor_pk_id", sensor_pk_id);
	        for (String type: types) {
	          JsonObject structuredData = structuredDataBase;
	          String value = String.valueOf(jo.getValue(type));
	          structuredData.put("value_key", type);
	          structuredData.put("value_content", String.valueOf(value));
            /**
             * send each record to data service and data publisher
             */
		        eb.send(ConstLib.DATA_SERVICE_ADDRESS, structuredData);
		        eb.send(ConstLib.PUBLISHER_ADDRESS, structuredData);
	        }
	      } 
      }
    });
    
  }
  
}