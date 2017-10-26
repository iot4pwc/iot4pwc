package com.iot4pwc.components.helpers;

import com.iot4pwc.components.tables.Queriable;
import com.iot4pwc.constants.ConstLib;
//import com.mysql.jdbc.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DBHelper {
  private static DBHelper instance;
  private HikariDataSource ds;

  private DBHelper(String mySQLConnectionString) {
    HikariConfig config = new HikariConfig();
    config.setPoolName(ConstLib.HIKARI_POOL_NAME);
    config.setJdbcUrl(mySQLConnectionString);
    config.setUsername(System.getenv("DB_USER_NAME"));
    config.setPassword(System.getenv("DB_USER_PW"));
    config.setMaximumPoolSize(ConstLib.HIKARI_MAX_POOL_SIZE);

    // caching
    config.addDataSourceProperty("cachePrepStmts", ConstLib.HIKARI_CACHE_PSTMT);
    config.addDataSourceProperty("prepStmtCacheSize", ConstLib.HIKARI_PSTMT_CACHE_SIZE);
    config.addDataSourceProperty("useServerPrepStmts", ConstLib.HIKARI_USE_SERVER_PSTMT);
    ds = new HikariDataSource(config);
  }

  public static DBHelper getInstance(String databaseName) {
    String MySQLConnectionString = String.format(
      ConstLib.MYSQL_CONNECTION_STRING,
      System.getenv("MYSQL_URL"),
      databaseName
    );
    if (DBHelper.instance == null) {
      DBHelper.instance = new DBHelper(MySQLConnectionString);
    }
    return DBHelper.instance;
  }


  public boolean insert(JsonObject recordObject, Queriable table) {
    try {
      Connection connection = ds.getConnection();
      PreparedStatement pstmt = getInsertStatement(table, recordObject, connection);
      pstmt.execute();
      connection.close();
      return true;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private PreparedStatement getInsertStatement(
    Queriable table,
    JsonObject recordObject,
    Connection connection
  ) throws SQLException {
    List<String> attributeNames = new LinkedList<>();
    StringBuilder attrSection = new StringBuilder();
    StringBuilder valueSection = new StringBuilder();

    for (Map.Entry<String, Object> entry : recordObject) {
      String attributeName = entry.getKey();
      attributeNames.add(attributeName);
      attrSection.append(attributeName + ",");
      valueSection.append("?,");
    }

    attrSection.deleteCharAt(attrSection.length() - 1);
    valueSection.deleteCharAt(valueSection.length() - 1);

    String query = String.format(
      "INSERT INTO %s (%s) VALUES (%s)",
      table.getTableName(),
      attrSection.toString(),
      valueSection.toString()
    );

    PreparedStatement preparedStatement = connection.prepareStatement(query);
    table.configureInsertPstmt(preparedStatement, recordObject, attributeNames);
    return preparedStatement;
  }

  public List<JsonObject> select(String query) {
    Statement statement;
    Connection connection = null;
    try {
      LinkedList<JsonObject> records = new LinkedList<>();

      connection = ds.getConnection();
      statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMetaData = rs.getMetaData();
      int columnCount = rsMetaData.getColumnCount();

      while (rs.next()) {
        JsonObject record = new JsonObject();
        for (int i = 1; i <= columnCount; i++) {
          String field = rsMetaData.getColumnName(i);
          record.put(field, rs.getString(field));
        }
        records.add(record);
      }

      return records;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }

  public void closeDatasource() {
    if (ds != null) {
      try {
        ds.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
