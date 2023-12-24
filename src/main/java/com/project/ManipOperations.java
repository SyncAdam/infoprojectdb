package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManipOperations {

    private Connection myConnection;

    ManipOperations(Connection connection)
    {  
        this.myConnection = connection;
    }

    //later remove staticity
    public void addOperation(int idproduct, int idtype, int opbef, int opaft) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pStatement = this.myConnection.prepareStatement(
            "INSERT INTO OPERATIONS "
            + "(IDTYPE, IDPRODUCT) "
            + "VALUES(?, ?);"
        ))
        {
            pStatement.setInt(1, idtype);
            pStatement.setInt(2, idproduct);
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
        addOperation(1, 3, 0, 2);
        addOperation(1, 5, 1, 3);
        addOperation(1, 1, 2, 0);
    }
    
}