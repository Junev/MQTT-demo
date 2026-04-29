CREATE DATABASE DEMO;
USE DEMO;
GO 
CREATE TABLE DEMO.dbo.mqtt_message (
	id int IDENTITY(1,1) NOT NULL,
	message nvarchar(500) COLLATE Chinese_PRC_CI_AS NULL,
	save_time datetime2 NOT NULL,
	isdeleted bit DEFAULT 0 NULL,
	CONSTRAINT PK_mqtt_message PRIMARY KEY (id)
);