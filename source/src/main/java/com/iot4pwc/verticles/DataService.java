package com.iot4pwc.verticles;

import com.iot4pwc.components.tables.SensorHistory;
import com.iot4pwc.constants.ConstLib;
import com.mysql.jdbc.Statement;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle {
  private Connection connection;

  public void start() {
    EventBus eb = vertx.eventBus();

    WorkerExecutor executor = vertx.createSharedWorkerExecutor(ConstLib.DATA_SERVICE_WORKER_POOL);
    executor.executeBlocking (future -> {

      Connection connection = getConnection();

      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        String structuredData = (String)message.body();
        JsonObject structuredDataJSON = new JsonObject(structuredData);
        boolean result = insertLog(connection, structuredDataJSON);
        System.out.println(DataService.class.getName()+": Insertion success: " + result);
        // TODO: remove this is not useful
        this.context.put("result", result);
      });

      future.complete();
    }, res -> {
      // TODO: add response
    });
  }

  public void stop() {
    closeConnection(connection);
  }

  private static boolean insertLog(Connection connection, JsonObject jsonPayload) {
    try {
      Statement statement = (Statement) connection.createStatement();

      String insertLogQuery = String
        .format(
          "INSERT INTO sensor_history (sensor_id, value_content, recorded_time) VALUES (%1, %2, %3)",
          jsonPayload.getInteger(SensorHistory.sensor_id),
          jsonPayload.getString(SensorHistory.value_content),
          jsonPayload.getString(SensorHistory.recorded_time)
        );

      statement.execute(insertLogQuery);

      System.out.println(DataService.class.getName()+": Inserted records into the table...");

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  private static String getData(Connection connection, String sqlQuery) {
    System.out.println("Creating SELECT statement: " + sqlQuery);
    Statement statement;
    try {
      statement = (Statement) connection.createStatement();

      ResultSet resultSet = statement.executeQuery(sqlQuery);

      StringBuilder sb = new StringBuilder();

      while (resultSet.next()) {
        // System.out.println(resultSet.getString("log_message"));
        sb.append(resultSet.getString("log_message"));
        sb.append("\n");
        resultSet.next();
      }

      return sb.toString();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

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
}
