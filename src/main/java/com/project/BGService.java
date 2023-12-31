package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BGService {

    private static final int initialDelay = 0;
    private static final int rerunTime_seconds = 1;

    private Connection myConnection;

    BGService(Connection connection)
    {
        this.myConnection = connection;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::runTask, initialDelay, rerunTime_seconds, TimeUnit.SECONDS);
    }

    public void runTask()
    {
        //System.out.println("Hello World");
    }

    public void checkQueue() throws SQLException
    {
        ResultSet productQueueQuery;
        ResultSet machinesQuery;
        ResultSet machinesRealiseQuery;

        ArrayList<Machine> machines = new ArrayList<>();
        ArrayList<Product> productQueue = new ArrayList<>();

        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM MACHINE"))
        {
            machinesQuery = pStatement.executeQuery();
        }
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE"))
        {
            productQueueQuery = pStatement.executeQuery();
        }

        do
        {
            machinesQuery.next();
            machines.add(new Machine(machinesQuery.getString("REF"), machinesQuery.getString("DES"), machinesQuery.getDouble("POWER")));
        }
        while(!machinesQuery.isLast());

        do
        {
            productQueueQuery.next();
            productQueue.add(new Product(productQueueQuery.getString("SERIAL"), ManipProducts.getProductType(productQueueQuery.getString("REF"), this.myConnection)));
        }
        while(!productQueueQuery.isLast());

        //I need the realise table!!!!!!!!!!!!!!!!!!!!!!!

        for(int i = 0; i < productQueue.size(); i++)
        {
            for(int j = 0; j < machines.size(); j++)
            {
                for(int k = 0; k < productQueue.get(i).operationsNotDone.size(); k++)
                {
                    //if the operations before the selected operations are done, if the product is not worked on at the moment, and there is a machine available assign product to machine
                    //if(machines.get(j).realises(productQueue.get(i).operationsNotDone.get(k).idtype))
                }
            }
        }

    }

}