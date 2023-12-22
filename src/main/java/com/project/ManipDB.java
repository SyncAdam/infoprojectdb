package com.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ManipDB {
    
        public Connection myConnection;
        public ManipMachines mymanipmachines;
        public ManipOperationTypes mymanipoperationtypes;
        public ManipProducts mymanipproducts;
        
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
                this.mymanipoperationtypes = new ManipOperationTypes(myConnection);
                this.mymanipproducts = new ManipProducts(myConnection);
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
                    +   "DES TEXT NOT NULL, \n"
                    +   "POWER DOUBLE NOT NULL, \n"
                    +   "PRIMARY KEY (ID)"
                    +   ");\n"
                );

                //create operations table
                statement.executeUpdate(
                    "CREATE TABLE OPERATIONS (\n"
                    +   "ID INTEGER NOT NULL AUTO_INCREMENT, \n"
                    +   "IDTYPE INTEGER NOT NULL, \n"
                    +   "IDPRODUCT INTEGER NOT NULL, \n"
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );

                //create precedenceoperation table
                  statement.executeUpdate(
                    "CREATE TABLE PRECEDENCEOPERATION (\n"
                    +   "OPBEF INTEGER NOT NULL, \n"        //operation before
                    +   "OPAFT INTEGER NOT NULL \n"         //opeartion after
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
                    +   "REF VARCHAR(25) NOT NULL UNIQUE, \n"
                    +   "DES TEXT NOT NULL,"
                    +   "PRIMARY KEY(ID)"
                    +   ");\n"
                );
                
                this.myConnection.commit();

                addOperationsConstraints();
                addRealiseConstraints();
                addPrecedenceOperationConstraints();
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

        public void addOperationsConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                statement.executeUpdate(
                    "ALTER TABLE OPERATIONS ADD CONSTRAINT FK_OPERATIONS_IDTYPE FOREIGN KEY (IDTYPE) REFERENCES OPERATIONTYPE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );

                statement.executeUpdate(
                    "ALTER TABLE OPERATIONS ADD CONSTRAINT FK_OPERATIONS_IDPRODUCT FOREIGN KEY (IDPRODUCT) REFERENCES PRODUCT(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
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
                    "ALTER TABLE REALISE ADD CONSTRAINT FK_REALISE_IDMACHINE FOREIGN KEY (IDMACHINE) REFERENCES MACHINE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );
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

        public void addPrecedenceOperationConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                statement.executeUpdate(
                    "ALTER TABLE PRECEDENCEOPERATION ADD CONSTRAINT FK_PRECEDENCEOPERATION_OPBEF FOREIGN KEY (OPBEF) REFERENCES OPERATIONTYPE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
                );
                statement.executeUpdate(
                    "ALTER TABLE PRECEDENCEOPERATION ADD CONSTRAINT FK_PRECEDENCEOPERATION_OPAFT FOREIGN KEY (OPAFT) REFERENCES OPERATIONTYPE(ID) ON DELETE RESTRICT ON UPDATE RESTRICT;"
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

        public void deleteOperationsContraints() throws SQLException
        {

            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    statement.executeUpdate("ALTER TABLE OPERATIONS DROP CONSTRAINT FK_OPERATIONS_IDTYPE;");
                    statement.executeUpdate("ALTER TABLE OPERATIONS DROP CONSTRAINT FK_OPERATIONS_IDPRODUCT;");
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

        public void deletePrecedenceOperationConstraints() throws SQLException
        {
            this.myConnection.setAutoCommit(false);

            try(Statement statement = this.myConnection.createStatement())
            {
                //first delete constraints with other tables, than delete tables
                try
                {
                    statement.executeUpdate("ALTER TABLE PRECEDENCEOPERATION DROP CONSTRAINT FK_PRECEDENCEOPERATION_OPBEF;");
                    statement.executeUpdate("ALTER TABLE PRECEDENCEOPERATION DROP CONSTRAINT FK_PRECEDENCEOPERATION_OPAFT;");
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
                    statement.executeUpdate("ALTER TABLE REALISE DROP CONSTRAINT FK_REALISE_IDMACHINE;");
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
                    deletePrecedenceOperationConstraints();

                    this.myConnection.setAutoCommit(false);

                    statement.executeUpdate("DROP TABLE OPERATIONS");
                    statement.executeUpdate("DROP TABLE REALISE");
                    statement.executeUpdate("DROP TABLE MACHINE");
                    statement.executeUpdate("DROP TABLE PRECEDENCEOPERATION");
                    statement.executeUpdate("DROP TABLE OPERATIONTYPE");
                    statement.executeUpdate("DROP TABLE PRODUCT");

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