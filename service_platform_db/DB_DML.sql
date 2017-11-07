DROP DATABASE service_platform;
CREATE DATABASE service_platform;

USE service_platform;

DROP DATABASE information_broadcaster;
CREATE DATABASE information_broadcaster;

DROP USER 'iot4pwc'@'%';
FLUSH PRIVILEGES;
CREATE USER 'iot4pwc'@'%' IDENTIFIED BY 'Heinz123!';
GRANT ALL PRIVILEGES ON service_platform.* TO 'iot4pwc'@'%';
GRANT ALL PRIVILEGES ON information_broadcaster.* TO 'iot4pwc'@'%';

SET GLOBAL max_connections = 5000;


DROP TABLE IF EXISTS sensor_topic_map;
DROP TABLE IF EXISTS sensor_history;
DROP TABLE IF EXISTS sensor;
DROP TABLE IF EXISTS app_action_map;
DROP TABLE IF EXISTS actuator_action_map;
DROP TABLE IF EXISTS actuator;
DROP TABLE IF EXISTS application;


CREATE TABLE sensor (
  sensor_num_id INT(10) AUTO_INCREMENT,
  sensor_id VARCHAR(20),
  sensor_type VARCHAR(20),
  sensor_desc VARCHAR(80),
  model_no VARCHAR(40),
  device_id VARCHAR(30),
  gateway_id VARCHAR(80),
  installed_on DATE,
  expiration_date DATE,
  install_loc VARCHAR(40),
  CONSTRAINT sensor_pk PRIMARY KEY(sensor_num_id)
);


CREATE TABLE sensor_topic_map(
  sensor_num_id INT(10),
  topic VARCHAR(40),
  CONSTRAINT sensor_topic_map_pk PRIMARY KEY(sensor_num_id, topic),
  CONSTRAINT sensor_topic_map_fk FOREIGN KEY (sensor_num_id) REFERENCES sensor(sensor_num_id)
);


CREATE TABLE sensor_history (
  record_id INT(10) AUTO_INCREMENT,
  sensor_num_id INT(10),
  recorded_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  value_key VARCHAR(200),
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
  act_num_id INT(10) AUTO_INCREMENT,
  act_id VARCHAR(30),
  act_type VARCHAR(20),
  act_desc VARCHAR(80),
  model_no VARCHAR(40),
  device_id VARCHAR(50),
  gateway_id VARCHAR(80),
  installed_on DATE,
  expiration_date DATE,
  install_loc VARCHAR(40),
  CONSTRAINT actuator_pk PRIMARY KEY(act_num_id)
);


CREATE TABLE actuator_action_map (
  record_id INT(10) AUTO_INCREMENT,
  act_num_id INT(10),
  action_code VARCHAR(20),
  action_desc VARCHAR(80),
  CONSTRAINT actuator_action_map_pk PRIMARY KEY(record_id),
  CONSTRAINT actuator_action_map_fk FOREIGN KEY (act_num_id) REFERENCES actuator(act_num_id)
);


CREATE TABLE app_action_map (
  app_id INT(10),
  record_id INT(10),
  CONSTRAINT app_action_map_pk PRIMARY KEY(app_id, record_id),
  CONSTRAINT app_action_map_fk_1 FOREIGN KEY (record_id) REFERENCES actuator_action_map(record_id),
  CONSTRAINT app_action_map_fk_2 FOREIGN KEY (app_id) REFERENCES application(app_id)
);





USE information_broadcaster;

DROP TABLE IF EXISTS room_fileshare;
DROP TABLE IF EXISTS user_detail;
DROP TABLE IF EXISTS room_occupancy;
DROP TABLE IF EXISTS uuid_room;
DROP TABLE IF EXISTS room_details;
DROP TABLE IF EXISTS room_info;


CREATE TABLE room_info (
  room_id int(10) auto_increment,
  room_name varchar(255),
  room_type varchar(255),
  room_location varchar(255),
  CONSTRAINT room_info_pk PRIMARY KEY(room_id)
);


CREATE TABLE room_details (
  room_id int(10),
  info_key varchar(50),
  info_value varchar(1000),
  info_type varchar(50),
  CONSTRAINT room_details_pk PRIMARY KEY (room_id, info_key)
);


CREATE TABLE uuid_room (
  record_id int(10) NOT NULL auto_increment,
  uuid varchar(255),
  room_id int(10),
  CONSTRAINT uuid_room_pk PRIMARY KEY(record_id),
  CONSTRAINT uuid_room_fk FOREIGN KEY (room_id) REFERENCES room_info (room_id)
);


CREATE TABLE room_occupancy (
  user_email varchar(255),
  room_id int(10),
  host_token varchar(255),
  CONSTRAINT room_occupancy_pk PRIMARY KEY(user_email),
  CONSTRAINT room_occupancy_fk FOREIGN KEY (room_id) REFERENCES room_info (room_id)
);


CREATE TABLE user_detail (
  user_detail_id int(10) AUTO_INCREMENT,
  user_email varchar(255),
  info_key varchar(50),
  info_value varchar(1000),
  info_type varchar(50),
  CONSTRAINT user_detail_pk PRIMARY KEY(user_detail_id),
  CONSTRAINT user_detail_fk FOREIGN KEY (user_email) REFERENCES room_occupancy (user_email) ON DELETE CASCADE
);


CREATE TABLE room_fileshare (
  fileshare_id int(10) auto_increment,
  room_id int(10),
  file_header varchar(255),
  file_link varchar(255),
  file_type varchar(255),
  CONSTRAINT room_fileshare_pk PRIMARY KEY(fileshare_id),
  CONSTRAINT room_fileshare_fk FOREIGN KEY (room_id) REFERENCES room_info (room_id)
);