package com.project;

public class MachineState 
{
    private String ref;
    private String state;

    public MachineState(String ref, String state) 
    {
        this.ref = ref;
        this.state = state;
    }

    public String getRef() 
    {
        return ref;
    }

    public String getState() 
    {
        return state;
    }
}
