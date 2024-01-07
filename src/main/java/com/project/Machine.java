package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private String description;
    private double power;
    private State machinestate;

    Machine(String reference, String description, double power)
    {
        this.ref = reference;
        this.description = description;
        this.power = power;
        this.machinestate = State.ONLINE;
    }

    public Machine(String reference, String description, double power, int machineState)
    {
        this.ref = reference;
        this.description = description;
        this.power = power;
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
        return this.description;
    }

    public double getPower()
    {
        return this.power;
    }

    public boolean does(int operationTypeID, Connection myConnection) throws SQLException
    {
        try(PreparedStatement pStatement = myConnection.prepareStatement("SELECT * FROM REALISE WHERE REALISE.MACHINEREF = ? AND REALISE.IDTYPE = ?"))
        {
            pStatement.setString(1, this.ref);
            pStatement.setInt(2, operationTypeID);

            ResultSet r = pStatement.executeQuery();

            return r.next();
        }
    }
}
