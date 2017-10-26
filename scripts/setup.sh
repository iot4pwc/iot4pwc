#!/bin/bash

sudo apt-get update
sudo apt-get install -y default-jre default-jdk maven mosquitto mosquitto-clients
sudo chmod 777 /etc/environment
sudo echo "JAVA_HOME=\"/usr/lib/jvm/java-8-openjdk-amd64/jre\"" >> /etc/environment
source /etc/environment
wget https://bintray.com/artifact/download/vertx/downloads/vert.x-3.4.2-full.tar.gz
tar -zxf vert.x-3.4.2-full.tar.gz
export PATH=~/vertx/bin:$PATH
HOST=$(ip route get 8.8.8.8 | awk '{print $NF; exit}')
export HOST
DB_USER_NAME='iot4pwc'
export DB_USER_NAME
DB_USER_PW='Heinz123!'
export DB_USER_PW

PRIVATE_KEY_PATH='/home/ubuntu/ca.key'
export PRIVATE_KEY_PATH
CERTIFICATE_PATH='/home/ubuntu/ca.crt'
export CERTIFICATE_PATH

sudo ufw allow 37288
