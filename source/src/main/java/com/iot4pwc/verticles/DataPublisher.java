package com.iot4pwc.verticles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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
public class DataPublisher extends AbstractVerticle{

  Connection connection;

  Map<Integer, String> sensorTopicMapping = new HashMap<Integer, String>(); 

  public void start() {
    EventBus eb = vertx.eventBus();

    // Do all insertions to DB via a WorkerExecutor so to not block.
    WorkerExecutor executor = vertx.createSharedWorkerExecutor("my-worker-pool");

    // Execute this in the background.
    executor.executeBlocking (future -> {

      // We want this to block because it is in the startup only.
      Connection connection = getConnection();

      sensorTopicMapping = getSensorTopicMapping(connection);

      System.out.println(sensorTopicMapping.toString());

      // Consume from EventBus
      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        String structuredData = (String)message.body();

        JsonObject jsonObject = new JsonObject(structuredData);
        int sensorID = jsonObject.getInteger("sensorID");
        String topic ;

        if (sensorTopicMapping.containsKey(Integer.valueOf(sensorID))) {
          topic = sensorTopicMapping.get(Integer.valueOf(sensorID));
        } else {
          // Refresh map with just one record (for optimized DB performance, yo!)
          getOneSensorMapping(connection, sensorID, sensorTopicMapping);

          // Set sensible default
          topic = sensorTopicMapping.getOrDefault(Integer.valueOf(sensorID), "NO_TOPIC");
        }

        // Add topic to the JSON being forwarded to MQTT
        jsonObject.put("topic", topic);

        // Sysout
        System.out.println(this.getClass().getName()+": Sending to MQTT - " + jsonObject.toString());

        //@TODO Write to MQTT the newly updated JSON object.


      });

      // Future is complete, we can safely return to the main thread.
      future.complete();

      // Nothing to do with the response for now.
    }, res -> {
    });   

  }

  public void stop() {
    /**
     * clear up, feel free to delete this method if you think it's unnecessary
     */
  }

  // @TODO: This can be modular. Please create a ticket for this in 2.0
  private static Connection getConnection(){

    Connection connection = null;

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
      System.out.println(DataService.class.getName()+": Connecting to a selected database...");
      connection =  DriverManager.getConnection(ConstLib.CONNECTION_STRING, DB_USER_NAME, DB_USER_PW);
      System.out.println(DataService.class.getName()+": Connected database successfully...");
      return connection;
    } catch (SQLException ex) {
      System.out.println(DataService.class.getName()+": SQLException: " + ex.getMessage());
      System.out.println(DataService.class.getName()+": SQLState: " + ex.getSQLState());
      System.out.println(DataService.class.getName()+": VendorError: " + ex.getErrorCode());
      return null;
    }
  }

  // @TODO: This can be modular. Please create a ticket for this in 2.0
  private static void closeConnection (Connection connection) {
    if(connection!=null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connection = null;
      }
      System.out.println(DataService.class.getName()+": Closed connection!");
    }
  }

  private static Map<Integer, String> getSensorTopicMapping(Connection connection) {
    Map<Integer, String> tempMap = new HashMap<Integer, String>();

    Statement statement;
    try {
      // Execute a SQL query.
      statement = (Statement) connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM sensor_topic_mapping");

      // Loop over result set and update map
      while (resultSet.next()) {
        tempMap.put(resultSet.getInt("sensorid"), resultSet.getString("topic"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return tempMap;
  }

  private static void getOneSensorMapping(Connection connection, int sensorID, Map<Integer, String> existingMapping) {
    Statement statement;
    try {
      // Execute a SQL query.
      statement = (Statement) connection.createStatement();
      String sqlQueryString = "SELECT * FROM sensor_topic_mapping WHERE sensorid = " + sensorID; 
      ResultSet resultSet = statement.executeQuery(sqlQueryString);

      // Loop over result set and update map
      while (resultSet.next()) {
        existingMapping.put(resultSet.getInt("sensorid"), resultSet.getString("topic"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

} // end class
