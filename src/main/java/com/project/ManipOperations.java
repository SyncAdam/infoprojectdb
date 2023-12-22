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
    public static void addOperation(int idproduct, int idtype, Connection con) throws SQLException
    {
        con.setAutoCommit(false);

        try(PreparedStatement pStatement = con.prepareStatement(
            "INSERT INTO OPERATIONS "
            + "(IDTYPE, IDPRODUCT) "
            + "VALUES(?, ?);"
        ))
        {
            pStatement.setInt(1, idtype);
            pStatement.setInt(2, idproduct);
            pStatement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            con.rollback();
        }

        con.setAutoCommit(true);
    }
    
}
