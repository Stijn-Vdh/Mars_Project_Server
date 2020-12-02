# Martian Travel Tube System - SERVER

###### This is the server of the Martian Travel Tube System

## Table of Contents
- [Introduction](#introduction)
- [Getting Started](#Getting Started)
- [Usage](#Usage)
- [Features](#features)
- [API](#API)
- [Description](#Description)

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

## Description

![Diagram](https://svgshare.com/i/S00.svg) 

### WebServer
When the WebServer starts it first creates the H2 database, after that it loads a yaml file located in the resources folder. 
The yaml file contains an OpenAPI3 specification of our REST API. Based on that file it adds all the routes to the router.
Next it adds the Logger, the cors handler, security handlers, SockJSHandler and as last the error handlers.


The cors handler allows Cross-Origin Resource Sharing (CORS) on each incoming request.
We add three security handlers: for users only, for businesses only and for businesses or users. Based on the bearer token giving by each request it validates the request.
We also add a SockJSHandler which handles all requests incoming on the eventbus on the `events.` address. 
The SockJSHandler allows the clients to listen to specific channels on the eventbus on which they will receive their notifications.
The error handlers adds the error handlers for all the non 2XX status code responses listed in the specification.
It sets the right status code, message and the cause of the error.


For errors thrown by the request which aren't handled/caught by previous handlers it will return an internal server error. 
Which shouldn't be happening but indicates a failed handling of the incoming request by the server.
The WebServer also links the correct bridge functions to the REST API paths.
The WebServer also starts a daily timer which resets the amount of used pods by the businesses.
### Bridge



### Auth Controller

### MTTS Controller

### Repositories

#### AccountsRepository

#### FriendsRepository

#### TravelsRepository

#### EndpointsRepository

#### DeliveriesRepository

#### FavoritesRepository

#### SubscriptionsRepository

#### ReportsRepository

### H2 Database
The `MarsConnection` util class creates the H2 Database. It uses the [Singleton](https://en.wikipedia.org/wiki/Singleton_pattern) pattern so that only one instance of the class can exist.
It has a static `configure` method which creates the actual H2 database using the parameters and stores those inside the `MarsConnection` class. 
Further it has a static `getConnection` method which returns the current connection to the H2 Database.
`configure` also calls a private method which reads a sql file from resources/h2 folder which initialize all the tables.
The sql file contains all the sql to set up all the tables we use.
It is in fifth normal form, and it has all the constraints to keep the database consistent (ACID).
Then it reads the sql files which populate our tables. Those sql files are also located in resources/h2 folder.