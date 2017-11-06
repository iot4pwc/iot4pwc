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
import com.iot4pwc.components.helpers.DBHelper;
/**
 * This is a parser that will subscribe to an event published by DataPoller/DataListener.
 * It will also publish formatted data to DataService for DB persistence and DataPublisher
 * for MQTT publishing.
 */
public class DataParser extends AbstractVerticle {
  Logger logger = LogManager.getLogger(DataParser.class);
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.PARSER_ADDRESS, message -> {
      JsonObject data = (JsonObject) message.body();
      JsonArray value_type = (JsonArray)data.getValue("value_type");
      JsonArray history = (JsonArray)data.getValue("history");
      String lastTime = data.getString("lastTime");
      
      /**
       * one sensor may contain many types of value
       * we map them to different pairs of key and content
       */
      List<String> types = new ArrayList<String>();
      for (int i=0; i<value_type.size(); i++) {
    	types.add(value_type.getJsonObject(i).getString("desc"));
      }
      
      /**
       * parse data to map database columns
       */
      for (int i=0; i<history.size(); i++) {
        JsonObject structuredDataBase = new JsonObject();
    	  JsonObject jo = history.getJsonObject(i);
    	  String timestamp = jo.getString("timestamp");
        String sensor_id = data.getString("sensor_id");
        String gateway_id = data.getString("gateway_id");
        String device_id = data.getString("device_id");
        String query = "select sensor_num_id from sensor where sensor_id='"+sensor_id+
        		       "' and gateway_id='"+gateway_id+"' and device_id='"+device_id+"';";
        List<JsonObject> result = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
        String sensor_id_pk = result.get(0).getString("sensor_num_id");

        /**
         * sample timestamp: 201711011537
         * since we get the whole history of values
         * we only keep records that happen after last time
         */
	      if (lastTime.compareTo(timestamp) <= 0) {
	        try {
		        structuredDataBase.put("timestamp", new SimpleDateFormat("yyyyMMddHHmm").parse(timestamp).getTime());
		      } catch (ParseException e) {
				    logger.error(e);
		      }
	        structuredDataBase.put("sensor_num_id", Integer.parseInt(sensor_id_pk));
	        for (String type: types) {
	          JsonObject structuredData = structuredDataBase;
	          int value = jo.getInteger(type);
	          structuredDataBase.put("value_key", type);
	          structuredDataBase.put("value_content", String.valueOf(value));
		        eb.send(ConstLib.DATA_SERVICE_ADDRESS, structuredData);
		        eb.send(ConstLib.PUBLISHER_ADDRESS, structuredData);
	        }
	      } 
      }
    });
    
  }
  
}
