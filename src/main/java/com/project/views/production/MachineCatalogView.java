package com.project.views.production;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.MachineType;
import com.project.ManipMachines;
import com.project.views.MainLayout;
import com.project.views.products.ProductCatalogView;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
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

        List<MachineType> machineTypes = ManipMachines.queryMachineTypes();
        for(int i = 0; i < machineTypes.size(); i++)
        {
            vLayouts.get(i % 3).add(createMachineTypePane(machineTypes.get(i)));
        }

        this.add(vLayouts.get(0), vLayouts.get(1), vLayouts.get(2));
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

        HorizontalLayout hlayout = new HorizontalLayout();

        IntegerField buyField = new IntegerField();
        buyField.setValue(1);
        buyField.setStepButtonsVisible(true);
        buyField.setMin(0);
        buyField.setMax(10);
        buyField.setWidth("80px");

        hlayout.add(buyField);

        Div buyDiv = new Div();
        buyDiv.getStyle().set("width", "100%");
        buyDiv.getStyle().set("justify-content", "right");
        Button buyButton = new Button("Acquire");
        buyButton.addClickListener(e -> {

            Dialog popup = new Dialog();
            popup.setHeaderTitle("Acquire machine");

            int number = buyField.getValue();

            Html text = new Html("<div>Are you sure you want to add " + number + " " + m.getName() + " machines?" + "</div>");
            popup.add(text);
            
            Button confirm = new Button("Confirm");
            confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            
            confirm.addClickListener(event -> {
                addMachine(m, number);
                popup.close();
            });

            popup.add(confirm);

            popup.open();
        });
        buyButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        hlayout.add(buyButton);
        
        buyDiv.add(hlayout);

        res.add(buyDiv);

        result.add(res);        

        return result;
    }

    private void addMachine(MachineType m, int num)
    {
        for(int i = 0; i < num; i++)
        {
            try
            {
                String reference = ProductCatalogView.serialGenerator(7);
                ManipMachines.createMachine(reference, m.getModel());
            }
            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    
}
