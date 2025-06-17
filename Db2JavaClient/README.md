## Requirements

 * Maven
 * JDK
 
## Getting Started

Run mvn clean install

Command to run this jar is 'java -jar DB2JdbcClient-0.1-jar-with-dependencies.jar `<dbHost`> `<dbPort`> `<dbName`> `<sslTrustStoreLocationJksFilePath`> `<sslTrustStorePassword`> `<dbUser`> `<dbPassword`> `<dbQuery`>'

dbQuery example - SELECT CURRENT SERVER FROM SYSIBM.SYSDUMMY1;
