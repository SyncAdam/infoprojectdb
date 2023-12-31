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
    private Button createProduct;

    public HelloWorldView() {
        deleteDB = new Button("Delete tables");
        createDB = new Button("Create tables");
        dummyMachines = new Button("Create dummy machines");
        createProduct = new Button("Create a bolt");

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
                App.manipDB.myManipMachines.loadMachineStates();
                App.manipDB.myManipMachines.loadDefaultMachinesCapabilities();
                Notification.show("Dummy machines created");
            }
            catch(SQLException err)
            {
                Notification.show("Unable to create dummy machines");
            }
        });
        createProduct.addClickListener(e -> {
            try{
                App.manipDB.myManipProducts.createProduct("FFFFFE", "AAAAAB");
            }
            catch(SQLException err)
            {
                err.printStackTrace();
                Notification.show("Impossible to create product");
            }
        });

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, deleteDB, createDB, dummyMachines, createProduct);

        add(deleteDB, createDB, dummyMachines, createProduct);
    }

}
