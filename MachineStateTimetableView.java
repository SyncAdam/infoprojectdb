package com.project.views.production;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.project.views.MainLayout;
import java.sql.SQLException;
import java.util.List;
import com.project.ManipMachines;

@Route(value = "machineStateTimetable", layout = MainLayout.class)
@PageTitle("Machine State Timetable")
public class MachineStateTimetableView extends VerticalLayout 
{

    public MachineStateTimetableView(MachineService machineService) {
        Grid<ManipMachines.MachineStateTimetable> stateTimetableGrid = new Grid<>(ManipMachines.MachineStateTimetable.class);
        stateTimetableGrid.setColumns("state", "startTime", "endTime");
        stateTimetableGrid.getColumnByKey("state").setHeader("State");
        stateTimetableGrid.getColumnByKey("startTime").setHeader("Start Time");
        stateTimetableGrid.getColumnByKey("endTime").setHeader("End Time");

        String selectedMachineRef = "";  // Selected machineRef
        List<ManipMachines.MachineStateTimetable> stateHistory = null;

        try 
        {
            stateHistory = machineService.getMachineStateHistory(selectedMachineRef);
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }

        stateTimetableGrid.setItems(stateHistory);

        add(new H3("Machine State Timetable"));
        add(stateTimetableGrid);
    }
}
