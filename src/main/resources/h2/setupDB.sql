DROP ALL OBJECTS;

create table users
(
    homeEndpointID int,
    name           varchar(50),
    password       varchar(512),
    homeAddress    varchar(125),
    sharesLocation boolean,
    subscriptionID int,
    CONSTRAINT userName_pk Primary Key (name)
);

create table businesses
(
    homeEndpointID int,
    name           varchar(50),
    password       varchar(512),
    homeAddress    varchar(125),
    subscriptionID int,
    CONSTRAINT businessID_pk Primary Key (name)
);

create table subscriptions
(
    subscriptionID             int auto_increment,
    name                       varchar(50),
    remainingSmallPods_thisDay int,
    remainingLargePods_thisDay int,
    amountOfDedicatedPods      int,
    CONSTRAINT subscriptionID_pk PRIMARY KEY (subscriptionID)
);

create table users_subscriptions
(
    userName       varchar(50) not null,
    subscriptionID int         not null,
    CONSTRAINT userID_fk FOREIGN KEY (userName) REFERENCES users (name),
    CONSTRAINT u_subscriptionID_fk FOREIGN KEY (subscriptionID) REFERENCES subscriptions (subscriptionID)
);

create table businesses_subscriptions
(
    businessName               varchar(50) not null,
    subscriptionID             int         not null,
    remainingSmallPods_thisDay int,
    remainingLargePods_thisDay int,
    amountOfDedicatedPods      int,
    CONSTRAINT businessName_fk FOREIGN KEY (businessName) REFERENCES businesses (name),
    CONSTRAINT b_subscriptionID_fk FOREIGN KEY (subscriptionID) REFERENCES subscriptions (subscriptionID)
);

create table trips
(
    tripID      int auto_increment,
    `from`      varchar(125),
    destination varchar(125),
    `dateTime`      varchar(50),
    podType     varchar(20),
    CONSTRAINT tripID_pk PRIMARY KEY (tripID)
);

create table trips_users
(
    tripID int not null,
    userName varchar(50) not null,
    CONSTRAINT tripID_fk FOREIGN KEY (tripID) REFERENCES trips (tripID),
    CONSTRAINT t_userName_fk FOREIGN KEY (username) REFERENCES users (name)
);

create table deliveries
(
    deliveryID   int auto_increment,
    deliveryType varchar(25),
    `from`       int,
    destination  int,
    `date`       date
);

create table deliveries_businesses
(
    deliveryID int not null,
    businessID int not null,
    CONSTRAINT d_businessID_fk FOREIGN KEY (businessID) REFERENCES businesses (name),
    CONSTRAINT deliveryID_fk FOREIGN KEY (deliveryID) REFERENCES deliveries (deliveryID)
);

create table endpoints
(

    id   int auto_increment,
    name varchar(50),
    CONSTRAINT endpointID_pk PRIMARY KEY (id)
);

create table friends
(
    friendName varchar(50) not null,
    userName   varchar(50) not null,
    CONSTRAINT f_userName_fk FOREIGN KEY (userName) REFERENCES users (name),
    CONSTRAINT friendName_fk FOREIGN KEY (friendName) REFERENCES users (name)
);

create table favorite_trips_users
(
    userName varchar(50) not null,
    endpointID int not null,
    constraint f_userNameF_fk FOREIGN KEY (userName) REFERENCES users (name),
    constraint fu_endpointID_fk FOREIGN KEY (endpointID) REFERENCES endpoints (id)
);

create table favorite_trips_businesses
(
    businessName varchar(50) not null,
    endpointID int not null,
    constraint f_businessName_fk FOREIGN KEY (businessName) REFERENCES businesses (name),
    constraint fb_endpointID_fk FOREIGN KEY (endpointID) REFERENCES endpoints (id)
);

CREATE TABLE `report_sections`
(
    `Name` VARCHAR(90) NOT NULL,
    PRIMARY KEY (`Name`)
);

CREATE TABLE `reports`
(
    `id`            INT           NOT NULL AUTO_INCREMENT,
    `reportSection` VARCHAR(90)   NOT NULL,
    `body`          VARCHAR(2000) NULL,
    `accountId`     varchar(50)   NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_report_section` FOREIGN KEY (`reportSection`) REFERENCES `report_sections` (`Name`)
);