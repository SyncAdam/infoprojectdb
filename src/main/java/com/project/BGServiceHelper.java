package com.project;

public class BGServiceHelper {

    int operationID;
    String serial;
    boolean operationEffected;

    BGServiceHelper(int operationID, String serial, boolean operationEffected)
    {
        this.operationEffected = operationEffected;
        this.operationID = operationID;
        this.serial = serial;
    }

    public int getOperationID()
    {
        return this.operationID;
    }

    public String getSerial()
    {
        return this.serial;
    }

    public boolean getOperationEffected()
    {
        return this.operationEffected;
    }

    
}
