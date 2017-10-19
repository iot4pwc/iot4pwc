package com.iot4pwc.components.tables;

import java.util.LinkedList;

/**
 * Model for sensor_history table
 */
public class QueryTable {
  private LinkedList<String> fields;
  private String tableName;

  public QueryTable(String tableName, String... attributes) {
    this.tableName = tableName;
    fields = new LinkedList<>();
    for (String field: attributes) {
      fields.add(field);
    }
  }

  public LinkedList<String> getFields() {
    return fields;
  }

  public String getTableName() {
    return tableName;
  }
}
