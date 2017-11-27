package com.iot4pwc.components.publisheRequests;

import com.iot4pwc.components.helpers.MqttHelper;

/**
 * A handler class that handles publishing message to MQTT
 * Author: Xianru Wu
 */
public interface PublishRequestHandler {
  /**
   * Publish a message to MQTT
   * @params
   * mqttHelper: MqttHelper, the MQTT helper instance
   */    
  void handlePublish(MqttHelper mqttHelper);
}
