package com.iot4pwc.components.tables;

import java.util.List;

public abstract class DBTable {
  public String tableName;
  public List<String> fields;

  abstract public void setFields(String... fields);
  abstract public DBTable getInstance();
  abstract public List<String> getFields();
}
