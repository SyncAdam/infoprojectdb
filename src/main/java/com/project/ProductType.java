package com.project;

import java.util.ArrayList;

public class ProductType {

    private ArrayList<Operation> operations;
    public String reference;
    private String description;
    private double price;
    public String pathToImage;

    public ProductType(String reference, String description, double price, ArrayList<Operation> alloperations, String pathToImage)
    {
        this.operations = alloperations;
        this.reference = reference;
        this.description = description;
        this.price = price;
        this.pathToImage = pathToImage;
    }

    public ProductType(String reference, String description, double price, ArrayList<Operation> alloperations)
    {
        this.operations = alloperations;
        this.reference = reference;
        this.description = description;
        this.price = price;
        switch(description)
        {
            case "Bolt":
                this.pathToImage = "images/bolt.png";
                break;
            case "Sprocket":
                this.pathToImage = "images/sprocket.png";
                break;
            case "Chisel":
                this.pathToImage = "images/chisel.png";
                break;
            default:
                this.pathToImage = "images/empty-plant.png";
                break;
        }
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