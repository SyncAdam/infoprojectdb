package com.project;

import java.sql.Timestamp;

public class MachineStateTimetable 
{

    private String state;
    private Timestamp startTime;
    private Timestamp endTime;

    public MachineStateTimetable(String state, Timestamp startTime, Timestamp endTime) 
    {
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getState()
     {
        return state;
    }

    public Timestamp getStartTime() 
    {
        return startTime;
    }

    public Timestamp getEndTime() 
    {
        return endTime;
    }
}