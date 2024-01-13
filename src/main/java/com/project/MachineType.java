package com.project;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MachineType {

    public enum mType
    {
        CNCMILL,
        ASSEMBLER,
        LATHE,
        PRESS,
        BANDSAW,
        OVEN,
        CHEMICALETCHER        
    };

    @JsonProperty("name")
    String name;

    @JsonProperty("model")
    String model;

    @JsonProperty("manufacturer")
    String manufacturer;

    @JsonProperty("pathtoimage")
    String imagepath;

    @JsonProperty("description")
    String description;

    mType type;

    public MachineType(String name, String model, String manufacturer, String imagePath, mType type)
    {
        this.name = name;
        this.model = model;
        this.manufacturer = manufacturer;
        this.type = type;
        this.imagepath = imagePath;
    }

    public MachineType()
    {
    }

    public String getName()
    {
        return this.name;
    }

    public String getModel()
    {
        return this.model;
    }

    public String getManufacturer()
    {
        return this.manufacturer;
    }

    public mType getType()
    {
        return this.type;
    }

    public String getPathToImage()
    {
        return this.imagepath;
    }

    public String getDescription()
    {
        return this.description;
    }
    
}