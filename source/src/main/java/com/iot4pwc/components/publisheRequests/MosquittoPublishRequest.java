package com.iot4pwc.components.publisheRequests;

import com.iot4pwc.components.helpers.MqttHelper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * A class that publish message to Mosquitto MQTT
 * Author: Xianru Wu
 */
public class MosquittoPublishRequest implements PublishRequestHandler {
  private String topic;
  private String message;
  private int qos;

  /**
   * Initialize the MosquittoPublishRequest
   * @params
   * topic: String, the topic of the message
   * message: String, the message
   * qos: int, quality of service
   */    
  public MosquittoPublishRequest(String topic, String message, int qos) {
    this.topic = topic;
    this.message = message;
    this.qos = qos;
  }

  
  /**
   * Publish the message
   * @params
   * mqttHelper: MqttHelper, the helper that will actualize this request
   */    
  public void handlePublish(MqttHelper mqttHelper) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(qos);
    try {
      MqttClient client = mqttHelper.getAliveClient();
      client.publish(topic, mqttMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
