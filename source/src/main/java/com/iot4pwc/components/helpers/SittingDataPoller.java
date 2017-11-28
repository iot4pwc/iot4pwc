package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

/**
 * A helper class that provides methods for RFID data poller polling data from Udoo platform
 * Author: Yan Wang
 */

public class SittingDataPoller {
	/** 
	 * Sitting duration sensor history is different from normal and RFID, so we seperate them into different types
	 * Polling interval is set for 1 minute
	 */
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where topic='"+ConstLib.SITTING_SENSOR_TOPIC+"';";
	private static final int frequency = 60000;
	private static SittingDataPoller pollerInstance;

	/** 
	 * Get one sitting duration data poller instance
	 */
	public static SittingDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new SittingDataPoller();
	  } else {
	    return pollerInstance;
      }
	}

	/** 
	 * Get sitting duration data poller frequency
	 */	
	public int getFrequency() {
	  return frequency;
	}
	
	/** 
	 * Get sitting duration data poller select query
	 */
	public String getQuery() {
		return query;
	}
}
