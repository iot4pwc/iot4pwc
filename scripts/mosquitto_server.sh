#!/bin/bash

sudo apt-get update
sudo apt-get install -y mosquitto mosquitto-clients
# kill mosquitto
sudo kill $(ps aux |awk '/mosquitto/ {print $2}')
# get the config file from git
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/conf/mosquitto.conf > mosquitto.conf
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/scripts/TLS_cert_gen.sh > TLS_cert_gen.sh
chmod +x TLS_cert_gen.sh
# generate ssl keys and certificates
# use localhost because we don't want to introduce complicity by using a real host name
./TLS_cert_gen localhost
# activate mosquitto with tls
mosquitto -c mosquitto.conf -v
