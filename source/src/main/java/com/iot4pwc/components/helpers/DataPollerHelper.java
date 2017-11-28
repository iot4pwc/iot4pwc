package com.iot4pwc.components.helpers;

/**
 * An abstract helper class that to be extended by different kinds of sensor data pollers from Udoo platform
 * Author: Yan Wang
 */

public abstract class DataPollerHelper{
	
	/** 
	 * Get normal data poller frequency
	 */
	public abstract int getFrequency();
	/** 
	 * Get normal data poller select query
	 */
	public abstract String getQuery();
}