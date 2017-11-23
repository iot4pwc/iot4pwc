package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

public class SittingDataPoller {
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where topic='"+ConstLib.SITTING_SENSOR_TOPIC+"';";
	private static final int frequency = 60000;
	private static SittingDataPoller pollerInstance;

	public static SittingDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new SittingDataPoller();
	  } else {
	    return pollerInstance;
      }
	}
	
	public int getFrequency() {
	  return frequency;
	}
	
	public String getQuery() {
		return query;
	}
}
