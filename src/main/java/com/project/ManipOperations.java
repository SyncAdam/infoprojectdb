package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.project.views.MainLayout;

public class ManipOperations {

    private Connection myConnection;

    ManipOperations(Connection connection)
    {  
        this.myConnection = connection;
    }

    //later remove staticity
    public void addOperation(String refproduct, int idtype, int opbef, int opaft) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pStatement = this.myConnection.prepareStatement(
            "INSERT INTO OPERATIONS "
            + "(IDTYPE, REFPRODUCT) "
            + "VALUES(?, ?);"
        ))
        {
            pStatement.setInt(1, idtype);
            pStatement.setString(2, refproduct);
            //pStatement.setInt(3, opbef);
            //pStatement.setInt(4, opaft);
            pStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            this.myConnection.rollback();
        }

        this.myConnection.setAutoCommit(true);
    }

    public void loadDefaultOperations() throws SQLException
    {    
        addOperation("FFFFFE", 3, 0, 2);
        addOperation("FFFFFE", 5, 1, 3);
        addOperation("FFFFFE", 1, 2, 0);
    }

    public static String getOperationTypeByID(int id) throws SQLException
    {
        String result = "";

        int opTypeID = 0;

        try(PreparedStatement pStatement = MainLayout.manipDB.myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.ID = ?"))
        {
            pStatement.setInt(1, id);
            ResultSet res1 = pStatement.executeQuery();
            res1.next();

            opTypeID = res1.getInt("IDTYPE");
        }

        try(PreparedStatement pStatement = MainLayout.manipDB.myConnection.prepareStatement("SELECT * FROM OPERATIONTYPE WHERE ID = ?"))
        {
            pStatement.setInt(1, opTypeID);
            ResultSet res2 = pStatement.executeQuery();
            res2.next();

            result = res2.getString("DES");
        }

        return result;
    }
    
}