# IoT for PwC (iot4pwc)

This capstone project runs from September to December, 2017, with the goal of building a IoT test-bed at the Risk and Regulatory Services Innovation Center.

## Getting Started

There are three components to this project:
1. Service Platform
2. Information Broadcaster
3. Gamified Office

### Prerequisites

System requirements

```
Tested on Amazon EC2 t2.micro instance
```

### Installing the Service Platform

Get the most recent stable build and build the project:
```
git checkout master
cd src
mvn package
```

Note that you need to run scripts before you deploy the service. All scripts for service platform can be found in the scripts directory. 

On the instance that will start the service platform:
```
source setup.sh
```

On the instance the runs mysql:
```
source mysql_setup.sh
```

On the instance the runs mosquitto:
```
source mosquitto_server.sh
```

To run the service platform locally:
```
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar
```
or, to just run parts of the platform, either distributedly or locally:
```
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar -dg
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar -sp
```

Note that the distributed version of the service platform is powered by Hazelcast, which can be very platform specific. We can only guarantee that the latest version of service platform can be run on AWS instances that are under the same vnet. Refer to Hazelcast if you want to configure it to support multicast on your local distributed instances.

## Built With

* [Vert.x](http://vertx.io/) - The Java framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Mosquitto](https://mosquitto.org/) - Used as a MQTT
* [Paho](https://www.eclipse.org/paho/) - Used to read from and write to the MQTT
* [Hikari](https://github.com/brettwooldridge/HikariCP/) - Support for connection pool for MySQL
* MySQL

## Authors

* **Tarun Khandelwal** - *Project Manager*
* **Xianru Wu** - *Engineering Lead*
* **Lydia Li** - *Engineering IC*
* **Yan Wang** - *Engineering IC*
* **Giovanni Burresi** - *Hardware Lead*
* **Stefan Hermanek** - *Product Lead*


## License

[TBD]

## Acknowledgments

* [TBD]
