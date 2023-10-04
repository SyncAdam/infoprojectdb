package com.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ManipDB {
    
        private Connection myConnection;
        
        private final String username = "m3_asinkovics01";
        private final String password = "0ecd918c";
        
        private final String host = "92.222.25.165";
        private final int port = 3306;
        private final String database = "m3_asinkovics01";
        
        public ManipDB()
        {
            try{
                this.myConnection = establishConnection(host, port, database);
            }
            catch(SQLException e)
            {
                System.out.println("ERROR : \n");
                e.printStackTrace();
            }
        }
        
        private Connection establishConnection(String host, int port, String database) throws SQLException
        {
             Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port
                + "/" + database,
                this.username, this.password);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            return con;
        }
        
        public void createSchema() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement()){
                //create machine table
                statement.executeUpdate(
                    "CREATE TABLE MACHINE (\n"
                    +   "ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, \n"
                    +   "REF VARCAR(30) NOT NULL UNIQUE, \n"
                    +   "DES TEXT NOT NULL, \n"
                    +   "PUISSANCE DOUBLE NOT NULL, \n"
                    +   ")\n"
                );
                //create operations table
                //...
                //create precedenceoperation table
                //...
                //create produit table
                //...
                //create realise table
                //...
                //create typeoperation table
                //...

                
                this.myConnection.commit();
            }
            catch(SQLException e)
            {
                System.out.println("ERROR : \n");
                e.printStackTrace();
                System.out.println("ROLLING BACK CHANGES TO THE SQL SERVER");
                this.myConnection.rollback();
                throw(e);
            }

            this.myConnection.setAutoCommit(true);
        }

        public void deleteSchema() throws SQLException
        {
            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    //drop constraints
                    //statement.executeUpdate("ALTER TABLE MACHINE");

                    //drop tables
                    statement.executeUpdate("DROP TABLE MACHINE");
                }
                catch(SQLException e)
                {
                    System.out.println("ERROR : \n");
                    e.printStackTrace();
                    System.out.println("ROLLING BACK CHANGES TO THE SQL SERVER");
                    this.myConnection.rollback();
                    throw(e);
                }

            }
        }   
}