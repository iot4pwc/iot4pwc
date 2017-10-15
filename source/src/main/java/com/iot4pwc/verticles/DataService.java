package com.iot4pwc.verticles;

import com.iot4pwc.constants.ConstLib;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

// Required imports.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * This is a data service that persists the data to the database
 */
public class DataService extends AbstractVerticle{

  // All of these to be provided by Tarun.
  // Will move to ConstLib.java later.
  final String CONNECTION_STRING = "stefantestdb1.caqii6amhgcq.us-east-1.rds.amazonaws.com/all_logs";
  final String USER_NAME = "stefantestuser";
  final String USER_PW = "stefan123";

  public void start() {
    EventBus eb = vertx.eventBus();

    Connection conn = null;
    try {
      conn =  DriverManager.getConnection(CONNECTION_STRING, USER_NAME, USER_PW);


    } catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
    
    
    eb.consumer(ConstLib.DATA_SERVICE_ADDRESS, message -> {
      // structuredData is a JSON string

      String structuredData = (String)message.body();



    });
  }

  public void stop() {
    /**
     * clear up, feel free to delete this method if you think it's unnecessary
     */
  }



}
