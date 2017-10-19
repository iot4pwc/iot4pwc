package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import com.mysql.jdbc.Statement;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;

// Required imports.
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;


/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle {

  //  // We want this to block because it is in the startup only.
  Connection connection;

  public void start() {
    EventBus eb = vertx.eventBus();

    // Do all insertions to DB via a WorkerExecutor so to not block.
    WorkerExecutor executor = vertx.createSharedWorkerExecutor("my-worker-pool");

    // Execute this in the background.
    executor.executeBlocking (future -> {

      // We want this to block because it is in the startup only.
      Connection connection = getConnection();

      // Consume from EventBus
      eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
        String structuredData = (String)message.body();
        boolean result = insertLog(connection, structuredData);
        System.out.println(DataService.class.getName()+": Insertion success: " + result);
        this.context.put("result", result);
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
    closeConnection(connection);
  }


  private static boolean insertLog(Connection connection, String logString) {
    try {
      Statement statement = (Statement) connection.createStatement();

      Timestamp stamp = new Timestamp(System.currentTimeMillis());
      Date date = new Date(stamp.getTime());

      String sqlInsert = "INSERT INTO logs " +
        "VALUES ( \'" + logString + "\' , \'" + date.toString() + "\')";

      statement.execute(sqlInsert);

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
