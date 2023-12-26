package com.project;

public class Machine {

    enum State
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
}
