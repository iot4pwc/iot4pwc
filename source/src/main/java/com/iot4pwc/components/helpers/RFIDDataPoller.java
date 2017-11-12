package com.iot4pwc.components.helpers;
import com.iot4pwc.constants.ConstLib;

public class RFIDDataPoller extends DataPollerHelper {
	private static final String query = "select gateway_id, device_id, sensor_type, sensor_id from sensor where sensor_type="+"'"+ConstLib.RFID_SENSOR_TYPE+"';";
	private static final int frequency = 300000;
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
