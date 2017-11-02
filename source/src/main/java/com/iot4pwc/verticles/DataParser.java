package com.iot4pwc.verticles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * This is a parser that will subscribe to an event published by DataPoller/DataListener.
 * It will also publish formatted data to DataService for DB persistence and DataPublisher
 * for MQTT publishing.
 */
public class DataParser extends AbstractVerticle {
  public void start() {
    EventBus eb = vertx.eventBus();

    eb.consumer(ConstLib.PARSER_ADDRESS, message -> {
      JsonObject data = (JsonObject) message.body();
      /**
       * implement business logic here.
       * reconstruct data to proper format for publishing and persisting.
       * suppose the result is a JSON string named structuredData.
       */
      JsonArray value_type = (JsonArray)data.getValue("value_type");
      JsonArray history = (JsonArray)data.getValue("history");
      String lastTime = data.getString("lastTime");
      
      List<String> types = new ArrayList<String>();
      for (int i=0; i<value_type.size(); i++) {
    	types.add(value_type.getJsonObject(i).getString("desc"));
      }
      
      for (int i=0; i<history.size(); i++) {
        JsonObject structuredData = new JsonObject();
    	JsonObject jo = history.getJsonObject(i);
    	System.out.println(jo);
    	String timestamp = jo.getString("timestamp");
    	
    	if (lastTime.compareTo(timestamp) <= 0) {
        	structuredData.put("timestamp", timestamp);
        	for (String type: types) {
              int value = jo.getInteger(type);
        	  structuredData.put(type, value);
        	}
        	structuredData.put("sensor_id", jo.getString("sensor_id"));
//        	eb.send(ConstLib.DATA_SERVICE_ADDRESS, structuredData);
//            eb.send(ConstLib.PUBLISHER_ADDRESS, structuredData);
    	}

      }
    });
    
  }
  
}
