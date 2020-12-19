DROP ALL OBJECTS;

CREATE TABLE endpoints
(
	id        int AUTO_INCREMENT PRIMARY KEY,
	name      varchar(50),
	available boolean DEFAULT TRUE,
	private   boolean DEFAULT FALSE,
	latitude  double,
	longitude double
);

CREATE TABLE user_subscriptions
(
	id                int AUTO_INCREMENT (0) PRIMARY KEY,
	name              varchar(50),
	unlimitedTravels  boolean DEFAULT FALSE,
	unlimitedPackages boolean DEFAULT FALSE,
	price             DECIMAL(2, 1)
);

INSERT INTO user_subscriptions VALUES (default, 'No subscription', FALSE, FALSE, 0);
INSERT INTO user_subscriptions VALUES (default, 'Martian Transport', TRUE, FALSE, 40);
INSERT INTO user_subscriptions VALUES (default, 'Package Transport', FALSE, TRUE, 15);
INSERT INTO user_subscriptions VALUES (default, 'Combined Transport', TRUE, TRUE, 50);


CREATE TABLE business_subscriptions
(
	id             int AUTO_INCREMENT (0) PRIMARY KEY,
	name           varchar(50),
	smallPodsDaily int,
	largePodsDaily int,
	dedicatedPods  int DEFAULT 0,
	priorityLevel  int DEFAULT 0,
	price          int
);

INSERT INTO business_subscriptions VALUES (DEFAULT, 'No business subscription', 0, 0, 0, 0, 0);
INSERT INTO business_subscriptions VALUES (DEFAULT, 'Start-up Business', 300, 3, 0, 0, 5160);
INSERT INTO business_subscriptions VALUES (DEFAULT, 'Professional Business', 500, 10, 2, 1, 8700);
INSERT INTO business_subscriptions VALUES (DEFAULT, 'Enterprise Business', 2500, 50, 5, 2, 34470);
INSERT INTO business_subscriptions VALUES (DEFAULT, 'Custom business', -1, -1, -1, -1, -1);


CREATE TABLE accounts
(
	name           varchar(50) PRIMARY KEY,
	password       varchar(512) NOT NULL,
	homeAddress    varchar(125),
	homeEndpointId int,
	FOREIGN KEY (homeEndpointId) REFERENCES endpoints (id)
);


CREATE TABLE users
(
	name           varchar(50) PRIMARY KEY,
	displayName    varchar(100) NOT NULL,
	sharesLocation boolean DEFAULT FALSE,
	subscriptionId int     DEFAULT 0, -- different subscription type compared to business!
	FOREIGN KEY (name) REFERENCES accounts (name),
	FOREIGN KEY (subscriptionId) REFERENCES user_subscriptions (id)
);

CREATE TABLE businesses
(
	name           varchar(50) PRIMARY KEY,
	subscriptionId int DEFAULT 0,
	largePodsUsed  int DEFAULT 0, --will be reset daily
	smallPodsUsed  int DEFAULT 0, --will be reset daily
	FOREIGN KEY (name) REFERENCES accounts (name),
	FOREIGN KEY (subscriptionId) REFERENCES business_subscriptions (id)
);

CREATE TABLE `report_sections`
(
	`Name` VARCHAR(90) PRIMARY KEY
);

INSERT INTO report_sections VALUES ('Pod ordering / arrival');
INSERT INTO report_sections VALUES ('Menu usage');
INSERT INTO report_sections VALUES ('Account information');
INSERT INTO report_sections VALUES ('Billing');
INSERT INTO report_sections VALUES ('Other');


CREATE TABLE `reports`
(
	`id`            INT AUTO_INCREMENT PRIMARY KEY,
	`accountName`   varchar(50)   NOT NULL,
	`reportSection` VARCHAR(90)   NOT NULL,
	`body`          VARCHAR(2000) NULL,
	FOREIGN KEY (`reportSection`) REFERENCES `report_sections` (`Name`),
	FOREIGN KEY (`accountName`) REFERENCES accounts (name)
);

