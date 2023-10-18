package com.project.views.production;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import com.project.App;
import com.project.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Pane;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("View your machines")
@Route(value = "machines", layout = MainLayout.class)

public class MachinesView extends HorizontalLayout
{
    private ArrayList<HorizontalLayout> MachinesList;

    MachinesView()
    {
        //ask for the table of machines
        MachinesList = new ArrayList<>();
        try(Statement statement = App.manipDB.myConnection.createStatement()
        ){
            ResultSet queryResultSet = statement.executeQuery(
                "SELECT * FROM MACHINE"
            );
            while(!queryResultSet.isLast())
            {
                queryResultSet.next();
                Button deleteButton = new Button("Delete machine");
                String machineRef = queryResultSet.getString("REF");
                deleteButton.addClickListener(e -> {
                    try{
                        App.manipDB.mymanipmachines.deleteMachine(machineRef);
                        //somehow remove the fucking panel or reload the page
                        this.remove();
                    }
                    catch(SQLException err)
                    {
                        Notification.show("Impossible to connect delete button to machine with reference: " + machineRef);
                    }
                });
                HorizontalLayout myLayout = new HorizontalLayout(Alignment.AUTO, new Text(queryResultSet.getString("REF")), deleteButton);
                MachinesList.add(myLayout);
            }
            for(HorizontalLayout h : MachinesList)
            {
                this.add(h);
            }

        }
        catch(SQLException err){
            err.printStackTrace();
        }
    }


}
