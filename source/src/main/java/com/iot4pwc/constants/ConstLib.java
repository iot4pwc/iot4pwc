package com.iot4pwc.constants;

public class ConstLib {
  public static final String PARSER_ADDRESS = "iot4pwc.data.parser";
  public static final String PUBLISHER_ADDRESS = "iot4pwc.data.publisher";
  public static final String DATA_SERVICE_ADDRESS = "iot4pwc.data.dataService";

  public static final String DATA_SERVICE_WORKER_POOL = "data service pool";
  public static final String DATA_PUBLISHER_WORKER_POOL = "data publisher pool";
  public static final int DATA_SERVICE_WORKER_POOL_SIZE = 40;
  public static final int DATA_PUBLISHER_WORKER_POOL_SIZE = 40;

  public static final int CLUSTER_EVENT_BUS_PORT = 37288;

  public static final int DUMMY_SENSOR_NUMBER = 20;
  public static final int DATA_PARSER_NUMBER = 20;
  public static final int DATA_PUBLISHER_NUMBER = 20;
  public static final int DATA_SERVICE_NUMBER = 20;
  public static final int RESTFUL_DB_SERVICE_NUMBER = 1;

  public static final String SERVICE_PLATFORM_OPTION = "-sp";
  public static final String DATA_GENERATOR_OPTION = "-dg";
  
  public static final String MYSQL_CONNECTION_STRING = "jdbc:mysql://18.221.182.91/service_platform";

  public static final String MQTT_HOST = "localhost";
  public static final int MQTT_PORT = 8883;
  public static final boolean MQTT_TLS_ENABLED = false;
  public static final String MQTT_BROKER_STRING = "tcp://ec2-18-221-127-99.us-east-2.compute.amazonaws.com:1883";
  public static final String MQTT_BROKER_TLS_STRING = "ssl://ec2-18-221-127-99.us-east-2.compute.amazonaws.com:8883";
  public static final String MQTT_CA_FILE = "/home/ubuntu/ca.crt";
  public static final String MQTT_TLS_VERSION = "TLSv1.2";
  public static final String MQTT_CLIENT_ID = "iot4pwc";
  public static final int MQTT_QUALITY_OF_SERVICE = 2;


}
