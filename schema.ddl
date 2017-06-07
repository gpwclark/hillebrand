CREATE TABLE messages(
        id MEDIUMINT NOT NULL AUTO_INCREMENT,
        hash INT NOT NULL,
        sender VARCHAR(254) NOT NULL,
        body VARCHAR(1600),
        response VARCHAR(1600),
        validResource BIT,
        validRequest BIT,
        sent BIT,
        processed BIT,
        timestamp BIGINT NOT NULL,
        PRIMARY KEY (id)
);

CREATE TABLE resources(
     id MEDIUMINT NOT NULL AUTO_INCREMENT,
     resource VARCHAR(254) NOT NULL,
     PRIMARY KEY (id)
);
