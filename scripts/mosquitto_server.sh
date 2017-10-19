#!/bin/bash

sudo apt-get update
sudo apt-get install -y mosquitto mosquitto-clients
# kill mosquitto
sudo kill $(ps aux |awk '/mosquitto/ {print $2}')
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/conf/mosquitto.conf > mosquitto.conf
curl https://raw.githubusercontent.com/iot4pwc/iot4pwc/master/scripts/TLS_cert_gen.sh > TLS_cert_gen.sh
chmod +x TLS_cert_gen.sh
# use localhost because we don't want to introduce complicity by using a real host name
./TLS_cert_gen localhost
mosquitto -c mosquitto.conf -v
