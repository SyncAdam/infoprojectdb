package com.project;

import java.util.ArrayList;

public class Product {

    private ArrayList<Operation> operationsDone;
    private ArrayList<Operation> operationsNotDone;
    public String reference;
    private String description;
    private double price;

    Product(String reference, String description, double price, ArrayList<Operation> alloperations)
    {
        this.operationsDone = new ArrayList<>();
        this.operationsNotDone = alloperations;
        this.reference = reference;
        this.description = description;
        this.price = price;
    }
    
}
