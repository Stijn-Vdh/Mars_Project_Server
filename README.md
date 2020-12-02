# Martian Travel Tube System - SERVER

###### This is the server of the Martian Travel Tube System

## Table of Contents
- [Introduction](#introduction)
- [Getting Started](#Getting Started)
- [Usage](#Usage)
- [Features](#features)
- [API](#API)

## Introduction
Together with a team of researchers and developers, we want to build an open web application that will allow 
users/clients to travel to different endpoints on Mars and allow businesses to use pods for product transportation.
 
## Getting Started
### Prerequisites
Make sure you have:
* [Java 11 SDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [Gradle](https://gradle.org/)

Clone the project!
```shell script
git clone https://git.ti.howest.be/TI/2020-2021/s3/project-ii/projects/groep-15/server.git
```

## Usage
### Starting the server
To start the server simple press 'run' in the gradle tab > Tasks > application > run
on the rightHand side (intelliJ) or in the shell go:

```shell script
gradle run
```

## Features
H2 database gets generated when the application starts so no other actions are needed.

## API
We use an openAPI version(3.0.0) we use this API to establish communication between the server and our client.

### Using the API
You can use tools like Postman or your browser (only for GET methods).
For information about our endpoints you can look them up [here](https://git.ti.howest.be/TI/2020-2021/s3/project-ii/projects/groep-15/server/-/blob/master/src/main/resources/openapi-group-15.yaml).
