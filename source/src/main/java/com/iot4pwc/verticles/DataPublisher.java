package com.iot4pwc.verticles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import com.iot4pwc.constants.ConstLib;
import com.mysql.jdbc.Statement;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;


/**
 * This is a publisher that subscribes to an event published by DataParser and pass the
 * data to the MQTT under certain topic.
 */
public class DataPublisher extends AbstractVerticle {

  private Connection connection;
  private MqttClient mqttClient = null;
  private static Map<Integer, Set<String>> sensorTopicMapping = new HashMap<Integer, Set<String>>(); 

  public void start() {
    EventBus eb = vertx.eventBus();

    // Do all insertions to DB via a WorkerExecutor so to not block.
    WorkerExecutor executor = vertx.createSharedWorkerExecutor("my-worker-pool");

    // Execute this in the background.
    executor.executeBlocking (future -> {

      // We want this to block because it is in the startup only.
      connection = getConnection();

      try {
        mqttClient = getMqttClient(ConstLib.MQTT_BROKER_STRING, this.getClass().getName());
      } catch (Exception e) {
        System.out.println(this.getClass().getName() + ": Unable to get MQTT Client");
        e.printStackTrace();
      }

      // Get the map of sensors to topic.
      sensorTopicMapping = getSensorTopicMapping(connection);

      // System.out.println(sensorTopicMapping.toString());

      // Consume from EventBus
      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        String structuredData = (String)message.body();

        JsonObject jsonObject = new JsonObject(structuredData);
        Set<String> sensorTopics = getTopicSet(jsonObject, connection);
        

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

      // Nothing to do with the response for now.
    }, res -> {}); // NOTE: I need to have an empty handler. Function won't work with just one param.

  }

  public void stop() {
    closeConnection(connection);

    try {
      closeMqttClient(mqttClient);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  // @TODO: This can be modular. Please create a ticket for this in 2.0
  private static Connection getConnection() {

    // Get a fresh connection
    Connection connection = null;

    // Get System environment variables.
    String DB_USER_NAME = System.getenv("DB_USER_NAME");
    String DB_USER_PW = System.getenv("DB_USER_PW");

    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    try { 
      System.out.println(DataPublisher.class.getName() + ": Connecting to a selected database...");
      connection =  DriverManager.getConnection(ConstLib.CONNECTION_STRING, DB_USER_NAME, DB_USER_PW);
      System.out.println(DataPublisher.class.getName() + ": Connected database successfully...");
      return connection;
    } catch (SQLException ex) {
      System.out.println(DataPublisher.class.getName() + ": SQLException: " + ex.getMessage());
      System.out.println(DataPublisher.class.getName() + ": SQLState: " + ex.getSQLState());
      System.out.println(DataPublisher.class.getName() + ": VendorError: " + ex.getErrorCode());
      return null;
    }
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

    // Return the connected Client
    return tempClient;
  }

  // TODO make a nicer clean-up.
  private static void closeConnection (Connection connection) {
    if(connection!=null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connection = null;
      }
      System.out.println(DataService.class.getName() + ": Closed connection!");
    }
  }

  // TODO -- I don't know if it will be one line later on. 
  // I suggest we keep it modular, in case we want to do more cleanup upon closing MqttClient later on. 
  private static void closeMqttClient(MqttClient mqttClient) throws MqttException {
    mqttClient.close();
  }

  private static Map<Integer, Set<String>> getSensorTopicMapping(Connection connection) {
    Map<Integer, Set<String>> tempMap = new HashMap<Integer, Set<String>>();

    Statement statement;
    try {
      // Execute a SQL query.
      statement = (Statement) connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM sensor_topic_map");

      // Loop over result set and update map
      while (resultSet.next()) {
        int aSensor = resultSet.getInt("sensor_id");
        Set<String> tempSet = tempMap.getOrDefault(aSensor, new HashSet<String>());
        tempSet.add(resultSet.getString("topic"));
        tempMap.put(aSensor, tempSet);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tempMap;
  }

  private static void getOneSensorMapping(Connection connection, int sensorID, Map<Integer, Set<String>> existingMapping) {
    Statement statement;
    try {
      // Execute a SQL query.
      statement = (Statement) connection.createStatement();
      String sqlQueryString = "SELECT * FROM sensor_topic_map WHERE sensor_id = " + sensorID; 
      ResultSet resultSet = statement.executeQuery(sqlQueryString);

      Set<String> tempSet = new HashSet<String>();

      // Loop over result set and update map
      while (resultSet.next()) {
        tempSet.add(resultSet.getString("topic"));
      }

      existingMapping.put(sensorID, tempSet);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  private static Set<String> getTopicSet(JsonObject jsonPayload, Connection connection) {
    int sensorID = jsonPayload.getInteger("sensorID");
    Set<String> sensorTopics = null;

    if (sensorTopicMapping.containsKey(sensorID)) {
      sensorTopics = sensorTopicMapping.get(sensorID);
    } else {
      // Refresh map with just one record (for optimized DB performance, yo!)
      getOneSensorMapping(connection, sensorID, sensorTopicMapping);

      // Set sensible default
      if (sensorTopicMapping.containsKey(sensorID)) {
        sensorTopics = sensorTopicMapping.get(sensorID);
      } 
    }
    
    return sensorTopics;
  } 
}
