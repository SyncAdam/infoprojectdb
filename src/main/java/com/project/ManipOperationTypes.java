package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManipOperationTypes {

    private Connection myConnection;

    ManipOperationTypes(Connection establishedConnection)
    {
        this.myConnection = establishedConnection;
    }

    public void createOperationType(String description) throws SQLException
    {
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
            "INSERT INTO OPERATIONTYPE (ID, DES)"
                + "VALUES(NULL, ?);"))
        {
            pstatement.setString(1, description);
            pstatement.executeUpdate();
        }

        this.myConnection.setAutoCommit(true);
    }

    public void loadDefaultOperationTypes() throws SQLException
    {
        try{
            createOperationType("MILLING");     //fraisage (CNC)
            createOperationType("DRILLING");    //forage?
            createOperationType("TURNING");     //usinage (tournage)
            createOperationType("GRINDING");    //affutage?
            createOperationType("THREADING");   //filetage
            createOperationType("TAPPING");     //tarodage
            createOperationType("POLISHING");   //polissage
            createOperationType("ETCHING");
            createOperationType("SAWING");
            createOperationType("PRESSING");
            createOperationType("HEATING");
            createOperationType("PLACING");

            System.out.println("Default operations loaded.");
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
