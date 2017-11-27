package com.iot4pwc.components.tables;

import io.vertx.core.json.JsonObject;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * Abstract class that will be passed to DBHelper methods
 * Author: Xianru Wu
 */
public abstract class Queriable {
  /**
   * Get the table name
   */  
  public abstract String getTableName();
  
  /**
   * Inject parameters to a prepared statement, subclass must provide this method to DBHelper. This method will
   * hydrate the prepared statement based on the elements in the attributeNames. Refer to SensorHistory.java to
   * see an example.
   * @params
   * pstmt: PreparedStatement, a prepared statement that will be used by DBHelper
   * recordObject: JsonObject, a JSON object whose keys are attribute names and values are attribute values
   * attributeNames: List<String>, a list that contains all the attribute names.
   */  
  public abstract void configureInsertPstmt(
    PreparedStatement pstmt,
    JsonObject recordObject,
    List<String> attributeNames
  );
}
