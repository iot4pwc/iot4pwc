#!/bin/bash

wget https://dev.mysql.com/get/mysql-apt-config_0.8.8-1_all.deb
wget https://github.com/iot4pwc/iot4pwc/blob/master/service_platform_db/DB_DML.sql
sudo dpkg -i mysql-apt-config*
sudo apt-get update
sudo apt-get install -y default-jre default-jdk maven mosquitto mysql-server
DB_USER_NAME="iot4pwc"
export DB_USER_NAME
DB_USER_PW="Heinz123!"
export DB_USER_PW
# you will need to set the passwd for root, remember that pwd

mysql -u root -p < DB_DML.sql -f
