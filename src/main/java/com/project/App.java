package com.project;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "project")
public class App implements AppShellConfigurator
{

    public static ManipDB manipDB;

    App()
    {
        
        manipDB = new ManipDB();
        
    }

    public static void main(String[] args)
    {
    
        SpringApplication.run(App.class, args);
        
    }
}
