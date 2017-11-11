DROP DATABASE IF EXISTS gamified_office;
CREATE DATABASE gamified_office;

USE gamified_office;

CREATE TABLE challenge(
  challenge_id int(10) AUTO_INCREMENT,
  start_date DATETIME,
  end_date DATETIME,
  CONSTRAINT challenge_pk PRIMARY KEY (challenge_id)
);

CREATE TABLE challenge_component(
  component_id int(10) AUTO_INCREMENT,
  challenge_id int(10),
  component_code varchar(20), /*component means what all does challenge measures. Ex- sitting and water would be two components.*/
  component_weight int(2), /*weight out of hundred (50 means weight is 0.5)*/
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
  share_flag varchar(1), /*do you want to share your ranking info with others? If not, you will not be able to see anyone's ranking too.*/
  compete_flag varchar(1), /*do you want to compete? If no, admin will not put you in any challenge.*/
  CONSTRAINT app_user_pk PRIMARY KEY (email)
);

CREATE TABLE participant(
  participant_id int(10) AUTO_INCREMENT,
  challenge_id int(10),
  email varchar(255),
  /*Following scores are redundant but just saved for faster lookup*/
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
  /*Store component wise score to have more info that can be shown.*/
  total_score int(5),
  today_score int(5),
  yesterday_score int(5),
  last_week_score int(5),
  last_month_score int(5),
  CONSTRAINT participant_component_score_pk PRIMARY KEY (part_comp_id),
  CONSTRAINT participant_component_score_fk_1 FOREIGN KEY (component_id) REFERENCES challenge_component (component_id),
  CONSTRAINT participant_component_score_fk_2 FOREIGN KEY (email) REFERENCES app_user (email) 
);