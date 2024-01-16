package com.project;

import com.project.views.MainLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@Theme(value = "project")
public class App extends SpringBootServletInitializer implements AppShellConfigurator
{

    public static BGService myBgService;
    private Connection connection;

    private final String username = "m3_asinkovics01";
    private final String password = "0ecd918c";
    
    private final String host = "92.222.25.165";
    private final int port = 3306;
    private final String database = "m3_asinkovics01";

    App()
    {  
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = establishConnection(host, port, database);
            myBgService = new BGService(this.connection);
        }
        catch(SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }

    private Connection establishConnection(String host, int port, String database) throws SQLException
    {
            Connection con = DriverManager.getConnection(
            "jdbc:mysql://" + host + ":" + port
            + "/" + database,
            this.username, this.password);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }
}