DROP DATABASE IF EXISTS `letta`;
CREATE DATABASE `letta`;


CREATE TABLE `letta`.`users`(
  `id` int(11) NOT NULL,
  `login` varchar(100) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE `letta`.`events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(2000) NOT NULL,
  `location` varchar(255) NOT NULL,
  `day` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `tag` varchar(100) NOT NULL,
  `capacity` int(11) NOT NULL,
  `important` tinyint(1) NOT NULL,
  `image` blob DEFAULT NULL,
  `organizer` int(11) NOT NULL,
  FOREIGN KEY (`organizer`) REFERENCES `users`(`id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `letta`.`eventuser`(
  `userid` int NOT NULL,
  `eventid` int NOT NULL,
  FOREIGN KEY (`userid`) REFERENCES `users`(id),
  FOREIGN KEY (`eventid`) REFERENCES `events`(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE USER IF NOT EXISTS 'daa'@'localhost' IDENTIFIED WITH mysql_native_password BY 'daa';
GRANT ALL ON `letta`.* TO 'daa'@'localhost';
