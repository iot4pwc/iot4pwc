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
    connection = getConnection();
  }

  public boolean insert(JsonObject recordObject, QueryTable table) {
    try {
      Statement statement = (Statement) connection.createStatement();

      List<String> fields = table.getFields();

      String query = getQueryString(fields, table.getTableName(), recordObject);

      statement.execute(query);

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

  private String getQueryString(List<String> fields, String tableName, JsonObject recordObject) {
    List<String> values = new LinkedList<>();
    for (String field: fields) {
      values.add(recordObject.getString(field));
    }

    String query = String
      .format(
        "INSERT INTO %1 (%2) VALUES (%3)",
        tableName,
        concatWithCommas(fields),
        concatWithCommas(values)
      );

    return query;
  }

  private String concatWithCommas(Collection<String> words) {
    StringBuilder wordList = new StringBuilder();
    for (String word : words) {
      wordList.append(word + ",");
    }
    return new String(wordList.deleteCharAt(wordList.length() - 1));
  }
}
