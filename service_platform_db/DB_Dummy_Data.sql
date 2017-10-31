USE service_platform;

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

--- Info Bro

INSERT INTO room_info(room_id, room_name, room_type, room_location)
VALUES (1, 'Monroe Conference Room', 'conf-room', 'First Floor West Wing Corner Room'),
	   (2, 'Gates Conference Room', 'conf-room', 'First Floor East Wing Corner Room'),
       (3, 'Executive Board Room', 'exec-room', 'Fourth Floor West Wing Corner Room');

INSERT INTO room_details(room_id, info_key, info_value, value_type)
VALUES (1, 'Television Instructions', 'url.com/tv-1', 'url'),
       (1, 'Bluejeans Instructions', 'Some Instructions Text', 'text'),
       (2, 'Television Instructions', 'url.com/tv-2', 'url');

INSERT INTO uuid_room(record_id, uuid, room_id)
VALUES (1, 'XCYBS', 1),
       (2, 'AFGTE', 2);

INSERT INTO room_occupancy(user_email, room_id, is_host, host_token)
VALUES ('cool@infobro.com', 1, 'N', ''),
       ('awesome@infobro.com', 1, 'N', ''),
	   ('host@infobro.com', 1, 'Y', 'DHEG4y5');

INSERT INTO user_detail(user_detail_id, user_email, info_key, info_value)
VALUES (1, 'cool@infobro.com', 'Name', 'Cool Guy'),
       (2, 'cool@infobro.com', 'Company', 'UDOOOOOO'),
	   (3, 'awesome@infobro.com', 'Name', 'Awesome girl'),
	   (4, 'host@infobro.com', 'Name', 'The wonderful host');
	   
INSERT INTO room_fileshare(fileshare_id, room_id, file_header, file_link, file_type, hashed_host_token)
VALUES (1, 1, 'Presentation', 'drive.google.com/ppt', 'Official Presentation', '43tgvretreg34w532t3ytrgdt436');