package com.iot4pwc.verticles;

import java.util.*;

import com.iot4pwc.components.helpers.DBHelper;
import com.iot4pwc.components.helpers.MqttHelper;
import com.iot4pwc.components.publisheRequests.MosquittoPublishRequest;
import com.iot4pwc.components.publisheRequests.PublishRequestHandler;
import com.iot4pwc.components.tables.SensorTopic;
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
  private MqttHelper mqttHelper;
  private static Map<String, Set<String>> sensorTopicMapping = new HashMap<>();

  public void start() {
    EventBus eb = vertx.eventBus();

    WorkerExecutor executor = vertx.createSharedWorkerExecutor(
      ConstLib.DATA_PUBLISHER_WORKER_POOL,
      ConstLib.DATA_PUBLISHER_WORKER_POOL_SIZE
    );
    executor.executeBlocking (future -> {
      mqttHelper = new MqttHelper(ConstLib.MQTT_TLS_ENABLED);
      sensorTopicMapping = getSensorTopicMapping();
      future.complete();
    }, res ->
      eb.consumer(ConstLib.PUBLISHER_ADDRESS, message -> {
        JsonObject structuredDataJSON = (JsonObject)message.body();
        String structuredData = structuredDataJSON.toString();
        Set<String> sensorTopics = getTopicSet(structuredDataJSON);

        List<PublishRequestHandler> publishRequests = new LinkedList<>();
        if (sensorTopics != null && mqttHelper != null) {
          for (String topic : sensorTopics) {
            MosquittoPublishRequest request = new MosquittoPublishRequest(
              topic,
              structuredData,
              ConstLib.MQTT_QUALITY_OF_SERVICE
            );

            publishRequests.add(request);
          }
          mqttHelper.publish(publishRequests);
        }
      })
    );
  }

  public void stop() {
    mqttHelper.closeConnection();
  }

  private Map<String, Set<String>> getSensorTopicMapping() {
    Map<String, Set<String>> sensorTopicMap = new HashMap<>();
    String query = "SELECT * FROM sensor_topic_map";

    List<JsonObject> records = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);

    for (JsonObject record: records) {
      String sensorPkId = record.getString(SensorTopic.sensor_pk_id);
      String topic = record.getString(SensorTopic.topic);
      Set<String> topicSet = sensorTopicMap.getOrDefault(sensorPkId, new HashSet<>());
      topicSet.add(topic);
      sensorTopicMap.put(sensorPkId, topicSet);
    }
    return sensorTopicMap;
  }

  private void getOneSensorMapping(String sensorPkId, Map<String, Set<String>> existingMapping) {
    try {
      String query = "SELECT * FROM sensor_topic_map WHERE sensor_pk_id = " + sensorPkId;
      List<JsonObject> records = DBHelper.getInstance(ConstLib.SERVICE_PLATFORM).select(query);
      Set<String> sensorTopics = new HashSet<>();

      for (JsonObject record: records) {
        sensorTopics.add(record.getString(SensorTopic.topic));
      }

      existingMapping.put(sensorPkId, sensorTopics);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Set<String> getTopicSet(JsonObject jsonPayload) {
	String sensorPkID = jsonPayload.getString(SensorTopic.sensor_pk_id);
    Set<String> sensorTopics;

    if (!sensorTopicMapping.containsKey(sensorPkID)) {
      getOneSensorMapping(sensorPkID, sensorTopicMapping);
    }
    sensorTopics = sensorTopicMapping.getOrDefault(sensorPkID, new HashSet<>());

    return sensorTopics;
  } 
}