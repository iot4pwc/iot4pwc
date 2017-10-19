package com.iot4pwc.verticles;

import java.util.*;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.helpers.MqttHelper;
import com.iot4pwc.components.tables.SensorTopic;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * This is a publisher that subscribes to an event published by DataParser and pass the
 * data to the MQTT under certain topic.
 */
public class DataPublisher extends AbstractVerticle {
  private DBHelper dbHelper;
  private MqttClient mqttClient = null;
  private static Map<Integer, Set<String>> sensorTopicMapping = new HashMap<>();

  public void start() {
    EventBus eb = vertx.eventBus();

    WorkerExecutor executor = vertx.createSharedWorkerExecutor(ConstLib.DATA_PUBLISHER_WORKER_POOL);
    executor.executeBlocking (future -> {
      dbHelper = new DBHelper();

      try {
        mqttClient = getMqttClient(ConstLib.MQTT_BROKER_STRING, this.getClass().getName());
      } catch (Exception e) {
        System.out.println(this.getClass().getName() + ": Unable to get MQTT TLSMQTTClient");
        e.printStackTrace();
      }

      sensorTopicMapping = getSensorTopicMapping();

      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        String structuredData = (String)message.body();

        JsonObject structuredDataJSON = new JsonObject(structuredData);
        Set<String> sensorTopics = getTopicSet(structuredDataJSON);
        

        if (sensorTopics != null && mqttClient != null) {
          // Loop over all topics, and publish one for each.
          for (String topic : sensorTopics) { 
            // TODO: Make mqttClient recover. Filed a bug for this (IOT-92)
            MqttHelper.publish(mqttClient, sensorTopics, structuredData, ConstLib.MQTT_QUALITY_OF_SERVICE);
          }
        }
      }
          );

      // Future is complete, we can safely return to the main thread.
      future.complete();

      // TODO: consider more functionality
    }, res -> {});
  }

  public void stop() {
    dbHelper.closeConnection();
  }

  private static MqttClient getMqttClient(String broker, String clientId) throws MqttException {
    // Not sure what to do with this persistence Setting, leaving default for now
    MemoryPersistence persistence = new MemoryPersistence();

    // Create the MqttClient object
    MqttClient tempClient = new MqttClient (broker, clientId, persistence);

    // Prepare for connection. Not sure what to do with this setting, leaving default for now
    MqttConnectOptions connOpts = new MqttConnectOptions();
    connOpts.setCleanSession(true);

    // Connect
    tempClient.connect(connOpts);

    // Return the connected TLSMQTTClient
    return tempClient;
  }

  // I suggest we keep it modular, in case we want to do more cleanup upon closing MqttClient later on.
  private void closeMqttClient(MqttClient mqttClient) throws MqttException {
    mqttClient.close();
  }

  private Map<Integer, Set<String>> getSensorTopicMapping() {
    Map<Integer, Set<String>> sensorTopicMap = new HashMap<>();
    String query = "SELECT * FROM sensor_topic_map";

    List<JsonObject> records = dbHelper.select(query);

    for (JsonObject record: records) {
      int sensorId = record.getInteger(SensorTopic.sensor_id);
      String topic = record.getString(SensorTopic.topic);
      Set<String> topicSet = sensorTopicMap.getOrDefault(sensorId, new HashSet<>());
      topicSet.add(topic);
      sensorTopicMap.put(sensorId, topicSet);
    }

    return sensorTopicMap;
  }

  private void getOneSensorMapping(int sensorId, Map<Integer, Set<String>> existingMapping) {
    try {
      String query = String.format("SELECT * FROM sensor_topic_map WHERE sensor_id = %1", sensorId);
      List<JsonObject> records = dbHelper.select(query);
      Set<String> sensorTopics = new HashSet<>();

      for (JsonObject record: records) {
        sensorTopics.add(record.getString(SensorTopic.topic));
      }

      existingMapping.put(sensorId, sensorTopics);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Set<String> getTopicSet(JsonObject jsonPayload) {
    int sensorID = jsonPayload.getInteger(SensorTopic.sensor_id);
    Set<String> sensorTopics;

    if (!sensorTopicMapping.containsKey(sensorID)) {
      getOneSensorMapping(sensorID, sensorTopicMapping);
    }
    sensorTopics = sensorTopicMapping.getOrDefault(sensorID, new HashSet<>());

    return sensorTopics;
  } 
}
