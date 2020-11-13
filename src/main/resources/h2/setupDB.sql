DROP ALL OBJECTS;

create table users
(
	userID int auto_increment,
	homeEndpointID int,
	name varchar(50),
	password varchar(30),
	homeAddress varchar(125),
	sharesLocation boolean,
	subscriptionID int,
	businessAccount boolean,
	CONSTRAINT userName_pk Primary Key (name)
);

create table businesses
(
	businessID int auto_increment,
	name varchar(50),
	subscriptionID int,
	CONSTRAINT businessID_pk Primary Key (businessID)
);

create table subscriptions
(
	subscriptionID int auto_increment,
	name varchar(50),
	CONSTRAINT subscriptionID_pk PRIMARY KEY (subscriptionID)
);

create table users_subscriptions
(
	userID int not null,
	subscriptionID int not null,
	CONSTRAINT userID_fk FOREIGN KEY (userID) REFERENCES users(userID),
	CONSTRAINT u_subscriptionID_fk FOREIGN KEY (subscriptionID) REFERENCES subscriptions(subscriptionID)
);

create table businesses_subscriptions
(
	businessID int not null,
	subscriptionID int not null,
	CONSTRAINT businessID_fk FOREIGN KEY (businessID) REFERENCES businesses(businessID),
	CONSTRAINT b_subscriptionID_fk FOREIGN KEY (subscriptionID) REFERENCES subscriptions(subscriptionID)
);

create table trips
(
	tripID int auto_increment,
	`from` varchar(125),
	destination varchar(125),
	`when` date,
	podType varchar(20),
	CONSTRAINT tripID_pk PRIMARY KEY (tripID)
);

create table trips_users
(
	tripID int not null,
	userID int not null,
	CONSTRAINT tripID_fk FOREIGN KEY (tripID) REFERENCES trips(tripID),
	CONSTRAINT t_userID_fk FOREIGN KEY (userID) REFERENCES users(userID)
);

create table deliveries
(
	deliveryID int auto_increment,
	deliveryType varchar(25),
	`when` date
);

create table deliveries_businesses
(
	deliveryID int not null,
	businessID int not null,
	CONSTRAINT d_businessID_fk FOREIGN KEY (businessID) REFERENCES businesses(businessID),
	CONSTRAINT deliveryID_fk FOREIGN KEY (deliveryID) REFERENCES deliveries(deliveryID)
);

create table endpoints
(
	endpointID int auto_increment,
	name varchar(50),
	CONSTRAINT endpointID_pk PRIMARY KEY (endpointID)
);

create table friends
(
	friendName varchar(50) not null,
	userName varchar(50) not null,
	CONSTRAINT f_userName_fk FOREIGN KEY (userName) REFERENCES users(name),
	CONSTRAINT friendName_fk FOREIGN KEY (friendName) REFERENCES users(name)
)

