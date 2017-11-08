USE information_broadcaster;

ALTER TABLE user_detail
MODIFY info_value MEDIUMTEXT;

/*CREATE TABLE user_img(
  user_email varchar(255),
  image MEDIUMTEXT,
  CONSTRAINT user_img_fk FOREIGN KEY (user_email) REFERENCES room_occupancy (user_email) ON DELETE CASCADE
);*/