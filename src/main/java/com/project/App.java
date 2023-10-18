package com.project;

import java.sql.SQLException;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "project")
public class App implements AppShellConfigurator
{
    public static void main( String[] args )
    {
    
        SpringApplication.run(App.class, args);
        
        System.out.println( "Hello World!" );
        ManipDB databaseManip = new ManipDB();
        try{
            databaseManip.deleteSchema();
            databaseManip.createSchema();
            //databaseManip.mymanipmachines.createMachine("F03", "bruh", 200);
            databaseManip.mymanipmachines.loadDefaultMachines();

        }
        catch(SQLException e)
        {
            //e.printStackTrace();
        }
        try{
            databaseManip.mymanipmachines.deleteMachine("F03");
        }
        catch(SQLException e)
        {
            //e.printStackTrace();
        }
    }
}
