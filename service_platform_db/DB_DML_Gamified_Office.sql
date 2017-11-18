DROP DATABASE IF EXISTS gamified_office;
CREATE DATABASE gamified_office;

GRANT ALL PRIVILEGES ON gamified_office.* TO 'iot4pwc'@'%';

USE gamified_office;

DROP TABLE IF EXISTS participant_component_score;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS challenge_component;
DROP TABLE IF EXISTS challenge;

CREATE TABLE challenge(
  challenge_id int(10) AUTO_INCREMENT,
  start_date DATETIME,
  end_date DATETIME,
  CONSTRAINT challenge_pk PRIMARY KEY (challenge_id)
);

CREATE TABLE challenge_component(
  component_id int(10) AUTO_INCREMENT,
  challenge_id int(10),
  component_code varchar(20), 
  component_weight int(2), 
  component_desc varchar(100),
  CONSTRAINT challenge_component_pk PRIMARY KEY (component_id),
  CONSTRAINT challenge_component_fk FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE app_user(
  email varchar(255),
  name varchar(80),
  alias varchar(80),
  reg_date DATETIME,
  age int(3),
  profile_pic MEDIUMTEXT,
  share_flag varchar(1), 
  compete_flag varchar(1), 
  CONSTRAINT app_user_pk PRIMARY KEY (email)
);

CREATE TABLE user_component_sensor (
  `id` int(10) AUTO_INCREMENT,
  email VARCHAR(255) not null,
  component_code varchar(20) not null,
  sensor_value varchar(255) not null,
  CONSTRAINT user_component_sensor_pk PRIMARY KEY (`id`),
  CONSTRAINT user_component_sensor_fk FOREIGN KEY (email) REFERENCES app_user (email) 
);


CREATE TABLE participant(
  participant_id int(10) AUTO_INCREMENT,
  challenge_id int(10),
  email varchar(255),
  total_score int(5),
  today_score int(5),
  yesterday_score int(5),
  last_week_score int(5),
  last_month_score int(5),
  CONSTRAINT participant_pk PRIMARY KEY (participant_id),
  CONSTRAINT participant_fk_1 FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
  CONSTRAINT participant_fk_2 FOREIGN KEY (email) REFERENCES app_user (email) 
);

CREATE TABLE participant_component_score(
  part_comp_id int(10) AUTO_INCREMENT,
  component_id int(10),
  email varchar(255),
  total_score int(5),
  today_score int(5),
  yesterday_score int(5),
  last_week_score int(5),
  last_month_score int(5),
  CONSTRAINT participant_component_score_pk PRIMARY KEY (part_comp_id),
  CONSTRAINT participant_component_score_fk_1 FOREIGN KEY (component_id) REFERENCES challenge_component (component_id),
  CONSTRAINT participant_component_score_fk_2 FOREIGN KEY (email) REFERENCES app_user (email) 
);