package com.project.views.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import com.project.App;
import com.project.ProductType;
import com.project.views.MainLayout;
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

@PageTitle("View products")
@Route(value = "products", layout = MainLayout.class)

public class ProductCatalogView extends HorizontalLayout{

    ProductCatalogView()
    {
        ArrayList<VerticalLayout> columns = new ArrayList<>();
        VerticalLayout l1 = new VerticalLayout();
        VerticalLayout l2 = new VerticalLayout();
        VerticalLayout l3 = new VerticalLayout();

        columns.add(l1);
        columns.add(l2);
        columns.add(l3);

        try
        {
            ArrayList<ProductType> products = queryProductTypes();
            for(int i = 0; i < products.size(); i++)
            {
                columns.get(i % 3).add(createProductPane(products.get(i)));
            }
        }
        catch(SQLException e)
        {}
        

        this.add(l1, l2, l3);
    }

    public Div createProductPane(ProductType p)
    {

        Div uResult = new Div();
        uResult.getStyle().set("background-color", "#f2f2f2");
        uResult.getStyle().set("border-radius", "20px");

        VerticalLayout res = new VerticalLayout();

        Html label = new Html("<div style='font-size: 20px;'>" + p.getDescription() + "</div>");
        res.add(label);

        HorizontalLayout hL = new HorizontalLayout();
        Image img = new Image(p.pathToImage, "product");
        img.setMaxWidth("50%");

        Text description = new Text("Product reference : " + p.getReference());
        hL.add(img);
        hL.add(description);
        res.add(hL);

        HorizontalLayout hL2 = new HorizontalLayout();

        IntegerField buyField = new IntegerField();
        buyField.setValue(1);
        buyField.setStepButtonsVisible(true);
        buyField.setMin(0);
        buyField.setMax(99);

        hL2.add(buyField);

        Button buyButton = new Button();
        buyButton.setText("Order");

        buyButton.addClickListener(e -> {

            Dialog popup = new Dialog();
            int orderNumber = buyField.getValue();

            popup.setHeaderTitle("Confirm order");

            VerticalLayout dialogLayout = new VerticalLayout();

            dialogLayout.add(new Html("<div> " + "Do you wish to order " + orderNumber + " products of " + p.getDescription() + "?" +"</div>"));
            
            Button cancel = new Button("Cancel");
            Button confirm = new Button("Confirm order");

            cancel.addClickListener(e1 -> {
                popup.close();
            });

            confirm.addClickListener(e1 -> {

                for(int i = 0; i < orderNumber; i++)
                {
                    try
                    {   
                        String serial = p.getReference() + "-" + serialGenerator(15);
                        App.manipDB.myManipProducts.createProduct(p.getReference(), serial);
                    }
                    catch(SQLException err)
                    {
                        err.printStackTrace();
                    }
                }


                buyField.setValue(1);
                popup.close();
            });

            confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            popup.add(dialogLayout);
            popup.getFooter().add(cancel);
            popup.getFooter().add(confirm);

            popup.open();

        });

        hL2.add(buyButton);

        res.add(hL2);

        uResult.add(res);

        return uResult;
    }

    private ArrayList<ProductType> queryProductTypes() throws SQLException
    {
        ArrayList<ProductType> result = new ArrayList<>();
        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM PRODUCT"))
        {
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();
                result.add(new ProductType(res.getString("REF"), res.getString("DES"), 0, new ArrayList<>()));
            }
            while(!res.isLast());
        }
        return result;
    }

    public static String serialGenerator(int size) {
        String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder(size);

        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            char randomChar = allowedCharacters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        // Convert StringBuilder to String
        return stringBuilder.toString();
    }
    
}