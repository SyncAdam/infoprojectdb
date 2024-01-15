package com.project;

import java.sql.SQLException;
import java.sql.Timestamp;

public class History {

    private String serial;
    private String machineRef;
    private Timestamp time;
    private int operationID;

    public History(String serial, String machineRef, Timestamp time, int operationID)
    {
        this.serial = serial;
        this.machineRef = machineRef;
        this.time = time;
        this.operationID = operationID;
    }

    public String getSerial()
    {
        return this.serial;
    }

    public String getMachineRef()
    {
        return this.machineRef;
    }

    public Timestamp getTime()
    {
        return this.time;
    }

    public String getTimeString()
    {
        return this.time.toString();
    }

    public int getOperationID()
    {
        return this.operationID;
    }

    public String getOperationType()
    {
        String result = "";

        try
        {
            result = ManipOperations.getOperationTypeByID(this.operationID);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    
}
