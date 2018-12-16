CREATE TABLE IF NOT EXISTS ACCOUNTS (
   ID       INTEGER     AUTO_INCREMENT      NOT NULL    PRIMARY KEY,
   BALANCE  DOUBLE
);

INSERT INTO ACCOUNTS
    SELECT 1 AS ID, 10000 AS BALANCE
    WHERE NOT EXISTS (SELECT 1 FROM ACCOUNTS WHERE ID = 1);
INSERT INTO ACCOUNTS
    SELECT 2 AS ID, 10000 AS BALANCE
    WHERE NOT EXISTS (SELECT 1 FROM ACCOUNTS WHERE ID = 2);
