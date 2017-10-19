package com.iot4pwc.components;

import java.util.Set;

import com.iot4pwc.verticles.DataPublisher;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttHelper {

  public static void publish(MqttClient mqttClient, Set<String> topicList, String message, int qualityOfService) {
    for (String topic : topicList) {
      publishToMqtt(mqttClient, topic, message, qualityOfService);
    }
  }
  
  private static void publishToMqtt(MqttClient mqttClient, String topic, String message, int qualityOfService) {
    MqttMessage mqttMessage = new MqttMessage(message.getBytes());
    mqttMessage.setQos(qualityOfService);
    try {
      mqttClient.publish(topic, mqttMessage);
      System.out.println(DataPublisher.class.getName() + ": Published message: " + message);
    } catch (MqttPersistenceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MqttException me) {
      // TODO Auto-generated catch block
      System.out.println("reason " + me.getReasonCode());
      System.out.println("msg " + me.getMessage());
      System.out.println("loc " + me.getLocalizedMessage());
      System.out.println("cause " + me.getCause());
      System.out.println("excep " + me);
    }
  }
  
}
