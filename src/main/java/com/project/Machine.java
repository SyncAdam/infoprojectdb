package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.project.views.MainLayout;

public class Machine {

    public enum State
    {
        OFFLINE,    //0
        ONLINE,     //1
        WORKING,    //2
        WAITING,    //3
        WFM         //4 waiting for maintenance
    }

    private String ref;
    private String model;
    private State machinestate;

    Machine(String reference, String model)
    {
        this.ref = reference;
        this.model = model;
        this.machinestate = State.ONLINE;
    }

    public Machine(String reference, String model, int machineState)
    {
        this.ref = reference;
        this.model = model;
        switch(machineState)
        {
            case 0:
                this.machinestate = State.OFFLINE;
                break;
            case 1:
                this.machinestate = State.ONLINE;
                break;
            case 2:
                this.machinestate = State.WORKING;
                break;
            case 3:
                this.machinestate = State.WAITING;
                break;
            case 4:
                this.machinestate = State.WFM;
                break;
            default:
                this.machinestate = State.OFFLINE;
        }
    }

    public State getState()
    {
        return this.machinestate;
    }

    public String getRef()
    {
        return this.ref;
    }

    public String getDes()
    {
        String result = "";

        List<MachineType> machines = ManipMachines.queryMachineTypes();

        for(MachineType m : machines)
        {
            if(m.model.equals(this.model)) result = m.getDescription();
        }

        return result;
    }

    public double getPower()
    {
        double result = 0.0;

        List<MachineType> machines = ManipMachines.queryMachineTypes();

        for(MachineType m : machines)
        {
            if(m.model.equals(this.model)) result = m.getPower();
        }

        return result;
    }

    public boolean does(int operationTypeID, Connection myConnection) throws SQLException
    {
        try(PreparedStatement pStatement = myConnection.prepareStatement("SELECT * FROM REALISE WHERE REALISE.MTYPE = ? AND REALISE.IDTYPE = ?"))
        {

            pStatement.setString(1, getTypeFromModel(this.model));
            pStatement.setInt(2, operationTypeID);

            ResultSet r = pStatement.executeQuery();

            return r.next();
        }
    }

    public static String getTypeFromModel(String model)
    {
        String result = "";

        List<MachineType> machines = ManipMachines.queryMachineTypes();

        for(MachineType m : machines)
        {
            if(m.model.equals(model)) result = m.getTypeString();
        }

        return result;
    }

    public static String getTypeFromRef(String ref) throws SQLException
    {
        String result = "";

        try(PreparedStatement pStatement = MainLayout.manipDB.myConnection.prepareStatement("SELECT * FROM MACHINE WHERE REF = ?"))
        {
            pStatement.setString(1, ref);
            ResultSet r = pStatement.executeQuery();

            r.next();
            result = Machine.getTypeFromModel(r.getString("MODEL"));
        }
        catch(SQLException err)
        {
            System.out.println("Unable to obtain type from reference");
        }

        return result;
    }
}
