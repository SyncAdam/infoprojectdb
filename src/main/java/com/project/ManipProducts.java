package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManipProducts {

    private Connection myConnection;

    ManipProducts(Connection connection)
    {  
        this.myConnection = connection;
    }

    //later remove staticity
    public static void addProduct(String ref, String des, Connection con) throws SQLException
    {
        con.setAutoCommit(false);

        try(PreparedStatement pStatement = con.prepareStatement(
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
            con.rollback();
        }

        con.setAutoCommit(true);
    }
    
}
