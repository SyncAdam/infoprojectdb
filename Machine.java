package com.project.views.production;

public class Machine 
{
    private String ref;
    private String description;
    private float power;
    private String state;

    public Machine(String ref, String description, float power) 
    {
        this.ref = ref;
        this.description = description;
        this.power = power;
        this.state = "Available";
    }
    public void setState(String state) 
    {
        this.state = state;
    }

    public String getRef() 
    {
        return ref;
    }

    public String getDescription() 
    {
        return description;
    }

    public float getPower() 
    {
        return power;
    }

    public String getState() 
    {
        return state;
    }
}
