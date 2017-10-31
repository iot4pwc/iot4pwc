package io.vertx.InformationBroadcasterServer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import io.vertx.core.json.JsonObject;

public class UserDetail extends Queriable {

  public static final String tableName = "user_detail";
  public static final String user = "user_email";
  public static final String asset = "info_key";
  public static final String value = "info_value";
  public static final String type = "info_type";
  private static UserDetail tableInstance;

  
  public static UserDetail getInstance() {
    if (tableInstance == null) {
      return new UserDetail();
    } else {
      return tableInstance;
    }
  }
  
  @Override
  public void configureInsertPstmt(PreparedStatement pstmt, JsonObject recordObject,
      List<String> attributeNames) {
    int counter = 1;
    try {
      for (String attributeName : attributeNames) {
        switch (attributeName) {
          case user: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
          case asset: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
          case value: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;          
          }
          case type: {
            // Not sure if we need a Blob here.
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;          
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
  }
  
  @Override
  public String getTableName() {
    return tableName;
  }
}
