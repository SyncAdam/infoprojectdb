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
        public ManipMachines mymanipmachines;
        
        private final String username = "m3_asinkovics01";
        private final String password = "0ecd918c";
        
        private final String host = "92.222.25.165";
        private final int port = 3306;
        private final String database = "m3_asinkovics01";
        
        public ManipDB()
        {
            try{
                this.myConnection = establishConnection(host, port, database);
                this.mymanipmachines = new ManipMachines(myConnection);
                System.out.println("Connection estabilshed");
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
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "REF VARCHAR(30) NOT NULL UNIQUE, \n"
                    +   "DES TEXT NOT NULL, \n"
                    +   "PUISSANCE DOUBLE NOT NULL, \n"
                    +   "PRIMARY KEY (ID)"
                    +   ");\n"
                );

                //create operations table
                statement.executeUpdate(
                    "CREATE TABLE OPERATIONS (\n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "IDTYPE INTEGER NOT NULL, \n"
                    +   "IDPRODUIT INTEGER NOT NULL, \n"
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );

                //create precedenceoperation table
                  statement.executeUpdate(
                    "CREATE TABLE PRECEDENCEOPERATION (\n"
                    +   "OPAVANT INTEGER NOT NULL, \n"
                    +   "OPAPRES INTEGER NOT NULL \n"
                    +   ");\n"
                );

                //create realise table
                statement.executeUpdate(
                    "CREATE TABLE REALISE (\n"
                    +   "IDMACHINE INTEGER  NOT NULL, \n"
                    +   "IDTYPE INTEGER NOT NULL, \n"
                    +   "DUREE DOUBLE NOT NULL\n"
                    +   ");\n"
                );

                //create typeoperation table
                statement.executeUpdate(
                    "CREATE TABLE OPERATIONTYPE (\n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "DES TEXT NOT NULL, \n"
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );
       
                //create produit table
                statement.executeUpdate(
                    "CREATE TABLE PRODUCT ( \n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "REF VARCHAR(25) NOT NULL, \n"
                    +   "DES TEXT NOT NULL,"
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );
                
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
                    statement.executeUpdate("DROP TABLE OPERATIONS");
                    statement.executeUpdate("DROP TABLE PRECEDENCEOPERATION");
                    statement.executeUpdate("DROP TABLE REALISE");
                    statement.executeUpdate("DROP TABLE OPERATIONTYPE");
                    statement.executeUpdate("DROP TABLE PRODUCT");

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