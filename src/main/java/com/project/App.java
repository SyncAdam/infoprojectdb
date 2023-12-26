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
    public static ArrayList<Product> productQueue;

    App()
    {
        manipDB = new ManipDB();
        productQueue = new ArrayList<>();
    }

    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }
}