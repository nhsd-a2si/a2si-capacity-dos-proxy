# a2si-dos-proxy
This application primarily acts as a proxy between a DoS client and the DoS system itself.

It only takes additional processing when handling a Check Capacity Summary Response, 
the SOAP response to a DoS SOAP request of CheckCapacitySummary.

When processing CheckCapacitySummaryResponse the proxy uses the service id of each
ServiceCareSummaryDestination element in the response and calls the Capacity Service requesting
Capacity Information for that specific Service Id.

If Capacity Information is returned then this information is formatted into plain text
(albeit with carriage returns) and PREFIXED to the response's `<ns1:notes/>` field.


## Getting started
1. Set up your [Development Environment](docs/dev_setup.md)

2. Clone this repo to your local machine.

3. Follow the instructions below to configure and run the application.

## Application Dependencies
Before building this module the following modules must have been downloaded and built using `mvn clean install`
to add them into your local Maven Repository.

* [a2si-capacity-information](https://github.com/nhsd-a2si/a2si-capacity-information)
* [a2si-capacity-service-client](https://github.com/nhsd-a2si/a2si-capacity-service-client)

Detailed instructions are in the README of the respective repositories.

## Building the Application
```
cd {projectRoot}
mvn clean package
```

The Maven project builds two deployable artifacts:

+ the "Uber-Jar" common to a lot of applications built
using Spring Boot it contains Tomcat as a container within the jar as well as all of Tomcat's dependencies
and the application's dependencies

+ a zip file containing the "Uber-Jar" and a Dockerfile

### Configuring the Application

#### Logging
By default, Logback will write logs to a relative file path of `logs/a2si/a2si-capacity-service.log`

You can override the **`LOG_PATH`** variable by setting it as an environment variable e.g.
```bash
$ export LOG_PATH="/var/logs/a2si"
```

The log file names will be appended to the **`LOG_PATH`** value: 
+ `a2si-dos-wrapper.log` will be used for application logs. 
+ `a2si-dos-wrapper-soap.log` will be used for SOAP logs.

#### Spring Profiles
Following a best practice approach that comes from Spring configuration files hold data specific to
environments whilst wiring of class dependencies is done via Java Configuration classes. These classes
use the configuration files to set simple properties and are created when the application starts.

This requires an environment variable **`SPRING_PROFILES_ACTIVE`** to be included when building or starting up the capacity service.

There are several configuration files which relate to the different profiles used.

These are listed here with the corresponding profile values:

+ **application.yml**  
The default configuration provides defaults for the port the server runs under (7030) and the
default SOAP API URL suffix.

##### Using Stubbed DOS

+ **application-doswrapper-local-dos-stub-na-cpsc-rest-local.yml**  
`doswrapper-local-dos-stub-na-cpsc-rest-local`
  + Deployed: Local
  + DOS Client: Stub
  + Capacity Service: Local Rest API


+ **application-doswrapper-local-dos-stub-na-cpsc-stub-na.yml**  
`doswrapper-local-dos-stub-na-cpsc-stub-na`
  + Deployed: Local
  + DOS Client: Stub
  + Capacity Service: Stub


+ **application-doswrapper-aws-dos-stub-na-cpsc-rest-aws.yml**  
`doswrapper-aws-dos-stub-na-cpsc-rest-aws`  
  + Deployed: AWS
  + DOS Client: Stub
  + Capacity Service: AWS Rest API


+ **application-doswrapper-aws-dos-stub-na-cpsc-stub-na.yml**  
`doswrapper-aws-dos-stub-na-cpsc-stub-na`
  + Deployed: AWS
  + DOS Client: Stub
  + Capacity Service: Stub


##### Using Production DOS

+ **application-doswrapper-local-dos-soap-prod-cpsc-rest-local.yml**
`doswrapper-local-dos-soap-prod-cpsc-rest-local`
  + Deployed: Local
  + DOS Client: Production
  + Capacity Service: Local Rest API


+ **application-doswrapper-local-dos-soap-prod-cpsc-stub-na.yml**  
`doswrapper-local-dos-soap-prod-cpsc-stub-na`
  + Deployed: Local
  + DOS Client: Production
  + Capacity Service: Stub


+ **application-doswrapper-aws-dos-soap-prod-cpsc-rest-aws.yml**  
`doswrapper-aws-dos-soap-prod-cpsc-rest-aws`
  + Deployed: AWS
  + DOS Client: Production
  + Capacity Service: AWS Rest API


##### Using UAT DOS

+ **application-doswrapper-local-dos-soap-uat-cpsc-rest-local.yml**
`doswrapper-local-dos-soap-uat-cpsc-rest-local`
  + Deployed: Local
  + DOS Client: UAT
  + Capacity Service: Local Rest API


+ **application-doswrapper-local-dos-soap-uat-cpsc-stub-na.yml**  
`doswrapper-local-dos-soap-uat-cpsc-stub-na`
  + Deployed: Local
  + DOS Client: UAT
  + Capacity Service: Stub


+ **application-doswrapper-aws-dos-soap-uat-cpsc-rest-aws.yml**  
`doswrapper-aws-dos-soap-uat-cpsc-rest-aws`
  + Deployed: AWS
  + DOS Client: UAT
  + Capacity Service: AWS Rest API


+ **application-doswrapper-aws-dos-soap-uat-cpsc-stub-na.yml**  
`doswrapper-aws-dos-soap-uat-cpsc-stub-na`
  + Deployed: AWS
  + DOS Client: UAT
  + Capacity Service: Stub

##### Using with Wiremock

+ **application-doswrapper-local-dos-soap-local-wiremock-cpsc-rest-local.yml**  
`doswrapper-local-dos-soap-local-wiremock-cpsc-rest-local`
  + Deployed: Local
  + DOS Client: Local Wiremock
  + Capacity Service: Local Rest API


+ **application-doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na.yml**  
`doswrapper-local-dos-soap-local-wiremock-cpsc-stub-na`
  + Deployed: Local
  + DOS Client: Local Wiremock
  + Capacity Service: Stub


+ **application-doswrapper-aws-dos-soap-aws-wiremock-cpsc-rest-aws.yml**  
`doswrapper-aws-dos-soap-aws-wiremock-cpsc-rest-aws`
  + Deployed: AWS
  + DOS Client: AWS Wiremock
  + Capacity Service: AWS Rest API


+ **application-doswrapper-aws-dos-soap-aws-wiremock-cpsc-stub-na.yml**  
`doswrapper-aws-dos-soap-aws-wiremock-cpsc-stub-na`
  + Deployed: AWS
  + DOS Client: AWS Wiremock
  + Capacity Service: Stub


#### Listening Port
The application configuration file (application.yml) defines the port the service will run under as 7030.

```yaml
server:
  port: 7030
```


## Running the Application
The application is currently designed to be deployed in AWS using Elastic Beanstalk, using Docker as a container. 

Elastic Beanstalk allows Spring Boot applications to be packaged along with a DockerFile in a single zip file. This zip file is all that is required to deploy into AWS Elastic Beanstalk.

### Running Locally
To run the service locally you can specify the configuration properties on the command line instead of using environment variables:

```
java -Dspring.profiles.active=doswrapper-local-dos-soap-uat-cpsc-stub-na \
     -Dcapacity.service.api.username=dummyValue \
     -Dcapacity.service.api.password=dummyValue \
     -jar a2si-dos-wrapper-0.0.5-SNAPSHOT.jar
```

where the value of the `-jar` parameter is the filename of your built .jar file.


## Maintaining the Application
The main focus of the DoS Wrapper must always be to remain compatible with the DoS system it should always be possible
to point a DoS Client to the DoS Wrapper without any change other than the URL the DoS Client is using.

It is therefore important to possess a wsdl file that accurately reflects the DoS API itself. The build process for
the DoS Wrapper uses WSDL to generate client code

*Note that the existing DoS System is NOT fully SOAP compliant because errors (faults on SOAP parlance)
do not follow SOAP standards. The DoS Wrapper however is SOAP compliant so when DoS throws an error
an interceptor is used to tweak the SOAP Fault so that no invalid values are used that would cause the
DoS Wrapper to fail when validating the DoS response*


## Using the Application
When running the application two environment variables must be provided on the application startup
to define the Capacity Service API Client's username and password so calls to the Capacity Service
are correctly authenticated.

The application configuration files have the usernames and password to use and the values can be set on
a per profile basis.

The API endpoint for the Capacity Service depends on the deployment location but after the host address and port (7020) a URL of ```/capacity```
is used for all requests.

The SOAP endpoint is listening at `{BASE_URL}/nhsd/doswrapper/ws/pathwayService/` - SOAP requests are POSTED to this endpoint and will be proxied to the appropriate DOS endpoint transparently.