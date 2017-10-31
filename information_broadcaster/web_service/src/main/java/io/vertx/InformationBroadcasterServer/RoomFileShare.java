package io.vertx.InformationBroadcasterServer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import io.vertx.core.json.JsonObject;

public class RoomFileShare extends Queriable {

  public static final String tableName = "room_fileshare";
  public static final String meetingRoom = "room_id";
  public static final String assetName = "file_header";
  public static final String value = "file_link";
  public static final String type = "file_type";
  private static RoomFileShare tableInstance;

  
  public static RoomFileShare getInstance() {
    if (tableInstance == null) {
      return new RoomFileShare();
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
          case meetingRoom: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
          case assetName: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
          case value: {
            pstmt.setString(counter++, recordObject.getString(attributeName));
            break;
          }
          case type: {
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
