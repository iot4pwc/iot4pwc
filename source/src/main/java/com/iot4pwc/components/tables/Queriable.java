package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;

import java.sql.PreparedStatement;
import java.util.List;

public interface Queriable {
  String getTableName();
  void getInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  );
}
