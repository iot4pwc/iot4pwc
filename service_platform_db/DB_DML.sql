DROP TABLE sensor_topic_map;
DROP TABLE sensor_history;
DROP TABLE sensor;

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
  CONSTRAINT sensor_topic_map_fk FOREIGN KEY sensor_id REFERENCES sensor(sensor_id)
);

CREATE TABLE sensor_history (
  record_id INT(10) AUTO_INCREMENT,
  sensor_id INT(10),
  recorded_time TIMESTAMP,
  value_content VARCHAR(200),
  CONSTRAINT sensor_history_pk PRIMARY KEY(record_id)
)