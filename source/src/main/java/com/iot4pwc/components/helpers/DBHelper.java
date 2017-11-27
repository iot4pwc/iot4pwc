package com.iot4pwc.components.helpers;

import com.iot4pwc.components.tables.Queriable;
import com.iot4pwc.constants.ConstLib;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.json.JsonObject;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An singleton helper class that provide method for easier bulk db operations powered by Hikari (https://github.com/brettwooldridge/HikariCP)
 * Author: Xianru Wu
 */
public class DBHelper {
  private static DBHelper instance;
  private HikariDataSource ds;

  /**
   * Initialize the DBHelper.
   * @params
   * mySQLConnectionString: String, the MySQL connection String
   */  
  private DBHelper(String mySQLConnectionString) {
    HikariConfig config = new HikariConfig();
    config.setPoolName(ConstLib.HIKARI_POOL_NAME);
    config.setJdbcUrl(mySQLConnectionString);
    config.setUsername(System.getenv("DB_USER_NAME"));
    config.setPassword(System.getenv("DB_USER_PW"));
    config.setMaximumPoolSize(ConstLib.HIKARI_MAX_POOL_SIZE);
    config.addDataSourceProperty("cachePrepStmts", ConstLib.HIKARI_CACHE_PSTMT);
    config.addDataSourceProperty("prepStmtCacheSize", ConstLib.HIKARI_PSTMT_CACHE_SIZE);
    config.addDataSourceProperty("useServerPrepStmts", ConstLib.HIKARI_USE_SERVER_PSTMT);
    ds = new HikariDataSource(config);
  }

  /**
   * Get one helper instance
   * @params
   * databaseName: String, the database to interact with
   */    
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

  /**
   * Insert one record to the table
   * @params
   * recordObject: JsonObject, attr name/ attr value pairs
   * table: Queriable, the table to insert to
   */   
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

  /**
   * Get the insert prepared statement
   * @params
   * table: Queriable, the table to insert to
   * recordObject: JsonObject, attr name/ attr value pairs
   * connection: Connection, a Hikari connection
   */    
  private PreparedStatement getInsertStatement(
    Queriable table,
    JsonObject recordObject,
    Connection connection
  ) throws SQLException {
    List<String> attributeNames = new LinkedList<>();
    StringBuilder attrSection = new StringBuilder();
    StringBuilder valueSection = new StringBuilder();

    for (Map.Entry<String, Object> entry : recordObject) {
      if (!entry.getKey().equals("timestamp")) {
        String attributeName = entry.getKey();
        attributeNames.add(attributeName);
        attrSection.append(attributeName + ",");
        valueSection.append("?,");
      }
    }

    attrSection.deleteCharAt(attrSection.length() - 1);
    valueSection.deleteCharAt(valueSection.length() - 1);

    // populate query
    String query = String.format(
      "INSERT INTO %s (%s) VALUES (%s)",
      table.getTableName(),
      attrSection.toString(),
      valueSection.toString()
    );
    // an adhoc fix for timestamps
    if (recordObject.containsKey("timestamp")) {
    	query = String.format(
    		      "INSERT INTO %s (%s,recorded_time) VALUES (%s,?)",
    		      table.getTableName(),
    		      attrSection.toString(),
    		      valueSection.toString()
    		    );
    }
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    if (recordObject.containsKey("timestamp")) {
    	preparedStatement.setTimestamp(recordObject.size(), new Timestamp(recordObject.getLong("timestamp")));
    }
    table.configureInsertPstmt(preparedStatement, recordObject, attributeNames);
    return preparedStatement;
  }

  /**
   * Select query. Currently vulnerable to SQL injection due to using Statement, a comprimise made due to limited time.
   * @params
   * query: String, the query string
   */    
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

  /**
   * Delete query. Currently vulnerable to SQL injection due to using Statement, a comprimise made due to limited time.
   * @params
   * query: String, the query string
   */      
  public boolean delete(String query) {
    Statement statement;
    try (Connection connection = ds.getConnection()) {
      statement = connection.createStatement();
      statement.executeUpdate(query);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
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
