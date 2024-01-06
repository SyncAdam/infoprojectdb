package com.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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
        try{
            checkQueue();
            checkWok();
        } 
        catch(SQLException e){}
    }

    public void checkQueue() throws SQLException
    {
        ResultSet operationQueueQuery;
        ResultSet machinesQuery;

        ArrayList<Machine> machines = new ArrayList<>();

        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM MACHINE"))
        {
            machinesQuery = pStatement.executeQuery();
            do
            {
                machinesQuery.next();
                machines.add(new Machine(machinesQuery.getString("REF"), machinesQuery.getString("DES"), machinesQuery.getDouble("POWER"), machinesQuery.getInt("STATE")));
            }
            while(!machinesQuery.isLast());
        }
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE"))
        {
            operationQueueQuery = pStatement.executeQuery();
            do
            {
                operationQueueQuery.next();
                try(PreparedStatement pStatement2 = this.myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.ID = ?"))
                {
                    
                    int opID = operationQueueQuery.getInt("IDOPERATION");
                    int opEpoch;
                    int opIDTYPE;
                    ResultSet res;

                    pStatement2.setInt(1, opID);
                    res = pStatement2.executeQuery();
                    res.next();

                    opEpoch = res.getInt("NEPOCH");
                    opIDTYPE = res.getInt("IDTYPE");


                    //if the operations before the selected operation are done
                    

                    //there is a machine available assign product to machine
                    for(int i = 0; i < machines.size(); i++)
                    {
                        //optimize for selecting the fastest machine to do the job
                        if(Product.opbeforeCompleted(opEpoch, operationQueueQuery.getString("SERIAL"), this.myConnection) && machines.get(i).getState() == Machine.State.ONLINE && machines.get(i).does(opIDTYPE, this.myConnection) && !operationQueueQuery.getBoolean("OPERATIONEFFECTED"))
                        {    
                            System.out.println(machines.get(i).getRef()+" can work on " + opIDTYPE);
                            if(assignToMachine(machines.get(i).getRef(), operationQueueQuery.getString("SERIAL"), opID))
                            {
                                System.out.println("Assigned");
                                return;
                            } 
                        }
                    }

                }
            }
            while(!operationQueueQuery.isLast());
        }                
    }

    public boolean assignToMachine(String machineRef, String serial, int opID) throws SQLException
    {
        //set machine to busy
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("UPDATE MACHINE SET MACHINE.STATE = 2 WHERE MACHINE.REF = ?"))
        {
            pStatement.setString(1, machineRef);
            pStatement.executeUpdate();
        }
        //set operation in productqueue to workedon
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("UPDATE PRODUCTQUEUE SET PRODUCTQUEUE.OPERATIONEFFECTED = TRUE WHERE PRODUCTQUEUE.IDOPERATION = ? AND PRODUCTQUEUE.SERIAL = ?"))
        {
            pStatement.setInt(1, opID);
            pStatement.setString(2, serial);
            pStatement.executeUpdate();
        }

        //set machineworking and create timestamp
        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
                "INSERT INTO MACHINEWORKING "
                    + "(MACHINEREF, SERIAL, IDOPERATION, TIME) "
                    + "VALUES(?, ?, ?, ?);"
        ))
        {
            pstatement.setString(1, machineRef);
            pstatement.setString(2, serial);
            pstatement.setInt(3, opID);
            pstatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstatement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public void checkWok() throws SQLException
    {
        //identify things that are being worked on in productqueue
        //calculate elapsed time for such operations
        //if elapsed time is greater than the time in which the machine does the operation take action

        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM PRODUCTQUEUE WHERE PRODUCTQUEUE.OPERATIONEFFECTED = TRUE"))
        {
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();

                int productidOP = res.getInt("IDOPERATION");
                String serial = res.getString("SERIAL");

                try(PreparedStatement pStatement2 = this.myConnection.prepareStatement("SELECT * FROM MACHINEWORKING WHERE SERIAL = ?"))
                {
                    pStatement2.setString(1, serial);

                    ResultSet res2 = pStatement2.executeQuery();

                    long currentTime = System.currentTimeMillis();


                    //think you've seen nesting before???
                    //there is a proper way to write code...........
                    do
                    {
                        res2.next();
                        
                        String machineref = res2.getString("MACHINEREF");
                        int idOperationType = getIDOperationType(res2.getInt("IDOPERATION"));

                        if(productidOP == res2.getInt("IDOPERATION"))
                        {
                            try(PreparedStatement pStatement3 = this.myConnection.prepareStatement("SELECT * FROM REALISE WHERE REALISE.MACHINEREF = ? AND REALISE.IDTYPE = ?"))
                            {
                                pStatement3.setString(1, machineref);
                                pStatement3.setInt(2, idOperationType);

                                ResultSet res3 = pStatement3.executeQuery();

                                res3.next();

                                long doTime = (long) res3.getDouble("DUREE");
                                
                                long workTime = res2.getTimestamp("TIME").getTime();
                                long deltaT = (currentTime - workTime) / 1000;

                                if(deltaT >= doTime)
                                {
                                    if(unassignFromMachine(machineref, serial, productidOP)) System.out.println("Work unassigned from machine " + res2.getString("MACHINEREF"));
                                    return;
                                }
                            }
                            catch(SQLException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    while(!res2.isLast());
                }
            }
            while(!res.isLast());
        }
    }

    public boolean unassignFromMachine(String machineRef, String serial, int opID) throws SQLException
    {
        //set machine to free
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("UPDATE MACHINE SET MACHINE.STATE = 1 WHERE MACHINE.REF = ?"))
        {
            pStatement.setString(1, machineRef);
            pStatement.executeUpdate();
        }
        //remove operation from productqueue
        //==============================================================================
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("DELETE FROM PRODUCTQUEUE WHERE PRODUCTQUEUE.IDOPERATION = ? AND PRODUCTQUEUE.SERIAL = ?"))
        {
            pStatement.setInt(1, opID);
            pStatement.setString(2, serial);
            pStatement.executeUpdate();
        }

        //set machineworking and create timestamp
        try(PreparedStatement pstatement = this.myConnection.prepareStatement(
                "INSERT INTO MACHINEWORKING "
                    + "(MACHINEREF, SERIAL, IDOPERATION, TIME) "
                    + "VALUES(?, ?, ?, ?);"
        ))
        {
            pstatement.setString(1, machineRef);
            pstatement.setNull(2, Types.VARCHAR);
            pstatement.setNull(3, Types.INTEGER);
            pstatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstatement.executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public int getIDOperationType(int idoperation) throws SQLException
    {
        try(PreparedStatement pStatement = this.myConnection.prepareStatement("SELECT * FROM OPERATIONS WHERE OPERATIONS.ID = ?"))
        {
            pStatement.setInt(1, idoperation);
            ResultSet r = pStatement.executeQuery();

            r.next();

            return r.getInt("IDTYPE");
        }
    }

}