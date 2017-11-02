package com.iot4pwc.components.helpers;


public interface DataPollerHelper{

   void pollData();
	  
   void getSensorHistoryValue(String gatewayId, String deviceId, String sensorType, String sensorId);
   
}
