package com.project.views.helloworld;

import java.sql.SQLException;

import com.project.App;
import com.project.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Industry Manager")
@Route(value = "hello", layout = MainLayout.class)
public class HelloWorldView extends HorizontalLayout {

    private Button deleteDB;
    private Button createDB;
    private Button dummyMachines;

    public HelloWorldView() {
        deleteDB = new Button("Delete tables");
        createDB = new Button("Create tables");
        dummyMachines = new Button("Create dummy machines");

        deleteDB.addClickListener(e -> {
            try{
                App.manipDB.deleteSchema();
                Notification.show("Tables deleted");
            }
            catch(SQLException err)
            {
                Notification.show("Unable to delete tables");
            }
            
        });
        createDB.addClickListener(e -> {
            try{
                App.manipDB.createSchema();
                App.manipDB.myManipOperationTypes.loadDefaultOperationTypes();
                App.manipDB.myManipProducts.loadDefaultProducts();
                Notification.show("Tables created");
            }
            catch(SQLException err)
            {
                Notification.show("Unable to create tables");
            }
        });
        dummyMachines.addClickListener(e -> {
            try{
                App.manipDB.myManipMachines.loadDefaultMachines();
                Notification.show("Dummy machines created");
            }
            catch(SQLException err)
            {
                Notification.show("Unable to create dummy machines");
            }
        });

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, deleteDB, createDB, dummyMachines);

        add(deleteDB, createDB, dummyMachines);
    }

}
