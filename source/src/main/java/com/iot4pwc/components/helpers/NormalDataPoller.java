package com.iot4pwc.components.helpers;


public class NormalDataPoller extends DataPollerHelper {
	private static final String query = "select gateway_id, device_id, sensor_type, sensor_id from sensor where sensor_type!=\"virtual\";";
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