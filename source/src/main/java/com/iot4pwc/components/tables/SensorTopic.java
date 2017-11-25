package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;
import java.sql.*;
import java.util.List;

public class SensorTopic extends Queriable {
  public static final String tableName = "sensor_topic_map";
  public static final String sensor_pk_id = "sensor_pk_id";
  public static final String topic = "topic";

  private static SensorTopic tableInstance;

  public static SensorTopic getInstance() {
    if (tableInstance == null) {
      return new SensorTopic();
    } else {
      return tableInstance;
    }
  }

  public void configureInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  ) {

  }

  public String getTableName() {
    return tableName;
  }
}
