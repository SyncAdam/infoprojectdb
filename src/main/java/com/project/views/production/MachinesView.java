package com.project.views.production;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.project.App;
import com.project.History;
import com.project.Machine;
import com.project.views.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("View your machines")
@Route(value = "machines", layout = MainLayout.class)

public class MachinesView extends HorizontalLayout
{

    MachinesView()
    {

        ArrayList<VerticalLayout> vLayouts = new ArrayList<>();
        
        VerticalLayout v1 = new VerticalLayout();
        VerticalLayout v2 = new VerticalLayout();

        vLayouts.add(v1);
        vLayouts.add(v2);

        try
        {  
            ArrayList<Machine> machines = queryMachines();
            for(int i = 0; i < machines.size(); i++)
            {
                vLayouts.get(i % 2).add(createMachinePane(machines.get(i)));
            }
        }
        catch(SQLException e)
        {}

        this.add(vLayouts.get(0), vLayouts.get(1));
    }

    public ArrayList<Machine> queryMachines() throws SQLException
    {
        ArrayList<Machine> result = new ArrayList<>();
        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM MACHINE"))
        {
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();
                result.add(new Machine(res.getString("REF"), res.getString("MODEL"), res.getInt("STATE")));
            }
            while(!res.isLast());
        }
        return result;
    }

    public Div createMachinePane(Machine m) throws SQLException
    {
        Div uResult = new Div();
        uResult.getStyle().set("width", "100%");
        uResult.getStyle().set("background-color", "#f2f2f2");
        uResult.getStyle().set("border-radius", "20px");


        VerticalLayout result = new VerticalLayout();

        Div myDiv = new Div();
        myDiv.getStyle().set("width", "100%");
        myDiv.getStyle().set("display", "flex");
        myDiv.getStyle().set("justify-content", "space-between");
        myDiv.getStyle().set("align-items", "center");

        
        Html label = new Html("<div style='font-size: 20px;'>" + m.getRef() + "</div>");
        myDiv.add(label);

        String circleHtmlP1 = "<div style='width: 8px; height: 8px; border-radius: 50%; display: flex; align-items: center; justify-content: center;color: #d2d2d2; font-weight: bold; background-color: ";

        String circlecolor;
        switch(m.getState())
        {
            case OFFLINE:
                circlecolor = "#000000"; // black
                break;
            case ONLINE:
                circlecolor = "#00ff00"; // green
                break;
            case WORKING:
                circlecolor = "#ffcc00"; // orange
                break;
            case WAITING:
                circlecolor = "#ffcc00"; // orange
                break;
            case WFM:
                circlecolor = "#ff0000"; // orange
                break;
            default:
                circlecolor = "#000000"; // black
                break;
        }

        String circleHtmlP2 = ";'></div>";

        Html circle = new Html(circleHtmlP1 + circlecolor + circleHtmlP2);
        myDiv.add(circle);

        result.add(myDiv);

        //Machine history
        try
        {
            List<History> histories = queryHistory(m.getRef());

            Grid<History> historyGridMachine = new Grid<>(History.class, false);
            historyGridMachine.addColumn(History::getSerial).setHeader("Serial");
            historyGridMachine.addColumn(History::getOperationType).setHeader("Op Type");
            historyGridMachine.addColumn(History::getTimeString).setHeader("Time");

            historyGridMachine.setItems(histories);

            for(Column<History> c : historyGridMachine.getColumns())
            {
                if(c.getHeaderText().equals("Op Type"))
                    c.setWidth("5px");
            }

            result.add(historyGridMachine);
        }
        catch(SQLException err)
        {}
    
        Button deleteButton = new Button("Delete machine");
        String machineRef = m.getRef();

        deleteButton.addClickListener(e -> {
            try{
                App.manipDB.myManipMachines.deleteMachine(machineRef);
                getUI().get().getPage().reload();
            }
            catch(SQLException err)
            {
                Notification.show("Impossible to connect delete button to machine with reference: " + machineRef);
            }
        });

        result.add(deleteButton);

        uResult.add(result);

        return uResult;
    }

    private ArrayList<History> queryHistory(String machineref) throws SQLException
    {
        ArrayList<History> result = new ArrayList<>();

        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM MACHINEWORKING WHERE MACHINEWORKING.MACHINEREF = ?"))
        {
            pStatement.setString(1, machineref);
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();
                if(res.getInt("IDOPERATION") != 0)
                    result.add(new History(res.getString("SERIAL"), machineref, res.getTimestamp("TIME"), res.getInt("IDOPERATION")));
            }
            while(!res.isLast());
        }
        Collections.reverse(result);
        return result;
    }

}