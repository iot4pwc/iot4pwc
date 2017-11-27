#!/bin/bash

sudo apt-get update
sudo apt-get install -y default-jre default-jdk maven mosquitto mosquitto-clients
sudo chmod 777 /etc/environment
sudo echo "JAVA_HOME=\"/usr/lib/jvm/java-8-openjdk-amd64/jre\"" >> /etc/environment
source /etc/environment
# export host for distributed eventbus
HOST=$(ip route get 8.8.8.8 | awk '{print $NF; exit}')
export HOST
# export mysql user name and passwd
DB_USER_NAME='iot4pwc'
export DB_USER_NAME
DB_USER_PW='Heinz123!'
export DB_USER_PW

# allow traffic on port 37288 for AWS instances
sudo ufw allow 37288
echo "#################################################################################"
echo "Please input the public ip and port of the MySQL instance: E.g 18.221.182.91:3306"
read MYSQL_URL
export MYSQL_URL
echo "#################################################################################"
echo "Please input the public DNS of the MQTT instance:"
echo "E.g ec2-18-221-127-99.us-east-2.compute.amazonaws.com"
read MQTT_URL
export MQTT_URL
