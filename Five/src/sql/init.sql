# Host: 127.0.0.1  (Version: 5.7.10-log)
# Date: 2016-11-17 20:00:37
# Generator: MySQL-Front 5.3  (Build 4.271)


#
# Structure for table "doc_info"
#

DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT,
    `sid`         char(4)      NOT NULL DEFAULT '0000',
    `creator`     varchar(255) NOT NULL DEFAULT '',
    `timestamp`   datetime     NOT NULL DEFAULT '2023-01-01 00:00:00',
    `description` text,
    `filename`    varchar(255) NOT NULL DEFAULT '',
    PRIMARY KEY (`Id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8;

#
# Data for table "doc_info"
#

INSERT INTO `doc_info` (sid, creator, timestamp, description, filename)
VALUES ('0001', 'jack', '2016-11-17 00:00:00', 'This is a test, and can\'t download' , 'doc.java');

#
# Structure for table "user_info"
#

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`
(
    `username` varchar(255) NOT NULL DEFAULT '',
    `password` varchar(32)  NOT NULL DEFAULT '',
    `role`     varchar(16)  NOT NULL DEFAULT '',
    PRIMARY KEY (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

#
# Data for table "user_info"
#

INSERT INTO `user_info`
VALUES ('jack', '123', 'operator'),
       ('kate', '123', 'administrator'),
       ('rose', '123', 'browser');


INSERT INTO `user_info`
VALUES ('super','123','administrator');
