DROP DATABASE service_platform;
CREATE DATABASE service_platform;
USE service_platform;

DROP USER 'iot4pwc'@'localhost';
FLUSH PRIVILEGES;
CREATE USER 'iot4pwc'@'localhost' IDENTIFIED BY 'Heinz123!';
GRANT ALL PRIVILEGES ON service_platform.* TO 'iot4pwc'@'localhost';

SET GLOBAL max_connections = 5000;
# for some reason dropping the tables will introduce error even with -f option
-- DROP TABLE sensor_topic_map;
-- DROP TABLE sensor_history;
-- DROP TABLE sensor;
-- DROP TABLE app_action_map;
-- DROP TABLE actuator_action_map;
-- DROP TABLE actuator;
-- DROP TABLE application;

CREATE TABLE sensor (
  sensor_id INT(10) AUTO_INCREMENT,
  sensor_type VARCHAR(20),
  sensor_desc VARCHAR(80),
  model_no VARCHAR(40),
  installed_on DATE,
  expiration_date DATE,
  install_loc VARCHAR(40),
  CONSTRAINT sensor_pk PRIMARY KEY(sensor_id)
);

CREATE TABLE sensor_topic_map(
  sensor_id INT(10),
  topic VARCHAR(40),
  CONSTRAINT sensor_topic_map_pk PRIMARY KEY(sensor_id, topic),
  CONSTRAINT sensor_topic_map_fk FOREIGN KEY (sensor_id) REFERENCES sensor(sensor_id)
);

CREATE TABLE sensor_history (
  record_id INT(10) AUTO_INCREMENT,
  sensor_id INT(10),
  recorded_time TIMESTAMP,
  value_content VARCHAR(200),
  CONSTRAINT sensor_history_pk PRIMARY KEY(record_id)
);

CREATE TABLE application (
  app_id INT(10) AUTO_INCREMENT,
  app_name VARCHAR(40),
  app_desc VARCHAR(80),
  app_owner VARCHAR(40),
  CONSTRAINT application_pk PRIMARY KEY(app_id)
);

CREATE TABLE actuator (
  act_id INT(10) AUTO_INCREMENT,
  act_type VARCHAR(20),
  act_desc VARCHAR(80),
  model_no VARCHAR(40),
  installed_on DATE,
  expiration_date DATE,
  install_loc VARCHAR(40),
  CONSTRAINT actuator_pk PRIMARY KEY(act_id)
);

CREATE TABLE actuator_action_map (
  record_id INT(10) AUTO_INCREMENT,
  act_id INT(10),
  action_code VARCHAR(20),
  action_desc VARCHAR(80),
  CONSTRAINT actuator_action_map_pk PRIMARY KEY(record_id),
  CONSTRAINT actuator_action_map_fk FOREIGN KEY (act_id) REFERENCES actuator(act_id)
);

CREATE TABLE app_action_map (
  app_id INT(10),
  record_id INT(10),
  CONSTRAINT app_action_map_pk PRIMARY KEY(app_id, record_id),
  CONSTRAINT app_action_map_fk_1 FOREIGN KEY (record_id) REFERENCES actuator_action_map(record_id),
  CONSTRAINT app_action_map_fk_2 FOREIGN KEY (app_id) REFERENCES application(app_id)
);