package com.project.views.production;

import com.project.App;
import com.project.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

@PageTitle("View your machines")
@Route(value = "machines", layout = MainLayout.class)
public class MachinesView extends VerticalLayout {

    private final ArrayList<VerticalLayout> machinesList;

    public MachinesView() {
        machinesList = new ArrayList<>();

        try {
            List<Machine> allMachines = App.manipDB.mymanipmachines.getAllMachines();

            for (Machine machine : allMachines) 
            {
                createMachineLayout(machine);
            }

            for (VerticalLayout machineLayout : machinesList) 
            {
                this.add(machineLayout);
            }

        } catch (SQLException err) 
        {
            err.printStackTrace();
            Notification.show("Error retrieving machine data");
        }
    }

    private void createMachineLayout(Machine machine) 
    {
        String machineRef = machine.getRef();
        String machineState = machine.getState();

        // Avoid the NullPointerException
        if (machineState != null) {
            Button deleteButton = new Button("Delete machine");
            Button changeStateButton = new Button("Change State");

            deleteButton.addClickListener(e -> {
                try {
                    App.manipDB.mymanipmachines.deleteMachine(machineRef);
                    // Remove the layout from the UI
                    machinesList.removeIf(layout -> layout.getComponentAt(0).getClass().equals(Text.class)
                            && ((Text) layout.getComponentAt(0)).getText().equals("Reference: " + machineRef));
                    this.remove(deleteButton.getParent().get());
                } catch (SQLException err) {
                    Notification.show("Unable to delete machine with reference: " + machineRef);
                }
            });
        
            changeStateButton.addClickListener(e -> {
                // change state in db
                try {
                    App.manipDB.mymanipmachines.changeMachineState(machineRef, "New State");
                    Notification.show("Machine state changed to New State");
                    // Refresh UI
                    createMachineLayout(machine);
                } catch (SQLException err) {
                    Notification.show("Unable to change state for machine with reference: " + machineRef);
                }
            });
        
            VerticalLayout machineLayout = new VerticalLayout(
                new Text("Reference: " + machineRef),
                new Text("Description: " + machine.getDescription()),
                new Text("Power: " + machine.getPower()),
                new Text("State: " + machineState),
                changeStateButton,
                deleteButton
            );
        
            switch (machineState) {
                case "Available":
                    machineLayout.getStyle().set("background-color", "green");
                    break;
                case "Idle":
                    machineLayout.getStyle().set("background-color", "yellow");
                    break;
                case "In Maintenance":
                    machineLayout.getStyle().set("background-color", "red");
                    break;
                default:
                    machineLayout.getStyle().set("background-color", "white");
            }
        
            machinesList.add(machineLayout);
        }
    }
}
