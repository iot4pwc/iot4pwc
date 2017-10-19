package com.iot4pwc.components;

import com.iot4pwc.components.tables.DBTable;
import com.iot4pwc.components.tables.SensorHistoryTable;
import com.iot4pwc.constants.ConstLib;
import com.iot4pwc.verticles.DataService;
import com.mysql.jdbc.Statement;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DBHelper {
  private Connection connection;

  public DBHelper() {
    connection = getConnection();
  }

  public boolean insert(JsonObject recordObject, DBTable table) {
    try {
      Statement statement = (Statement) connection.createStatement();

      List<String> fields = table.getFields();

      String query = getQueryString(fields, table.tableName, recordObject);

      statement.execute(query);

      System.out.println(DataService.class.getName()+": Inserted records into the table...");

      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private Connection getConnection() {
    Connection connection = null;

    String userName = System.getenv("DB_USER_NAME");
    String password = System.getenv("DB_USER_PW");

    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      System.out.println(DataService.class.getName()+": Connecting to a selected database...");
      connection =  DriverManager.getConnection(ConstLib.CONNECTION_STRING, userName, password);
      System.out.println(DataService.class.getName()+": Connected database successfully...");
    } catch (SQLException ex) {
      System.out.println(DataService.class.getName()+": SQLException: " + ex.getMessage());
      System.out.println(DataService.class.getName()+": SQLState: " + ex.getSQLState());
      System.out.println(DataService.class.getName()+": VendorError: " + ex.getErrorCode());
    } finally {
      return connection;
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
