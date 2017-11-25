package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;
import java.sql.PreparedStatement;
import java.util.List;

public abstract class Queriable {
  public abstract String getTableName();
  public abstract void configureInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  );
}
