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
    
}