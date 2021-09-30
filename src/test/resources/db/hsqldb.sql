CREATE TABLE events (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL,
	name VARCHAR(50) NOT NULL,
	description VARCHAR(8000) NOT NULL,
	location VARCHAR(100) NOT NULL,
	day TIMESTAMP NOT NULL,
	tag VARCHAR(100) NOT NULL,
	capacity INT NOT NULL,
	important BOOLEAN NOT NULL,
	image BLOB,
	organizer INT NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE users (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1) NOT NULL,
	login VARCHAR(100) NOT NULL,
	password VARCHAR(64) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE eventuser(
  userid INT NOT NULL,
  eventid INT NOT NULL,
  FOREIGN KEY (userid) REFERENCES users(id),
  FOREIGN KEY (eventid) REFERENCES events(id) ON DELETE CASCADE
);