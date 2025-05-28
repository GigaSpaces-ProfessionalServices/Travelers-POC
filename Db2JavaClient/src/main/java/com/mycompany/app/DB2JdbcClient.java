package com.mycompany.app;

import java.sql.*;

public class DB2JdbcClient {

    public static void main(String[] args) {


        if (args.length != 8) {
            System.out.println("Command to run this jar is 'java -jar DB2JdbcClient-0.1-jar-with-dependencies.jar <dbHost> <dbPort> <dbName> <sslTrustStoreLocationJksFilePath> <sslTrustStorePassword> <dbUser> <dbPassword> <dbQuery>'");
            System.out.println("Sample query SELECT CURRENT SERVER FROM SYSIBM.SYSDUMMY1;");
//            System.out.println("To Print all SCHEMANAME pass dbQuery as SELECT SCHEMANAME FROM SYSCAT.SCHEMATA");
//            System.out.println("To Print only user created SCHEMANAME pass dbQuery as SELECT SCHEMANAME FROM SYSCAT.SCHEMATA WHERE SCHEMANAME NOT LIKE 'SYS%' AND SCHEMANAME NOT IN ('SYSCAT', 'SYSTOOLS')");
            System.exit(1);
        }

        String dbHost = args[0];
        String dbPort = args[1];
        String dbName = args[2];
        String sslTrustStoreLocationJksFilePath = args[3];
        String sslTrustStorePassword = args[4];
        String dbUser = args[5];
        String dbPassword = args[6];
        String dbQuery = args[7];


        String url = "jdbc:db2://" + dbHost + ":" + dbPort + "/" + dbName + ":" +
                "sslConnection=true;" +
                "sslTrustStoreLocation=" + sslTrustStoreLocationJksFilePath + ";" +
                "sslTrustStorePassword=" + sslTrustStorePassword + ";";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            System.out.println("\nConnection successful with SSL!\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(dbQuery);
            System.out.println(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
