package com.project;

import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ManipDB databaseManip = new ManipDB();
        try{
            databaseManip.mymanipmachines.createMachine("F03", "bruh", 200);
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        try{
            databaseManip.mymanipmachines.deleteMachine("F03");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}
