package com.project;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class ManipMachines {

    private Connection myConnection;

    ManipMachines(Connection establishedConnection)
    {
        this.myConnection = establishedConnection;
    }

    /**
     * Function that creates a machine in the database based on the parameters
     * @param reference     <code>String</code> The reference number of the machine 
     * @param description   <code>String</code> A description of the machine 
     * @param power         <code>Float</code> The machines power 
     */
    void createMachine(String reference, String description, float power) throws SQLException
    {
        //disable autocommit to create a statement
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
            "INSERT INTO MACHINE "
                + "(REF, DES, POWER, STATE) "
                + "VALUES(?, ?, ?, ?);"
        ))
        {
            //pstatement.setInt(1, ?);
            pstatement.setString(1, reference);
            pstatement.setString(2, description);
            pstatement.setFloat(3, power);
            pstatement.setInt(4, 1);
            pstatement.executeUpdate();
            System.out.println("Machine created");
        }

        //reenable autocommit after the statement is finished
        this.myConnection.setAutoCommit(true);
    }

    /**
     * Function that deletes a machine in the database based on its ID in the database
     * @param id
     */
    void deleteMachine(int id) throws SQLException
    {
        //disable autocommit to create a statement
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
            "DELETE FROM MACHINE"
                + "WHERE ID = ?;"
        ))
        {
            pstatement.setInt(1, id);
            pstatement.executeUpdate();
            System.out.println("Machine deleted");
        }

        //reenable autocommit after the statement is finished
        this.myConnection.setAutoCommit(true);
    }

    /**
     * Function that deletes a machine based on its reference number
     * @param ref
     */
    public void deleteMachine(String ref) throws SQLException
    {
        //disable autocommit to create a statement
        this.myConnection.setAutoCommit(false);

        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
            "DELETE FROM MACHINE "
                + "WHERE REF = ?;"))
        {
            pstatement.setString(1, ref);
            pstatement.executeUpdate();
            System.out.println("Machine deleted");
        }

        //reenable autocommit after the statement is finished
        this.myConnection.setAutoCommit(true);
    }

    /**
     * Load default dummy machines for testing
     */
    public void loadDefaultMachines() throws SQLException
    {
        try{
            createMachine("F01", "rapide", 20);
            createMachine("F02", "lente", 10);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("Impossible to create default machines");
        }

    }

    public void loadMachineStates() throws SQLException
    {
        // query for all machines and set them to off at first
        this.myConnection.setAutoCommit(false);

        try(Statement statement = this.myConnection.createStatement())
        {
            ResultSet res = statement.executeQuery("SELECT * FROM MACHINE");

            do
            {
                res.next();
                try(PreparedStatement pstatement = this.myConnection.prepareStatement(
                "INSERT INTO MACHINEWORKING "
                    + "(IDMACHINE, IDOPERATIONTYPE, TIME) "
                    + "VALUES(?, ?, ?);"
                ))
                {
                    //pstatement.setInt(1, ?);
                    pstatement.setInt(1, res.getInt("ID"));
                    pstatement.setInt(2, 14);
                    pstatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    pstatement.executeUpdate();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }
            while(!res.isLast());

        }

        this.myConnection.setAutoCommit(true);

    }
}