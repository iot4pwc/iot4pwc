package com.iot4pwc.components.tables;

import java.util.LinkedList;

/**
 * Model for sensor_history table
 */
public class SensorHistoryTable extends DBTable {
  private SensorHistoryTable __tableInstance;
  public LinkedList<String> fields;

  public String tableName = "sensor_history";
  public static final String sensorId = "sensor_id";
  public static final String value  = "value_content";
  public static final String time = "recorded_time";

  private SensorHistoryTable() {
    // DO NOT add fields you're not going to insert
    __tableInstance = new SensorHistoryTable(sensorId, value, time);
  }

  private SensorHistoryTable(String... args) {
    setFields(args);
  }

  public void setFields(String... args) {
    for (String field: args) {
      fields.add(field);
    }
  }

  public SensorHistoryTable getInstance() {
    if (__tableInstance != null) {
      return __tableInstance;
    } else {
      return new SensorHistoryTable();
    }
  }

  public LinkedList<String> getFields() {
    return fields;
  }
}
