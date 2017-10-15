package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import com.mysql.jdbc.Statement;
import io.vertx.core.AbstractVerticle;
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
public class DataService extends AbstractVerticle{

  // All of these to be provided by Tarun.
  // Will move to ConstLib.java later.
  final static String CONNECTION_STRING = "jdbc:mysql://stefantestdb1.caqii6amhgcq.us-east-1.rds.amazonaws.com/all_logs";
  final static String USER_NAME = "stefantestuser";
  final static String USER_PW = "stefan123";

  final Connection connection = getConnection();
  
  public void start() {
    EventBus eb = vertx.eventBus();


    eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
      // structuredData is a JSON string
      String structuredData = (String)message.body();
      
      System.out.println(String.format(this.getClass().getName()+": Received: %s", message.body()));
      
      insertLog(connection, structuredData);
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
      connection =  DriverManager.getConnection(CONNECTION_STRING, USER_NAME, USER_PW);
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
