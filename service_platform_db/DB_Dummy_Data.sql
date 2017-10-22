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
VALUES (6, 'Motion', 'Measures noise', 'NHDG582', '2016-7-04', '2020-8-15', 'Main entrance');

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

INSERT INTO actuator (act_id, act_type, act_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (1, 'HVAC', 'Controls HVAC', 'NVT1276', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO actuator (act_id, act_type, act_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (2, 'HVAC', 'Controls HVAC', 'NVT1276', '2016-7-04', '2020-8-15', 'Monroe Conference Room');

INSERT INTO actuator (act_id, act_type, act_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (3, 'Light', 'Controls General Space Lighting', 'NVT1276', '2016-7-04', '2020-8-15', 'Main Lobby');

INSERT INTO actuator (act_id, act_type, act_desc, model_no, installed_on, expiration_date, install_loc)
VALUES (4, 'Humidity', 'Controls humidity level', 'NVT1276', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(1, 1, 'on', 'Turn on HVAC');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(2, 1, 'off', 'Turn off HVAC');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(3, 1, 'inc_temp', 'Increase room temperature by 1 deg Faranheit');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(4, 1, 'dec_temp', 'Decrease room temperature by 1 deg Faranheit');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(5, 3, 'bright', 'Increase brightness');

INSERT INTO actuator_action_map(record_id, act_id, action_code, action_desc)
VALUES(6, 3, 'dim', 'Decrease brightness');

INSERT INTO application(app_id, app_name, app_desc, app_owner)
VALUES(1, 'SmartTemp', 'Adapts HVAC and lighting to the need', 'Cisco');

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 3);

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 4);

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 5);

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 6);