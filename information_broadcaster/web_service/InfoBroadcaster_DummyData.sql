-- meeting_room_info
INSERT INTO meeting_room_info (meeting_room_name, `key`, value, type) VALUES ('Big Conference Room', 'Instructions', 'Cool Shit', 'url');

INSERT INTO meeting_room_info (meeting_room_name, `key`, value, type)
VALUES ('Big Conference Room', 'Complaints', 'http://google.com/someOtherLink.pdf', 'url');

INSERT INTO meeting_room_info (meeting_room_name, `key`, value, type)
VALUES ('Big Conference Room', 'Feedback', 'http://google.com/giveSomeFeedback', 'url');

INSERT INTO meeting_room_info (meeting_room_name, `key`, value, type)
VALUES ('Small Conference Room', 'Instruction', 'Please close the light after 5', 'text');


-- meeting_room_files
INSERT INTO meeting_room_files (meeting_room_name, `key`, value, type, access_code)
VALUES ('Big Conference Room', 'Secret File', 'http://google.com/someSecretFile.pdf', 'url', 'Xianru123');

INSERT INTO meeting_room_files (meeting_room_name, `key`, value, type, access_code)
VALUES ('Small Conference Room', 'Not so secret file', 'http://google.com/someSecretFile.pdf', 'url', 'YanWang321');


-- user_name
INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'xianru@cmu.edu', 'First Name', 'Xianru', 'text');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'xianru@cmu.edu', 'Last Name', 'Wu', 'text');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'xianru@cmu.edu', 'Profile Pic', 'SOME FILE TOKEN', 'image');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'xianru@cmu.edu', 'Resume', 'http://google.com/XianruHasAResume.pdf', 'url');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'wangyan@cmu.edu', 'First Name', 'Yan', 'text');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'wangyan@cmu.edu', 'Last Name', 'Wang', 'text');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'wangyan@cmu.edu', 'Profile Pic', 'SOME OTHER FILE TOKEN', 'image');

INSERT INTO user (meeting_room_name, user_name, `key`, value, type) 
VALUES ('Big Conference Room', 'wangyan@cmu.edu', 'Resume', 'http://google.com/WangYanHasAResume.pdf', 'url');


-- meeting_room_occupancy
INSERT INTO meeting_room_occupancy (user, meeting_room_name, host_token)
VALUES ('wangyan@cmu.edu', 'Big Conference Room', '');

INSERT INTO meeting_room_occupancy (user, meeting_room_name, host_token)
VALUES ('xianru@cmu.edu', 'Big Conference Room', 'XianruHasTheToken');


-- uuid_meeting_room_url
INSERT INTO uuid_meeting_room_url (uuid, meeting_room_name, url)
VALUES ('asdklj123daslkje13@!@Fewn1349', 'Big Conference Room', 'soeCoolURL.html');