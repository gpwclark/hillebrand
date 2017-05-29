CREATE TABLE messages(
        id MEDIUMINT NOT NULL AUTO_INCREMENT,
        hash INT NOT NULL,
        sender VARCHAR(254) NOT NULL,
        body VARCHAR(1600) NOT NULL,
        response VARCHAR(1600),
        validResource BIT,
        validResponse BIT,
        sent BIT,
        processed BIT,
        timestamp BIGINT NOT NULL,
        PRIMARY KEY (id)
);

INSERT INTO messages
    (`hash`, `sender`, `body`, `response`, `validResource`, `validResponse`, `sent`, `processed`, `timestamp`)
VALUES
    (987989, 'customer5@localhost.com', 'laksjdlsjSTATUS', NULL, 0, 0, 0, 0, 1496013221194),
    (91208202, 'customer139@localhost.com', 'lkajsdlajsdl jfasldkfj ldakj ldskjflakjds lajdflaskjd flsdk jflk sl', NULL, 0, 0, 0, 0, 1496013221198),
    (1815692832, 'customer20@localhost.com', 'status', NULL, 0, 0, 0, 0, 1496013221100),
    (-1283141179, 'badhomebre@localhost.com', 'status', NULL, 0, 0, 0, 0, 1496013221000)
    ;
