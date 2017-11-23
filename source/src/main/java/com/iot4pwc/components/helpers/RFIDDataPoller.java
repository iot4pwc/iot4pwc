package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

public class RFIDDataPoller extends DataPollerHelper {
	private static final String query = "select sensor.sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id, topic from sensor join sensor_topic_map on sensor.sensor_pk_id=sensor_topic_map.sensor_pk_id where topic='"+ConstLib.RFID_SENSOR_TOPIC+"';";
	private static final int frequency = 60000;
	private static RFIDDataPoller pollerInstance;

	public static RFIDDataPoller getInstance() {
	  if (pollerInstance == null) {
	    return new RFIDDataPoller();
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
