package com.project.views.products;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.App;
import com.project.History;
import com.project.ManipProducts;
import com.project.Product;
import com.project.ProductType;
import com.project.views.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("View Stock")
@Route(value = "stock", layout = MainLayout.class)

public class StockView extends HorizontalLayout{

    StockView()
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
            ArrayList<ProductType> products = queryStock();

            ArrayList<String> productsRef = new ArrayList<>();
            ArrayList<Integer> productsCounter = new ArrayList<>();

            ArrayList<ProductType> diffProductTypes = new ArrayList<>();

            for(ProductType p : products)
            {
                if(!refAlreadyRegistered(p.getReference(), productsRef))
                {
                    productsRef.add(p.getReference());
                    productsCounter.add(1);
                    diffProductTypes.add(p);
                }
                else
                {
                    int refIndex = getRefIndex(p.getReference(), productsRef);
                    productsCounter.set(refIndex, productsCounter.get(refIndex) + 1);
                }
            }

            for(int i = 0; i < productsRef.size(); i++)
            {
                columns.get(i % 3).add(createStockPane(productsRef.get(i), productsCounter.get(i), diffProductTypes.get(i), productsCounter.get(i)));
            }
        }
        catch(SQLException e)
        {}

        this.add(l1, l2, l3);
    }

    private Div createStockPane(String productReference, Integer productCount, ProductType p, int num) {

        Div uResult = new Div();
        uResult.getStyle().set("background-color", "#f2f2f2");
        uResult.getStyle().set("border-radius", "20px");

        VerticalLayout res = new VerticalLayout();

        Html label = new Html("<div style='font-size: 20px;'>" + p.getDescription() + "(s)" +"</div>");
        res.add(label);

        HorizontalLayout hL = new HorizontalLayout();
        Image img = new Image(p.pathToImage, "product");
        img.setMaxWidth("50%");

        Text description = new Text("Product reference : " + productReference);
        hL.add(img);
        hL.add(description);
        res.add(hL);

        Div footer = new Div();
        footer.getStyle().set("width", "100%");
        footer.getStyle().set("display", "flex");
        footer.getStyle().set("justify-content", "space-between");
        footer.getStyle().set("align-items", "center");

        Html number = new Html("<div style='font-size: 16px; color: lightblue'>" + num + "</div>");
        footer.add(number);

        Button viewAll = new Button("View All");
        viewAll.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("View all " + p.getDescription() + "(s)");
        dialog.setMinWidth("550px");
        
        VerticalLayout dialogLayout = new VerticalLayout();

        if(productCount == 1)
        {
            dialogLayout.add(new Html("<div style='font-size: 16px;'>" + "There is " + productCount  + " " + p.getDescription() + "</div>"));
        }
        else
        {
            dialogLayout.add(new Html("<div style='font-size: 16px;'>" + "There are " + productCount  + " " + p.getDescription() + "s" + "</div>"));
        }

        TextField searchField = new TextField();
        searchField.setWidth("100%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        dialogLayout.add(searchField);

        try
        {
            ArrayList<Product> products = queryProductsWithRef(productReference);

            VerticalLayout vLayout = new VerticalLayout();
            vLayout.add(new Html("<div style='font-size: 16px;'>" + "SERIAL" + "</div>"));

            VerticalLayout grid = new VerticalLayout();
            for(Product prod : products)
            {
                Div hLayout = new Div();
                hLayout.getStyle().set("width", "100%");
                hLayout.getStyle().set("display", "flex");
                hLayout.getStyle().set("justify-content", "space-between");
                hLayout.getStyle().set("align-items", "center");
                Button serial = new Button();

                serial.getElement().setProperty("innerHTML", "<div style='font-size: 16px;'>" + prod.getSerial() + "</div>");
                serial.getElement().getStyle().set("background", "none");
                serial.getElement().getStyle().set("color", "black");

                serial.addClickListener(e -> {
                    Dialog productDialog = new Dialog();
                    productDialog.setHeaderTitle("History of product " + prod.getSerial());

                    productDialog.setMinWidth("700px");

                    try
                    {
                        VerticalLayout historyLayout = new VerticalLayout();
                        List<History> histories = queryHistory(prod.getSerial());

                        Grid<History> historyGrid = new Grid<>(History.class, false);
                        historyGrid.addColumn(History::getMachineRef).setHeader("Machine ref");
                        historyGrid.addColumn(History::getOperationID).setHeader("Operation ID");
                        historyGrid.addColumn(History::getTimeString).setHeader("Time");

                        historyGrid.setItems(histories);

                        historyLayout.add(historyGrid);

                        productDialog.add(historyLayout);
                    
                    }
                    catch(SQLException err)
                    {}

                    Button close = new Button("Close");
                    close.addClickListener(ee -> {
                        productDialog.close();
                    });

                    productDialog.add(close);

                    productDialog.open();

                });

                Button deleteButton = new Button("Delete");
                deleteButton.addClickListener(e -> {
                    try
                    {
                        deleteProductFromStock(prod.getSerial());
                    }
                    catch(SQLException err)
                    {
                        Notification.show("Product was not deleted");
                    }
                });

                hLayout.add(serial);
                hLayout.add(deleteButton);
                grid.add(hLayout);
            }

            vLayout.add(grid);
            dialogLayout.add(vLayout);

        }
        catch(SQLException err)
        {
            err.printStackTrace();
        }

        dialog.add(dialogLayout);

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> {
            dialog.close();
        });

        dialog.add(closeButton);

        viewAll.addClickListener(e -> {
            dialog.open();
        });

        footer.add(viewAll);
        res.add(footer);
        uResult.add(res);

        return uResult;


    }

    public boolean refAlreadyRegistered(String reference, ArrayList<String> productsRef)
    {
        boolean result = false;

        for(int i = 0; i < productsRef.size(); i++)
        {
            if(reference.equals(productsRef.get(i))) result = true;
        }

        return result;
    }

    public int getRefIndex(String reference, ArrayList<String> productsRef)
    {
        int res = -1;

        for(int i = 0; i < productsRef.size(); i++)
        {
            if(reference.equals(productsRef.get(i))) res = i;
        }

        return res;
    }

    private ArrayList<ProductType> queryStock() throws SQLException
    {
        ArrayList<ProductType> result = new ArrayList<>();
        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM STOCK"))
        {
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();
                // search des based on ref :c
                String des = getDesFromRef(res.getString("REF"));
                result.add(new ProductType(res.getString("REF"), des, 0, new ArrayList<>()));
            }
            while(!res.isLast());
        }
        return result;
    }

    private String getDesFromRef(String ref) throws SQLException
    {
        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM PRODUCT WHERE REF = ?"))
        {
            pStatement.setString(1, ref);
            ResultSet queryResults = pStatement.executeQuery();

            queryResults.next();

            return queryResults.getString("DES");
        }
    }

    private ArrayList<Product> queryProductsWithRef(String ref) throws SQLException
    {
        ArrayList<Product> res = new ArrayList<>();

        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM STOCK WHERE REF = ?"))
        {
            pStatement.setString(1, ref);
            ResultSet result = pStatement.executeQuery();

            do
            {
                result.next();
                res.add(new Product(result.getString("SERIAL"), ManipProducts.getProductType(ref, App.manipDB.myConnection)));
            }
            while(!result.isLast());
        }
        return res;
    }

    public void deleteProductFromStock(String serial) throws SQLException
    {
        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("DELETE FROM STOCK WHERE SERIAL = ?"))
        {
            pStatement.setString(1, serial);
            pStatement.executeUpdate();
            getUI().get().getPage().reload();
        }
        catch(SQLException e)
        {
            throw e;
        }
    }

    private ArrayList<History> queryHistory(String serial) throws SQLException
    {
        ArrayList<History> result = new ArrayList<>();

        try(PreparedStatement pStatement = App.manipDB.myConnection.prepareStatement("SELECT * FROM MACHINEWORKING WHERE SERIAL = ?"))
        {
            pStatement.setString(1, serial);
            ResultSet res = pStatement.executeQuery();

            do
            {
                res.next();
                result.add(new History(serial, res.getString("MACHINEREF"), res.getTimestamp("TIME"), res.getInt("IDOPERATION")));
            }
            while(!res.isLast());
        }

        return result;
    }
    
}
