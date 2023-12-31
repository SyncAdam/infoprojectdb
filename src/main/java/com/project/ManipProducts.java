package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;


public class ManipProducts {

    private Connection myConnection;

    ManipProducts(Connection connection)
    {  
        this.myConnection = connection;
    }

    public void addProduct(String ref, String des, ArrayList<Operation> operations) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(Statement s = this.myConnection.createStatement()){

            ResultSet res = s.executeQuery("SELECT COUNT(*) FROM OPERATIONS");
            res.next();

            int lastID = res.getInt(1);

            try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                "INSERT INTO PRODUCT "
                + "(REF, DES) "
                + "VALUES(?, ?);"
            ))
            {
                pStatement.setString(1, ref);
                pStatement.setString(2, des);
                pStatement.executeUpdate();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                this.myConnection.rollback();
            }
            for(int i = 0; i < operations.size(); i++)
            {
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "INSERT INTO OPERATIONS "
                    +   "(IDTYPE, REFPRODUCT) "
                    +    "VALUES(?, ?);"
                ))
                {
                    pStatement.setInt(1, operations.get(i).idtype);
                    pStatement.setString(2, operations.get(i).refproduct);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
            }
            this.myConnection.commit();
            for(int i = 0; i < operations.size(); i++)
            {
                
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "UPDATE OPERATIONS "
                    +   "SET OPERATIONS.OPBEF = ? WHERE OPERATIONS.ID = ?;"
                ))
                {
                    if(operations.get(i).opbef != 0)
                    {
                        pStatement.setInt(1, operations.get(i).opbef + lastID);
                    }
                    else
                    {
                        pStatement.setNull(1, Types.INTEGER);
                    }

                    pStatement.setInt(2, lastID + i + 1);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
            }
            for(int i = 0; i < operations.size(); i++)
            {
                try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                    "UPDATE OPERATIONS "
                    +   "SET OPERATIONS.OPAFT = ? WHERE OPERATIONS.ID = ?;"
                ))
                {
                    if(operations.get(i).opaft != 0)
                    {
                        pStatement.setInt(1, operations.get(i).opaft + lastID);
                    }
                    else
                    {
                        pStatement.setNull(1, Types.INTEGER);
                    }
                    pStatement.setInt(2, lastID + i + 1);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
        }

        }

        this.myConnection.commit();

        this.myConnection.setAutoCommit(true);
    }

    public void loadDefaultProducts() throws SQLException
    {
        ArrayList<Operation> opsBolt = new ArrayList<>();
        opsBolt.add(new Operation(1, 3, "FFFFFE", 0, 2));
        opsBolt.add(new Operation(2, 5, "FFFFFE", 1, 3));
        opsBolt.add(new Operation(3, 1, "FFFFFE", 2, 0));
        addProduct("FFFFFE", "Bolt", opsBolt);
        
        ArrayList<Operation> opsSprocket = new ArrayList<>();
        opsSprocket.add(new Operation(4, 3, "FFFFFD", 0, 2));
        opsSprocket.add(new Operation(5, 13, "FFFFFD", 1, 0));
        addProduct("FFFFFD", "Sprocket", opsSprocket);
    }

    public void createProduct(String productReference, String serial) throws SQLException
    {

        this.myConnection.setAutoCommit(false);

        ProductType product;
        ResultSet machineSet;
        ResultSet isThereAProductWithTheSameSerial;

        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE WHERE PRODUCTQUEUE.SERIAL = ?;"))
        {
            pStatement.setString(1, serial);
            isThereAProductWithTheSameSerial = pStatement.executeQuery();
            if(isThereAProductWithTheSameSerial.next()) throw(new SQLException());
        }

        product = getProductType(productReference, this.myConnection);
        
        try(PreparedStatement pstatement = this.myConnection.prepareStatement("SELECT * FROM MACHINE"))
        {
            machineSet = pstatement.executeQuery();
        }
        //Insert product into productqueue

        for(int i = 0; i < product.getOperations().size(); i++)
        {
            try(PreparedStatement pStatement = this.myConnection.prepareStatement
            (
                "INSERT INTO PRODUCTQUEUE "
                +   "(REF, SERIAL, IDOPERATION, OPERATIONEFFECTED, TIME) "
                +   "VALUES (?, ?, ?, ?, ?);"
            ))
            {
                pStatement.setString(1, product.getReference());
                pStatement.setString(2, serial);
                pStatement.setInt(3, product.getOperations().get(i).id);
                pStatement.setBoolean(4, false);
                pStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

                pStatement.executeUpdate();
            }
        }

        this.myConnection.commit();

        this.myConnection.setAutoCommit(true);

    }

    public static ProductType getProductType(String reference, Connection myConnection) throws SQLException
    {
        try(PreparedStatement pstatement = myConnection.prepareStatement("SELECT * FROM PRODUCT WHERE PRODUCT.REF = ?;"))
        {
            ResultSet productSet;
            pstatement.setString(1, reference);
            productSet = pstatement.executeQuery();

            productSet.next();
            ResultSet operationSet;
            ArrayList<Operation> operationsForProduct = new ArrayList<>();

            try(PreparedStatement pstatement2 = myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.REFPRODUCT = ?;"))
            {
                pstatement2.setString(1, reference);
                operationSet = pstatement2.executeQuery();

                //nesting meter overload
                do
                {
                    operationSet.next();
                    if(operationSet.getInt("OPBEF") == 0)
                    {
                        operationsForProduct.add(new Operation(operationSet.getInt("ID"), operationSet.getInt("IDTYPE"), operationSet.getString("REFPRODUCT"), 0, operationSet.getInt("OPAFT")));
                    }
                    else if(operationSet.getInt("OPAFT") == 0)
                    {
                        operationsForProduct.add(new Operation(operationSet.getInt("ID"), operationSet.getInt("IDTYPE"), operationSet.getString("REFPRODUCT"), operationSet.getInt("OPBEF"), 0));
                    }
                    else if(operationSet.getInt("OPAFT") == 0 && operationSet.getInt("OPBEF") == 0)
                    {
                        operationsForProduct.add(new Operation(operationSet.getInt("ID"), operationSet.getInt("IDTYPE"), operationSet.getString("REFPRODUCT"), 0, 0));
                    }
                    else
                    {
                        operationsForProduct.add(new Operation(operationSet.getInt("ID"), operationSet.getInt("IDTYPE"), operationSet.getString("REFPRODUCT"), operationSet.getInt("OPBEF"), operationSet.getInt("OPAFT")));
                    }
                }
                while(!operationSet.isLast());
            }

            return new ProductType(reference, productSet.getString("DES"), 0, operationsForProduct);
        }
    }   
    
}