CREATE TABLE RECIPE(
ID bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
NAME VARCHAR(255) NOT NULL,
BODY VARCHAR(255) NOT NULL,
IMAGES clob NULL,
TAGS varchar(255) NOT NULL);
