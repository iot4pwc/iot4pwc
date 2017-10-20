package com.iot4pwc.components.helpers;

import com.iot4pwc.components.tables.Queriable;
import com.iot4pwc.constants.ConstLib;
import com.iot4pwc.verticles.DataService;
import com.mysql.jdbc.Statement;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBHelper {
  private Connection connection;

  public DBHelper() {
    System.out.println("Creating a connection.");
    this.connection = getConnection();
    System.out.println("Created the connection, now returning");
  }

  public boolean insert(JsonObject recordObject, Queriable table) {
    try {
      PreparedStatement pstmt = getInsertStatement(table, recordObject);
      pstmt.execute();
      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private PreparedStatement getInsertStatement(
    Queriable table,
    JsonObject recordObject
  ) throws SQLException {
    List<String> attributeNames = new LinkedList<>();
    String attrSection = "";
    String valueSection = "";

    for (Map.Entry<String, Object> entry: recordObject) {
      attributeNames.add(entry.getKey());
      attrSection += entry.getKey() + ",";
      valueSection += "?,";
    }

    String query = String.format(
      "INSERT INTO %1 (%2) VALUES (%3)",
      table.getTableName(),
      attrSection,
      valueSection
    );

    PreparedStatement preparedStatement = connection.prepareStatement(query);
    table.getInsertPstmt(preparedStatement, recordObject, attributeNames);
    return preparedStatement;
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
}
