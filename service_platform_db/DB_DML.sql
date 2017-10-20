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