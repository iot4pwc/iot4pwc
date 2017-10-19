package com.iot4pwc.components.tables;

import java.util.List;

public abstract class DBTable {
  public String tableName;

  abstract public void setFields(String... fields);
  abstract public List<String> getFields();
}
