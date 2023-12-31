package com.project;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "project")
public class App implements AppShellConfigurator
{

    public static ManipDB manipDB;
    public static ArrayList<ProductType> productQueue;
    public static BGService myBgService;

    App()
    {
        manipDB = new ManipDB();
        productQueue = new ArrayList<>();
        myBgService = new BGService(App.manipDB.myConnection);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }
}