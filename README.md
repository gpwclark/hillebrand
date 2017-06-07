# University of Antarctica Agnostic Messaging Service aka Hillebrand

## Intro
- this program is designed to be extended to be able to send and receive messages.

## mail impl
- to get up and running with the a mail implementation create the necessary
properties files in the properties folder: `imap.properties`, `smtp.properties`,
`mailAuth.properties`, and `db.properties`.
- for the db I recommend using the provided schema with h2
```
java -cp ~/.m2/repository/com/h2database/h2/1.4.195/h2*.jar org.h2.tools.Console
```
as of the time of writing.
- you will need to input the schema.ddl into the sql console and insert any resources
into the db (the actual emails).
- after that execute run.sh
