package com.project;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public static void createMachine(String reference, String model) throws SQLException
    {
        //disable autocommit to create a statement
        App.manipDB.myConnection.setAutoCommit(false);

        try(PreparedStatement pstatement = App.manipDB.myConnection.prepareStatement(
            "INSERT INTO MACHINE "
                + "(REF, MODEL, STATE) "
                + "VALUES(?, ?, ?);"
        ))
        {
            pstatement.setString(1, reference);
            pstatement.setString(2, model);
            pstatement.setInt(3, 1);
            pstatement.executeUpdate();
            System.out.println("Machine created");

            loadMachineState(reference);
        }

        //reenable autocommit after the statement is finished
        App.manipDB.myConnection.setAutoCommit(true);
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
    public void loadDefaultMachinesTypes() throws SQLException
    {
        List<MachineType> machineTypes = queryMachineTypes();

        for(MachineType m : machineTypes)
        {
            addOperationsForMachineType(m);
        }
    }

    public void addOperationsForMachineType(MachineType m) throws SQLException
    {
        ArrayList<Integer> operations = MachineType.getOperationIDs(m.getType());
        for(int i = 0; i < operations.size(); i++)
        {
            try(PreparedStatement pStatement = this.myConnection.prepareStatement("INSERT INTO REALISE (MTYPE, IDTYPE, DUREE) VALUES (?, ?, ?);"))
            {
                pStatement.setString(1, m.getTypeString());
                pStatement.setInt(2, operations.get(i));
                pStatement.setDouble(3, 20.0);

                pStatement.executeUpdate();
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                throw(e);
            }
        }
    }

    public static void loadMachineState(String ref) throws SQLException
    {
        try(PreparedStatement pstatement = App.manipDB.myConnection.prepareStatement(
        "INSERT INTO MACHINEWORKING "
            + "(MACHINEREF, SERIAL, IDOPERATION, TIME) "
            + "VALUES(?, ?, ?, ?);"
        ))
        {
            //pstatement.setInt(1, ?);
            pstatement.setString(1, ref);
            pstatement.setNull(2, Types.VARCHAR);
            pstatement.setNull(3, Types.INTEGER);
            pstatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstatement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void loadMachineState() throws SQLException
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
                    + "(MACHINEREF, SERIAL, IDOPERATION, TIME) "
                    + "VALUES(?, ?, ?, ?);"
                ))
                {
                    //pstatement.setInt(1, ?);
                    pstatement.setString(1, res.getString("REF"));
                    pstatement.setNull(2, Types.VARCHAR);
                    pstatement.setNull(3, Types.INTEGER);
                    pstatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
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

    public static List<MachineType> queryMachineTypes()
    {
        List<MachineType> result = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        try{
            File resource = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\META-INF\\resources\\machinecatalog\\machineType.json");
            result = Arrays.asList(objectMapper.readValue(resource, MachineType[].class));
        }
        catch (IOException err)
        {
            err.printStackTrace();
        }

        return result;
    }
}