# Installation of Development Tools and Dependencies

## Install Git
Install Git for Windows: https://git-scm.com/download/win

Or install GitHub Desktop which also includes a GUI interface for git management: https://desktop.github.com/

Or use [Homebrew](https://docs.brew.sh/Installation) on MacOS: `brew install git`

## Install Java Development Kit 8:

Download the installer: http://www.oracle.com/technetwork/java/javase/downloads/

or use Homebrew on MacOS  
`brew cask install caskroom/versions/java8`

## Install Maven 3:
Download the installer: https://maven.apache.org/download.cgi

or use Homebrew on MacOS  
`brew install maven`

## Set Environment Variables
Ensure that the system environment variables for Java and Maven are set correctly.

*Note: If you are using MacOS and Homebrew it is possible you do not need to explicity set these environment variables - give it a go!*

1. `M2_HOME` should point to the install directory of your local Maven install folder, e.g.   
`C:\Maven\apache-maven-3.3.9`

2. `JAVA_HOME` should point to the install directory of your local Java JDK install folder, e.g.  
`C:\Program Files\Java\jdk1.8.0_121`

3. `PATH` should contain the bin directory of both `M2_HOME` and `JAVA_HOME`, e.g.  
`...;%JAVA_HOME%\bin;%M2_HOME%\bin;...`

## Test installations

Test Java installation: 

```bash
$ java -version

java version "1.8.0_172"
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)
```

Test Maven installation:

```bash
$ mvn -version

Apache Maven 3.5.3 (3383c37e1f9e9b3bc3df5050c29c8aff9f295297; 2018-02-24T19:49:05Z)
Maven home: /usr/local/Cellar/maven/3.5.3/libexec
Java version: 1.8.0_172, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_172.jdk/Contents/Home/jre
Default locale: en_GB, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.4", arch: "x86_64", family: "mac"
```