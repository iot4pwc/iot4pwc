package com.iot4pwc.components.helpers;

/**
 * A helper class that provides methods for normal data poller polling data from Udoo platform
 * Author: Yan Wang
 */

public class NormalDataPoller extends DataPollerHelper {
	/** 
	 * Light sensor data detection is considered normal for now
	 * Polling interval is set for 30 seconds
	 */
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor left join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where sensor_topic_map.sensor_pk_id IS NULL or topic='/gamified_office/light';";
	private static final int frequency = 30000;
	private static NormalDataPoller pollerInstance;

	/** 
	 * Get one normal data poller instance
	 */
	public static NormalDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new NormalDataPoller();
	  } else {
	    return pollerInstance;
      }
	}
	
	/** 
	 * Get normal data poller frequency
	 */
	public int getFrequency() {
	  return frequency;
	}

	/** 
	 * Get normal data poller select query
	 */
	public String getQuery() {
		return query;
	}
}
