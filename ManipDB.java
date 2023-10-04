package com.mycompany.beans;

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
        
        public ManipMachine()
        {
            try{
                this.myConnection = establishConnection(host, port, database);
            }
            catch()
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
        
        
    
}
