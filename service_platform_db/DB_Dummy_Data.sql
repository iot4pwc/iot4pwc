INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (1, 'Noise', 'Measures noise', 'NVT1276', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (2, 'Noise', 'Measures noise', 'NVT1276', '2016-7-04', '2020-8-15', 'Monroe Conference Room');

INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (3, 'Temperature', 'Measures temperature and humidity', 'THM186', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (4, 'Temperature', 'Measures temperature and humidity', 'THM186', '2016-7-04', '2020-8-15', 'Monroe Conference Room');

INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (5, 'Temperature', 'Measures temperature and humidity', 'THM186', '2016-7-04', '2020-8-15', 'Main lobby');

INSERT INTO sensor (sensor_id, sensor_type, sensor_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (6, 'Motion', 'Measures noise', 'NHDG582', '2016-7-04', '2020-8-15', 'Main enterence');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (1, '/noise/gates-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (2, '/noise/monroe-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (3, '/temperature/gates-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (3, '/humidity/gates-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (4, '/temperature/monroe-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (4, '/humidity/monroe-conf-room');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (5, '/temperature/main-lobby');

INSERT INTO sensor_topic_map (sensor_id, topic)
VALUES (6, '/motion/main-enterence');

INSERT INTO sensor_history (record_id, sensor_id, recorded_time, value_content)
VALUES (1, 1, CURRENT_TIMESTAMP, '{"noise_db": 64}');

INSERT INTO sensor_history (record_id, sensor_id, recorded_time, value_content)
VALUES (2, 1, CURRENT_TIMESTAMP, '{"noise_db": 61}');

INSERT INTO sensor_history (record_id, sensor_id, recorded_time, value_content)
VALUES (3, 1, CURRENT_TIMESTAMP, '{"noise_db": 80}');
