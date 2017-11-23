package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

public class NormalDataPoller extends DataPollerHelper {
	// light sensor data detection is considered normal for now
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor left join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where sensor_topic_map.sensor_pk_id IS NULL or topic='/gamified_office/light';";
	private static final int frequency = 30000;
	private static NormalDataPoller pollerInstance;

	public static NormalDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new NormalDataPoller();
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
