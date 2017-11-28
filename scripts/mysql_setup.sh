#!/bin/bash

wget https://dev.mysql.com/get/mysql-apt-config_0.8.8-1_all.deb
# get the sql file from github
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/service_platform_db/DB_DML.sql > DB_DML.sql
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/service_platform_db/DB_Dummy_Data.sql > DB_Dummy_Data.sql
sudo dpkg -i mysql-apt-config*
sudo apt-get update
sudo apt-get install -y mysql-server
# export username to env for service platform to use
DB_USER_NAME="iot4pwc"
export DB_USER_NAME
DB_USER_PW="Heinz123!"
export DB_USER_PW
# you will need to set the passwd for root, remember that pwd

mysql -u root -p < DB_DML.sql -f
mysql -u root -p < DB_Dummy_Data.sql -f
# override mysql conf file
sudo mv ../conf/my.cnf /etc/mysql/my.cnf
sudo service mysql restart
