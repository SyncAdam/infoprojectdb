package com.project.views.production;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.MachineType;
import com.project.views.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Machine catalog")
@Route(value = "machine catalog", layout = MainLayout.class)
public class MachineCatalogView extends HorizontalLayout{

    MachineCatalogView()
    {
        ArrayList<VerticalLayout> vLayouts = new ArrayList<>();
        
        VerticalLayout v1 = new VerticalLayout();
        VerticalLayout v2 = new VerticalLayout();
        VerticalLayout v3 = new VerticalLayout();

        vLayouts.add(v1);
        vLayouts.add(v2);
        vLayouts.add(v3);

        List<MachineType> machineTypes = queryMachineTypes();
        for(int i = 0; i < machineTypes.size(); i++)
        {
            vLayouts.get(i % 3).add(createMachineTypePane(machineTypes.get(i)));
        }

        this.add(vLayouts.get(0), vLayouts.get(1), vLayouts.get(2));
    }

    private List<MachineType> queryMachineTypes()
    {
        List<MachineType> result = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        try{
            File resource = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\META-INF\\resources\\machinecatalog\\machineType.json");
            result = Arrays.asList(objectMapper.readValue(resource, MachineType[].class));
        }
        catch (IOException err)
        {
            err.printStackTrace();
        }

        return result;
    }

    private Div createMachineTypePane(MachineType m)
    {
        Div result = new Div();
        result.getStyle().set("background-color", "#f2f2f2");
        result.getStyle().set("border-radius", "20px");

        VerticalLayout res = new VerticalLayout();

        Html label = new Html("<div style='font-size: 20px;'>" + m.getName() + "</div>");
        res.add(label);
        Html label1 = new Html("<div style='font-size: 16px;'>" + m.getModel() + "</div>");
        res.add(label1);

        HorizontalLayout hL = new HorizontalLayout();
        Image img = new Image(m.getPathToImage(), "product");
        img.setMaxWidth("50%");

        Text description = new Text("Manufactured by: \n" + m.getManufacturer() + "\n" + m.getDescription());
        hL.add(img);
        hL.add(description);
        res.add(hL);

        Div buyDiv = new Div();
        buyDiv.getStyle().set("justify-content", "right");
        Button buyButton = new Button("Acquire");
        buyButton.addClickListener(e -> {
            System.out.println("I want to acquire a machine " + m.getModel());
        });
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buyDiv.add(buyButton);

        res.add(buyDiv);

        result.add(res);        

        return result;
    }
    
}
