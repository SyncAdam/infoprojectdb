package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Product extends ProductType{

    private String serial;
    ArrayList<Operation> operationsDone;
    ArrayList<Operation> operationsNotDone;

    Product(String serial, ProductType p)
    {
        super(p.getReference(), p.getDescription(), p.getPrice(), p.getOperations(), p.pathToImage);
        this.serial = serial;

        this.operationsNotDone = p.getOperations();
        this.operationsDone = new ArrayList<>();
    }

    public static boolean opbeforeCompleted(int operationEpoch, String serial, Connection myConnection) throws SQLException
    {

        if(operationEpoch == 1) return true;

        ResultSet r;
        try(PreparedStatement pStatement = myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE WHERE PRODUCTQUEUE.SERIAL = ?"))
        {
            pStatement.setString(1, serial);
            r = pStatement.executeQuery();

            do
            {
                r.next();

                try(PreparedStatement pStatement2 = myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.ID = ?"))
                {
                    pStatement2.setInt(1, r.getInt("IDOPERATION"));
                    ResultSet rset = pStatement2.executeQuery();
                    rset.next();
                    if(rset.getInt("NEPOCH") < operationEpoch && !r.getBoolean("OPERATIONEFFECTED")) return false;
                }
            }
            while(!r.isLast());

        }
        catch(SQLException err)
        {
            throw(err);
        }
        return true;
    }

    public String getSerial()
    {
        return this.serial;
    }
}
