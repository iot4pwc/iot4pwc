package com.iot4pwc.components.publisheRequests;

import com.iot4pwc.components.helpers.MqttHelper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PublishRequest implements PublishRequestHandler {
  private String topic;
  private String message;
  private int qos;

  public PublishRequest(String topic, String message, int qos) {
    this.topic = topic;
    this.message = message;
    this.qos = qos;
  }

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