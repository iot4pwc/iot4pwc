package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

public class SittingDataPoller {
	private static final String query = "select sensor_pk_id,gateway_id, device_id, sensor_type, sensor_id from sensor where sensor_pk_id="+"'"+ConstLib.SITTING_SENSOR_PK_ID+"';";
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
