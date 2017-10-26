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

Deploy the fat .jar file locally:

```
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar
```
or, to just run parts of the platform:
```
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar -dg
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar -sp
```

## Built With

* [Vert.x](http://vertx.io/) - The Java framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Mosquitto](https://mosquitto.org/) - Used as a MQTT
* [Paho](https://www.eclipse.org/paho/) - Used to read from and write to the MQTT
* [DCloud HBuilder](https://dcloud.io/index.html) - Used for transpiling web code to Android
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
