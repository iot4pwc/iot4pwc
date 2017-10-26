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

Deploy the fat .jar file:

```
java -jar servicePlatform-[VERSION_NUMBER]-SNAPSHOT-fat.jar
```

/// TODO

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc

