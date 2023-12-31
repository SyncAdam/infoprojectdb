package com.project;

import java.util.ArrayList;

public class ProductType {

    private ArrayList<Operation> operations;
    public String reference;
    private String description;
    private double price;

    ProductType(String reference, String description, double price, ArrayList<Operation> alloperations)
    {
        this.operations = alloperations;
        this.reference = reference;
        this.description = description;
        this.price = price;
    }

    public ArrayList<Operation> getOperations()
    {
        return this.operations;
    }

    public String getReference()
    {
        return this.reference;
    }

    public String getDescription()
    {
        return this.description;
    }

    public double getPrice()
    {
        return this.price;
    }

}