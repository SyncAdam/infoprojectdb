package com.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ManipDB {
    
        public Connection myConnection;
        public ManipMachines myManipMachines;
        public ManipOperationTypes myManipOperationTypes;
        public ManipProducts myManipProducts;
        public ManipOperations myManipOperations;
        
        private final String username = "m3_asinkovics01";
        private final String password = "0ecd918c";
        
        private final String host = "92.222.25.165";
        private final int port = 3306;
        private final String database = "m3_asinkovics01";
        
        public ManipDB()
        {
            try{
                this.myConnection = establishConnection(host, port, database);
                this.myManipMachines = new ManipMachines(myConnection);
                this.myManipOperationTypes = new ManipOperationTypes(myConnection);
                this.myManipProducts = new ManipProducts(myConnection);
                this.myManipOperations = new ManipOperations(myConnection);
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

            try(Statement statement = this.myConnection.createStatement())
            {
                //create machine table

                statement.executeUpdate(
                    "CREATE TABLE MACHINE (\n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "REF VARCHAR(30) NOT NULL UNIQUE, \n"
                    +   "MODEL VARCHAR(30) NOT NULL, "
                    +   "STATE INTEGER NOT NULL, \n"
                    +   "PRIMARY KEY (ID)"
                    +   ");\n"
                );

                //create operations table
                statement.executeUpdate(
                    "CREATE TABLE OPERATIONS (\n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "IDTYPE INTEGER NOT NULL, \n"
                    +   "REFPRODUCT VARCHAR(25) NOT NULL, \n"
                    +   "NEPOCH INT NOT NULL, "
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );

                //create realise table
                statement.executeUpdate(
                    "CREATE TABLE REALISE (\n"
                    +   "MTYPE VARCHAR(30)  NOT NULL, \n"
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
                    +   "REF VARCHAR(25) NOT NULL UNIQUE, \n"
                    +   "DES TEXT NOT NULL, "
                    +   "NEPOCH INT NOT NULL, "
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );

                statement.executeUpdate(
                    "CREATE TABLE MACHINEWORKING ( "
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, "
                    +   "MACHINEREF VARCHAR(30) NOT NULL, "
                    +   "SERIAL VARCHAR(30), "
                    +   "IDOPERATION INT, "
                    +   "TIME TIMESTAMP, "
                    +   "PRIMARY KEY(ID));"
                );

                statement.executeUpdate(
                    "CREATE TABLE PRODUCTQUEUE ("
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, "
                    +   "REF VARCHAR(25) NOT NULL, "
                    +   "SERIAL VARCHAR(25) NOT NULL, "
                    +   "IDOPERATION INT NOT NULL, "      // operation not yet effected
                    +   "OPERATIONEFFECTED BOOLEAN NOT NULL, "
                    +   "TIME TIMESTAMP, "
                    +   "PRIMARY KEY(ID));"
                );

                statement.executeUpdate(
                    "CREATE TABLE STOCK ("
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, "
                    +   "REF VARCHAR(25) NOT NULL, "
                    +   "SERIAL VARCHAR(25) NOT NULL, "
                    +   "PRIMARY KEY(ID));"
                );
                
                this.myConnection.commit();

                addOperationsConstraints();
                addRealiseConstraints();
                addMachineWorkingConstraints();
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

        public void addMachineWorkingConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                statement.executeUpdate("ALTER TABLE MACHINEWORKING ADD CONSTRAINT FK_MACHINEWORKING_MACHINEREF FOREIGN KEY (MACHINEREF) REFERENCES MACHINE(REF) ON DELETE RESTRICT ON UPDATE RESTRICT");
                statement.executeUpdate("ALTER TABLE MACHINEWORKING ADD CONSTRAINT FK_MACHINEWORKING_IDOPERATION FOREIGN KEY (IDOPERATION) REFERENCES OPERATIONS(ID) ON DELETE RESTRICT ON UPDATE RESTRICT");
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                this.myConnection.rollback();
                throw(e);
            }

            this.myConnection.setAutoCommit(true);
            
        }

        public void addOperationsConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                statement.executeUpdate(
                    "ALTER TABLE OPERATIONS ADD CONSTRAINT FK_OPERATIONS_IDTYPE FOREIGN KEY (IDTYPE) REFERENCES OPERATIONTYPE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );

                statement.executeUpdate(
                    "ALTER TABLE OPERATIONS ADD CONSTRAINT FK_OPERATIONS_REFPRODUCT FOREIGN KEY (REFPRODUCT) REFERENCES PRODUCT(REF) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );

                this.myConnection.commit();
            }
            catch(SQLException e)
            {
                this.myConnection.rollback();
                e.printStackTrace();
                throw(e);
            }

            this.myConnection.setAutoCommit(true);
        }

        public void addRealiseConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                statement.executeUpdate(
                    "ALTER TABLE REALISE ADD CONSTRAINT FK_REALISE_IDTYPE FOREIGN KEY (IDTYPE) REFERENCES OPERATIONTYPE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );

                this.myConnection.commit();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                this.myConnection.rollback();
                throw(e);
            }

            this.myConnection.setAutoCommit(true);
        }

        public void deleteMachineWorkingConstraints() throws SQLException
        {

            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    statement.executeUpdate("ALTER TABLE MACHINEWORKING DROP CONSTRAINT FK_MACHINEWORKING_MACHINEREF;");
                    statement.executeUpdate("ALTER TABLE MACHINEWORKING DROP CONSTRAINT FK_MACHINEWORKING_IDOPERATION;");
                    
                    this.myConnection.commit();
                }
                catch(SQLException e)
                {
                    this.myConnection.rollback();
                    e.printStackTrace();
                    throw(e);
                }

            }

            this.myConnection.setAutoCommit(true);

        }

        public void deleteOperationsContraints() throws SQLException
        {

            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    statement.executeUpdate("ALTER TABLE OPERATIONS DROP CONSTRAINT FK_OPERATIONS_IDTYPE;");
                    statement.executeUpdate("ALTER TABLE OPERATIONS DROP CONSTRAINT FK_OPERATIONS_REFPRODUCT;");
                    
                    this.myConnection.commit();
                }
                catch(SQLException e)
                {
                    this.myConnection.rollback();
                    e.printStackTrace();
                    throw(e);
                }

            }

            this.myConnection.setAutoCommit(true);
        }

        public void deleteRealiseConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    statement.executeUpdate("ALTER TABLE REALISE DROP CONSTRAINT FK_REALISE_IDTYPE;");
                    this.myConnection.commit();
                }
                catch(SQLException e)
                {
                    this.myConnection.rollback();
                    e.printStackTrace();
                    throw(e);
                }

            }

            this.myConnection.setAutoCommit(true);
        }

        public void deleteSchema() throws SQLException
        {

            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    //drop constraints
                    //statement.executeUpdate("ALTER TABLE MACHINE");

                    //drop tables
                    deleteOperationsContraints();
                    deleteRealiseConstraints();
                    deleteMachineWorkingConstraints();

                    this.myConnection.setAutoCommit(false);

                    statement.executeUpdate("DROP TABLE OPERATIONS");
                    statement.executeUpdate("DROP TABLE REALISE");
                    statement.executeUpdate("DROP TABLE MACHINE");
                    statement.executeUpdate("DROP TABLE OPERATIONTYPE");
                    statement.executeUpdate("DROP TABLE PRODUCT");
                    statement.executeUpdate("DROP TABLE MACHINEWORKING");
                    statement.executeUpdate("DROP TABLE PRODUCTQUEUE"); 
                    statement.executeUpdate("DROP TABLE STOCK");        

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

            }
            this.myConnection.setAutoCommit(true);
        }
        
}