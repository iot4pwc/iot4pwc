USE service_platform;


INSERT INTO sensor (sensor_pk_id, sensor_id, sensor_type, sensor_desc, model_no, device_id, gateway_id, installed_on, expiration_date, install_loc)
VALUES ('59f11b9e3a8fd80d33e14e7c', 'usbRFID', 'virtual', 'Measures RFID', 'NVT1276', 'neo-231ab9d4e3167ab8', '33a84bf0ab9724cf90d20f464496d60aea877f3c4db8ccf465cf52f8e4d10f90', '2016-7-04', '2020-8-15', 'udooneo-LoungeRoom');

INSERT INTO sensor (sensor_pk_id, sensor_id, sensor_type, sensor_desc, model_no, device_id, gateway_id, installed_on, expiration_date, install_loc)
VALUES ('59f374113a8fd80d337f6ea8', '1-0029', 'i2c', 'Measures light', 'NVT1276', 'neo-161eb9d4e3167ab8', 'aa684c32171235abc466b7adab2cf8b47e23ed7dbf2a428165aa2cb99c830f41', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO sensor_topic_map (sensor_pk_id, topic)
VALUES ('59f11b9e3a8fd80d33e14e7c', '/gamified_office/rfid');

INSERT INTO sensor_topic_map (sensor_pk_id, topic)
VALUES ('59f374113a8fd80d337f6ea8', '/gamified_office/light');

INSERT INTO actuator (act_pk_id, act_id, act_type, act_desc, gateway_id, device_id, model_no, installed_on, expiration_date, install_loc)
VALUES ('59f8b56b3a8fd80d3397d6e2', '13', 'digital', 'light actuator', '82ccd7c9f70f23cbe570d1644f60a7293603fe95c5c51cabc6ee0de72f0df61d', 'ttyMCC-2125c1d4df669959', 'model1', '2016-7-04', '2020-8-15', 'Gates Conference Room');

INSERT INTO actuator_action_map(record_id, act_pk_id, action_code, action_desc)
VALUES(1, '59f8b56b3a8fd80d3397d6e2', '1', 'Turn on light');

INSERT INTO actuator_action_map(record_id, act_pk_id, action_code, action_desc)
VALUES(2, '59f8b56b3a8fd80d3397d6e2', '0', 'Turn off light');

INSERT INTO application(app_id, app_name, app_desc, app_owner)
VALUES(1, 'SmartTemp', 'Adapts HVAC and lighting to the need', 'Cisco');

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 1);

INSERT INTO app_action_map(app_id, record_id)
VALUES(1, 2);




USE information_broadcaster;

INSERT INTO room_info(room_id, room_name, room_type, room_location)
VALUES (1, 'Monroe Conference Room', 'conf-room', 'First Floor West Wing Corner Room'),
       (2, 'Gates Conference Room', 'conf-room', 'First Floor East Wing Corner Room'),
       (3, 'Executive Board Room', 'exec-room', 'Fourth Floor West Wing Corner Room');

INSERT INTO room_details(room_id, info_key, info_value, info_type)
VALUES (1, 'Television Instructions', 'url.com/tv-1', 'url'),
       (1, 'Bluejeans Instructions', 'Some Instructions Text', 'text'),
       (2, 'Television Instructions', 'url.com/tv-2', 'url');

INSERT INTO uuid_room(record_id, uuid, room_id)
VALUES (1, 'XCYBS', 1),
       (2, 'AFGTE', 2);

INSERT INTO room_occupancy(user_email, room_id, host_token)
VALUES ('cool@infobro.com', 1, ''),
       ('awesome@infobro.com', 1, ''),
       ('host@infobro.com', 1, 'DHEG4y5');

INSERT INTO user_detail(user_detail_id, user_email, info_key, info_value)
VALUES (1, 'cool@infobro.com', 'Name', 'Cool Guy'),
       (2, 'cool@infobro.com', 'Company', 'UDOOOOOO'),
       (3, 'awesome@infobro.com', 'Name', 'Awesome girl'),
       (4, 'host@infobro.com', 'Name', 'The wonderful host');
	   
INSERT INTO room_fileshare(fileshare_id, room_id, file_header, file_link, file_type)
VALUES (1, 1, 'Presentation', 'drive.google.com/ppt', 'Official Presentation');