CREATE TABLE friends
(
	userName   varchar(50),
	friendName varchar(50),
	PRIMARY KEY (userName, friendName),
	FOREIGN KEY (userName) REFERENCES users (name), --notice users here and not accounts
	FOREIGN KEY (friendName) REFERENCES users (name),
	CHECK (userName <> friendName)                  -- prevent befriending yourself
);
CREATE TABLE potential_friends
(
	userName   varchar(50),
	friendName varchar(50),
	PRIMARY KEY (userName, friendName),
	FOREIGN KEY (userName) REFERENCES users (name), --notice users here and not accounts
	FOREIGN KEY (friendName) REFERENCES users (name),
	CHECK (userName <> friendName)                  -- prevent befriending yourself
);

CREATE TABLE favorite_endpoints
(
	accountName varchar(50),
	endpointId  int,
	PRIMARY KEY (accountName, endpointId),
	FOREIGN KEY (accountName) REFERENCES accounts (name),
	FOREIGN KEY (endpointID) REFERENCES endpoints (id)
);

CREATE TABLE deliveryTypes
(
	type varchar(25) PRIMARY KEY
);

INSERT INTO deliveryTypes VALUES ('small');
INSERT INTO deliveryTypes VALUES ('large');

-- sending small/large packages for users and business
CREATE TABLE deliveries -- thinking of keeping a column called travelTime (how long the travel took for ETA ?)
(
	id           int AUTO_INCREMENT PRIMARY KEY,
	deliveryType varchar(25) NOT NULL,
	`from`       int         NOT NULL,
	destination  int         NOT NULL,
	dateTime     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
	sender       varchar(50),                                              -- if not set to null constraints will not allow to set it to null
	FOREIGN KEY (deliveryType) REFERENCES deliveryTypes (type),
	FOREIGN KEY (`from`) REFERENCES endpoints (id),
	FOREIGN KEY (destination) REFERENCES endpoints (id),
	FOREIGN KEY (sender) REFERENCES accounts (name),
	CHECK (`from` <> destination),                                         -- doesnt allow you to send to the location you are sending from
	CHECK (deliveryType = 'small' OR sender IN (SELECT * FROM businesses)) -- doesnt allow users to send big packages
);

CREATE TABLE podTypes
(
	type varchar(20) PRIMARY KEY
);

INSERT INTO podTypes VALUES ('standard');
INSERT INTO podTypes VALUES ('luxury');


CREATE TABLE travels -- can be used as travel history, not sure why we needed a separate trip history table
(
	id          int PRIMARY KEY AUTO_INCREMENT,
	`from`      int,
	destination int,
	userName    varchar(50),
	dateTime    datetime DEFAULT CURRENT_TIMESTAMP(),
	podType     varchar(20),
	ETA         int NULL, -- not sure about this one
	FOREIGN KEY (`from`) REFERENCES endpoints (id),
	FOREIGN KEY (destination) REFERENCES endpoints (id),
	FOREIGN KEY (userName) REFERENCES users (name),
	FOREIGN KEY (podType) REFERENCES podTypes (type),
	CHECK (`from` <> destination)
);

-- (∩｀-´)⊃━☆ﾟ.*･｡ﾟ*\~=+_changelog_+=-/*
-- one table for things users and businesses can do/have (accounts)
-- separated subscriptions into users and businesses (business_subscriptions, user_subscriptions) (these contain the subscription conditions)
-- the users business tables holds which subscription they have (cant mix)
-- merged deliveries and deliveries_business (why was it separate ?, we only kept who send a delivery if it was a business)
-- add travels table (will contain all normal travels by users)
-- is in 5nf (fifth normal form)
-- all constraints to fully adhere to C (consistency) in ACID
-- weird table order due to constraints (table has to exist before you can add FKs to it)