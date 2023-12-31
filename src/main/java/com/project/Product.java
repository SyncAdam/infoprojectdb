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
    private boolean workedOn;

    Product(String serial, ProductType p)
    {
        super(p.getReference(), p.getDescription(), p.getPrice(), p.getOperations());
        this.serial = serial;

        this.operationsNotDone = p.getOperations();
        this.operationsDone = new ArrayList<>();
        this.workedOn = false;
    }

    public static boolean opbeforeCompleted(Operation selectedOperation, Product p, Connection myConnection) throws SQLException
    {
        ResultSet r;
        boolean opsBeforeDone = false;
        ArrayList<Operation> opsBefore = new ArrayList<>(); 
        try(PreparedStatement pStatement = myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.REFPRODUCT = ?"))
        {
            pStatement.setString(1, p.getReference());
            r = pStatement.executeQuery();

            opsBeforeDone = true;
            do
            {
                r.next();
                opsBefore.add(new Operation(r.getInt("ID"), r.getInt("IDTYPE"), r.getString("REF"), r.getInt("OPBEF"), r.getInt("OPAFT")));
            }
            while(!r.isLast());

            ///--------------------------------------------------------------------------------------

        }
        catch(SQLException err)
        {
            throw(err);
        }
        return opsBeforeDone;
    }

    public boolean isWorkedOn()
    {
        return this.workedOn;
    }
}
