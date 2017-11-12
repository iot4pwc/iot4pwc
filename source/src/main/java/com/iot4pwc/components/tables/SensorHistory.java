package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SensorHistory extends Queriable {
  public static final String tableName = "sensor_history";
  public static final String sensor_num_id = "sensor_num_id";
  public static final String recorded_time = "recorded_time";
  public static final String value_key = "value_key";
  public static final String value_content = "value_content";
  public static final String record_id = "record_id";
  private static SensorHistory tableInstance;

  public static SensorHistory getInstance() {
    if (tableInstance == null) {
      return new SensorHistory();
    } else {
      return tableInstance;
    }
  }

  public void configureInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  ) {
    int counter = 1;
    try {
      for (String attributeName: attributeNames) {
        switch (attributeName) {
          case sensor_num_id: {
            pstmt.setInt(counter++, recordObject.getInteger(attributeName));
            break;
          }
          case recorded_time: {
            pstmt.setTimestamp(
              counter++,
              new java.sql.Timestamp(recordObject.getLong(attributeName))
            );
            break;
          }
          case value_key: {
              pstmt.setString(counter++, recordObject.getString(attributeName));
              break;
            }
          case value_content: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public String getTableName() {
    return tableName;
  }
}
