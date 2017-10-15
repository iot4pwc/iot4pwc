package com.iot4pwc.verticles;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.mysql.jdbc.Statement;

public class DataService_POJO {

  // All of these to be provided by Tarun.
  // Will move to ConstLib.java later.
  final static String CONNECTION_STRING = "jdbc:mysql://stefantestdb1.caqii6amhgcq.us-east-1.rds.amazonaws.com/all_logs";
  final static String USER_NAME = "stefantestuser";
  final static String USER_PW = "stefan123";

  @SuppressWarnings("deprecation")
  public static void main (String[] args) {

    Connection connection = null;

    connection = getConnection();

    try {
      long time;
      System.out.println("Now sleeping " + (time=3000) + "ms");
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    insertLog(connection, "hello there " + Instant.now().toString());

    System.out.println(getData(connection, "SELECT * FROM logs"));

    // Not yet working for some reason...
    System.out.println(getData(connection, "SELECT * FROM logs WHERE date > '" 
        + Instant.now().minus(1, ChronoUnit.MINUTES).toString() + "'"));

    closeConnection(connection);
  }

  public static boolean insertLog(Connection connection, String logString) {
    try {
      Statement statement = (Statement) connection.createStatement();

      Timestamp stamp = new Timestamp(System.currentTimeMillis());
      Date date = new Date(stamp.getTime());

      String sqlInsert = "INSERT INTO logs " +
          "VALUES ( \'" + logString + "\' , \'" + date.toString() + "\')";

      statement.execute(sqlInsert);

      System.out.println("Inserted records into the table...");

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  public static String getData(Connection connection, String sqlQuery) {
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

  public static Connection getConnection(){

    Connection connection = null;

    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (ClassNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    try { 
      System.out.println("Connecting to a selected database...");
      connection =  DriverManager.getConnection(CONNECTION_STRING, USER_NAME, USER_PW);
      System.out.println("Connected database successfully...");
      return connection;
    } catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
      return null;
    }
  }

  public static void closeConnection (Connection connection) {
    if(connection!=null)
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connection = null;
      }
    System.out.println("Closed connection!");
  }
}
