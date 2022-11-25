SHOW DATABASES;
CREATE DATABASE IF NOT EXISTS Thogakade;
USE Thogakade;
//#===============================
CREATE TABLE Customer(
    id VARCHAR(45) NOT NULL ,
    name VARCHAR(50) NOT NULL ,
    address TEXT,
    salary DOUBLE,
    CONSTRAINT PRIMARY KEY (id)

);

DESC Customer;

USE Thogakade;
SHOW TABLES ;
#=============================
CREATE TABLE IF NOT EXISTS Item(
                         code VARCHAR(45),
                         description VARCHAR(50),
                         unitPrice DOUBLE,
                         qtyOnHand INT,
                         CONSTRAINT PRIMARY KEY (code)

);

SHOW TABLES ;
DESC Item;

#==================================
CREATE TABLE IF NOT EXISTS `Order`(
    orderId VARCHAR(45),
    date VARCHAR(250),
    totalCost DOUBLE,
    customer VARCHAR(45),
    CONSTRAINT PRIMARY KEY (orderId),
    CONSTRAINT FOREIGN KEY (customer) REFERENCES Customer(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);

DESC `Order`;
