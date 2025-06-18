**Prerequisite:**

1. Install JDK 8
    - sudo apt update
    - sudo apt install openjdk-8-jdk
    - Verify JDK installation: 'java -version'

2. Install Maven
    - Install Maven on Ubuntu. below steps are given
    - sudo apt install maven
    - Verify mvn installation:  'mvn -version'
 
## Getting Started

**Build:**

1. Build DbJavaClient code (JDK 8):

    - cd Db2JavaClient

    - mvn clean install

Command to run this jar is 'java -jar DB2JdbcClient-0.1-jar-with-dependencies.jar `<dbHost`> `<dbPort`> `<dbName`> `<sslTrustStoreLocationJksFilePath`> `<sslTrustStorePassword`> `<dbUser`> `<dbPassword`> `<dbQuery`>'

dbQuery example - SELECT CURRENT SERVER FROM SYSIBM.SYSDUMMY1;
