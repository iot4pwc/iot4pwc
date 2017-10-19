package com.iot4pwc.components.helpers;

import com.iot4pwc.components.tables.QueryTable;
import com.iot4pwc.constants.ConstLib;
import com.iot4pwc.verticles.DataService;
import com.mysql.jdbc.Statement;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DBHelper {
  private Connection connection;

  public DBHelper() {
    System.out.println("Creating a connection.");
    this.connection = getConnection();
    System.out.println("Created the connection, now returning");
  }

  public boolean insert(JsonObject recordObject, QueryTable table) {
    try {
      Statement statement = (Statement) connection.createStatement();

      List<String> fields = table.getFields();
      List<String> values = new LinkedList<>();

      
      String query = getQueryString(fields, table.getTableName(), recordObject);

      PreparedStatement preparedStatement = connection.prepareStatement(query);
      
      finalizePreparedStatement(preparedStatement, fields, recordObject);
      
//      preparedStatement.setTimestamp(1, new java.sql.Timestamp(recordObject.getLong("recorded_time")));
      preparedStatement.execute();

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public List<JsonObject> select(String query) {
    Statement statement;
    try {
      LinkedList<JsonObject> records = new LinkedList<>();

      statement = (Statement) connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMetaData = rs.getMetaData();
      int columnCount = rsMetaData.getColumnCount();

      while (rs.next()) {
        JsonObject record = new JsonObject();
        for (int i = 1; i <= columnCount; i++ ) {
          String field = rsMetaData.getColumnName(i);
          record.put(field, rs.getString(field));
        }
        records.add(record);
        rs.next();
      }

      return records;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private Connection getConnection() {
    if (connection == null) {
      String userName = System.getenv("DB_USER_NAME");
      String password = System.getenv("DB_USER_PW");

      try {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        connection =  DriverManager.getConnection(ConstLib.CONNECTION_STRING, userName, password);
        System.out.println(DataService.class.getName()+": Connected database successfully...");
        return connection;
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        return connection;
      }
    }

    return connection;
  }

  public void closeConnection() {
    if(connection!= null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      System.out.println(DataService.class.getName()+": Closed connection!");
    }
  }

  private String getQueryString(List<String> fields,  String tableName, JsonObject recordObject) {
    List<String> values = new LinkedList<String>();
    for (String field: fields) {
      // Changed this (10/19) to reflect that not all fields are castable to String by default
      if (field.contains("recorded_time")) {
        values.add("?");
      } else {
        values.add(recordObject.getValue(field).toString()+"");
      }
    }

    String query = "INSERT INTO " + tableName + " ( " + concatWithCommas(fields) + " ) VALUES ( " +  concatWithCommas(values) + " )"; 

    return query;
  }

  // Method to run over the finalized queryString, which is already stored in the preparedStatement.
  // Replace any ? with timestamps.
  // TODO Make better in 2.0
  private void finalizePreparedStatement(PreparedStatement preparedStatement, List<String> fields, JsonObject recordObject) throws SQLException {
    for (int i = 1; i <= fields.size(); i++) {
      if (fields.get(i-1).contains("recorded_time")) {
        preparedStatement.setTimestamp(i, new java.sql.Timestamp(recordObject.getLong("recorded_time")));
      }
    }
  }

  private String concatWithCommas(Collection<String> words) {
    StringBuilder wordList = new StringBuilder();
    for (String word : words) {
      wordList.append(word + ",");
    }
    return new String(wordList.deleteCharAt(wordList.length() - 1));
  }
}
