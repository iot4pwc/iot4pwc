package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

/**
 * A helper class that provides methods for RFID data poller polling data from Udoo platform
 * Author: Yan Wang
 */

public class RFIDDataPoller extends DataPollerHelper {
	/** 
	 * RFID sensor history is different from normal history, so we seperate them into different types
	 * Polling interval is set for 1 minute
	 */
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where topic='"+ConstLib.RFID_SENSOR_TOPIC+"';";
	private static final int frequency = 60000;
	private static RFIDDataPoller pollerInstance;

	/** 
	 * Get one RFID data poller instance
	 */
	public static RFIDDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new RFIDDataPoller();
	  } else {
	    return pollerInstance;
      }
	}

	/** 
	 * Get RFID data poller frequency
	 */	
	public int getFrequency() {
	  return frequency;
	}
	
	/** 
	 * Get RFID data poller select query
	 */
	public String getQuery() {
		return query;
	}

}
