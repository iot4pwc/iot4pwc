package com.iot4pwc.constants;
import org.apache.logging.log4j.Level;

public class ConstLib {
  public static final String PARSER_ADDRESS = "iot4pwc.data.parser";
  public static final String PUBLISHER_ADDRESS = "iot4pwc.data.publisher";
  public static final String DATA_SERVICE_ADDRESS = "iot4pwc.data.dataService";
  public static final String ACTUATOR_ADDRESS = "iot4pwc.data.actuator";
  public static final String APP_AUTHENTICATOR_ADDRESS = "iot4pwc.data.appAuthenticator";
  public static final String UDOO_TOKEN_ADDRESS = "iot4pwc.udoo.token";

  public static final String DATA_SERVICE_WORKER_POOL = "data service pool";
  public static final String RESTFUL_DB_SERVICE_POOL = "restful service pool";
  public static final String DATA_PUBLISHER_WORKER_POOL = "data publisher pool";
  public static final int DATA_SERVICE_WORKER_POOL_SIZE = 5;
  public static final int DATA_PUBLISHER_WORKER_POOL_SIZE = 5;
  public static final int RESTFUL_DB_SERVICE_POOL_SIZE = 5;

  public static final int CLUSTER_EVENT_BUS_PORT = 37288;

  public static final int DUMMY_SENSOR_NUMBER = 10;
  public static final int DATA_PARSER_NUMBER = 10;
  public static final int DATA_PUBLISHER_NUMBER = 10;
  public static final int DATA_SERVICE_NUMBER = 10;
  public static final int DATA_POLLER_NUMBER = 1;
  public static final int RESTFUL_DB_SERVICE_NUMBER = 1;
  public static final int APP_AUTHENICATOR_NUMBER = 1;
  public static final int ACTUATOR_NUMBER = 1;

  public static final String SERVICE_PLATFORM_OPTION = "-sp";
  public static final String DATA_GENERATOR_OPTION = "-dg";

  public static final String MYSQL_CONNECTION_STRING = "jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false";
  public static final String SERVICE_PLATFORM = "service_platform";
  public static final String INFOMATION_BROADCASTER = "information_broadcaster";
  public static final String HIKARI_POOL_NAME = "DBHelper connection pool";
  public static final int HIKARI_MAX_POOL_SIZE = 4;
  public static final boolean HIKARI_CACHE_PSTMT = true;
  public static final int HIKARI_PSTMT_CACHE_SIZE = 256;
  public static final boolean HIKARI_USE_SERVER_PSTMT = true;

  public static final boolean MQTT_TLS_ENABLED = false;
  public static final String MQTT_BROKER_STRING = "tcp://%s:1883";
  public static final String MQTT_BROKER_TLS_STRING = "ssl://%s:8883";
  public static final String MQTT_CA_FILE = "/home/ubuntu/ca.crt";
  public static final String MQTT_TLS_VERSION = "TLSv1.2";
  public static final String MQTT_CLIENT_ID = "iot4pwc";
  public static final int MQTT_QUALITY_OF_SERVICE = 2;

  public static final String PAYLOAD_FIELD_APP_ID = "app_id";
  public static final String PAYLOAD_FIELD_ACTION_ID = "action_id";
  public static final String PAYLOAD_FIELD_ACTUATOR_ID = "actuator_id";
  
  public static final String UDOO_ENDPOINT = "https://cmu.udoo.cloud:443";
  public static final String UDOO_USERNAME = "cmu4pwc";
  public static final String UDOO_PWD = "CMU4pwc.";
  public static final String UDOO_ACTUATE_ENDPOINT = "http://udoo-iot-beta.cleverapps.io/ext/sensors/write/";
  public static final String UDOO_CLOUD_CERT = "udoo.cer";

  public static final Level LOGGING_LEVEL = Level.INFO;
  public static final String LOGGING_CONFIG = "log4j2.xml";

  public static final String PRIVATE_KEY_PATH = "ca.key";
  public static final String CERTIFICATE_PATH = "ca.crt";

  public static final int ONEDAY = 86400000;
  public static final String RFID_SENSOR_TYPE = "virtual";
  public static final String INITIAL_LAST_TIME = "201710010000";
}
