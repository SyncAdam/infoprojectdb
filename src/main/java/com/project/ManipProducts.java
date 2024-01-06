package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;


public class ManipProducts {

    private Connection myConnection;

    ManipProducts(Connection connection)
    {  
        this.myConnection = connection;
    }

    public void addProduct(String ref, String des, ArrayList<Operation> operations, int nepochs) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(Statement s = this.myConnection.createStatement()){

            ResultSet res = s.executeQuery("SELECT COUNT(*) FROM OPERATIONS");
            res.next();

            try(PreparedStatement pStatement = this.myConnection.prepareStatement(
                "INSERT INTO PRODUCT "
                + "(REF, DES, NEPOCH) "
                + "VALUES(?, ?, ?);"
            ))
            {
                pStatement.setString(1, ref);
                pStatement.setString(2, des);
                pStatement.setInt(3, nepochs);
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
                    +   "(IDTYPE, REFPRODUCT, NEPOCH) "
                    +    "VALUES(?, ?, ?);"
                ))
                {
                    pStatement.setInt(1, operations.get(i).idtype);
                    pStatement.setString(2, operations.get(i).refproduct);
                    pStatement.setInt(3, operations.get(i).nepoch);
                    pStatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                    this.myConnection.rollback();
                }
            }
            this.myConnection.commit();
        }

        this.myConnection.commit();

        this.myConnection.setAutoCommit(true);
    }

    public void loadDefaultProducts() throws SQLException
    {
        ArrayList<Operation> opsBolt = new ArrayList<>();
        opsBolt.add(new Operation(1, 3, "FFFFFE", 1));
        opsBolt.add(new Operation(2, 5, "FFFFFE", 2));
        opsBolt.add(new Operation(3, 1, "FFFFFE", 3));
        addProduct("FFFFFE", "Bolt", opsBolt, 3);
        
        ArrayList<Operation> opsSprocket = new ArrayList<>();
        opsSprocket.add(new Operation(4, 3, "FFFFFD", 1));
        opsSprocket.add(new Operation(5, 13, "FFFFFD", 2));
        addProduct("FFFFFD", "Sprocket", opsSprocket, 2);

        ArrayList<Operation> opsChisel = new ArrayList<>();
        opsChisel.add(new Operation(6, 2, "FFFFFC", 1));
        opsChisel.add(new Operation(7, 14, "FFFFFC", 1));
        opsChisel.add(new Operation(8, 14, "FFFFFC", 2));
        opsChisel.add(new Operation(9, 15, "FFFFFC", 2));
        addProduct("FFFFFC", "Chisel", opsChisel, 2);

        ArrayList<Operation> opsObject = new ArrayList<>();
        opsObject.add(new Operation(10, 4, "FFF210", 1));
        opsObject.add(new Operation(11, 10, "FFF210", 1));
        opsObject.add(new Operation(12, 6, "FFF210", 2));
        addProduct("FFF210", "Object", opsObject, 2);

        ArrayList<Operation> opsObject1 = new ArrayList<>();
        opsObject.add(new Operation(10, 4, "FFF211", 1));
        opsObject.add(new Operation(11, 10, "FFF211", 1));
        opsObject.add(new Operation(12, 6, "FFF211", 2));
        addProduct("FFF211", "Object1", opsObject1, 2);

        ArrayList<Operation> opsObject2 = new ArrayList<>();
        opsObject.add(new Operation(10, 4, "FFF212", 1));
        opsObject.add(new Operation(11, 10, "FFF212", 1));
        opsObject.add(new Operation(12, 6, "FFF212", 2));
        addProduct("FFF212", "Object2", opsObject2, 2);

        ArrayList<Operation> opsObject3 = new ArrayList<>();
        opsObject.add(new Operation(10, 4, "FFF215", 1));
        opsObject.add(new Operation(11, 10, "FFF215", 1));
        opsObject.add(new Operation(12, 6, "FFF215", 2));
        addProduct("FFF215", "Object3", opsObject3, 2);
    }

    public void createProduct(String productReference, String serial) throws SQLException
    {

        this.myConnection.setAutoCommit(false);

        ProductType product;
        ResultSet isThereAProductWithTheSameSerial;

        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE WHERE PRODUCTQUEUE.SERIAL = ?;"))
        {
            pStatement.setString(1, serial);
            isThereAProductWithTheSameSerial = pStatement.executeQuery();
            if(isThereAProductWithTheSameSerial.next()) throw(new SQLException());
        }

        product = getProductType(productReference, this.myConnection);
        
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
                    operationsForProduct.add(new Operation(operationSet.getInt("ID"), operationSet.getInt("IDTYPE"), operationSet.getString("REFPRODUCT"), operationSet.getInt("NEPOCH")));

                }
                while(!operationSet.isLast());
            }

            return new ProductType(reference, productSet.getString("DES"), 0, operationsForProduct);
        }
    }   
    
}