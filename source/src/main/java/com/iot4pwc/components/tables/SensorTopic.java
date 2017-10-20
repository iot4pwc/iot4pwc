package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;

import java.sql.PreparedStatement;
import java.util.List;

public class SensorTopic implements Queriable {
  public static final String tableName = "sensor_topic_map";
  public static final String sensor_id = "sensor_id";
  public static final String topic = "topic";

  public void getInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  ) {

  }

  public String getTableName() {
    return tableName;
  }
}
