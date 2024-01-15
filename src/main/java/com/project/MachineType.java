package com.project;

import java.util.ArrayList;

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
        CHEMICALETCHER,
        GRINDINGWHEEL,
        NONE     
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

    @JsonProperty("power")
    double power;

    mType type;

    public MachineType(String name, String model, String manufacturer, String imagePath, mType type, double power)
    {
        this.name = name;
        this.model = model;
        this.manufacturer = manufacturer;
        this.type = type;
        this.imagepath = imagePath;
        this.power = power;
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

    public String getTypeString()
    {
        switch(this.type)
        {
            case CNCMILL:
                return "CNCMILL";
            case ASSEMBLER:
                return "ASSEMBLER";
            case LATHE:
                return "LATHE";
            case PRESS:
                return "PRESS";
            case BANDSAW:
                return "BANDSAW";
            case OVEN:
                return "OVEN";
            case CHEMICALETCHER:
                return "CHEMICALETCHER";
            case GRINDINGWHEEL: 
                return "GRINDINGWHEEL";
            default:
                return "";
        }
    }

    public static mType getTypeFromString(String type)
    {
        switch(type)
        {
            case "CNCMILL":
                return MachineType.mType.CNCMILL;
            case "ASSEMBLER":
                return MachineType.mType.ASSEMBLER;
            case "LATHE":
                return MachineType.mType.LATHE;
            case "PRESS":
                return MachineType.mType.PRESS;
            case "BANDSAW":
                return MachineType.mType.BANDSAW;
            case "OVEN":
                return MachineType.mType.OVEN;
            case "CHEMICALETCHER":
                return MachineType.mType.CHEMICALETCHER;
            case "GRINDINGWHEEL":
                return MachineType.mType.GRINDINGWHEEL;
            default:
                return MachineType.mType.NONE;
        }
    }

    public String getPathToImage()
    {
        return this.imagepath;
    }

    public String getDescription()
    {
        return this.description;
    }

    public double getPower()
    {
        return this.power;
    }

    public static ArrayList<Integer> getOperationIDs(mType type)
    {
        ArrayList<Integer> result = new ArrayList<>();

        switch(type)
        {
            case ASSEMBLER:
                result.add(15);
                result.add(12);
                break;
            case BANDSAW:
                result.add(9);
                break;
            case CHEMICALETCHER:
                result.add(8);
                break;
            case CNCMILL:
                result.add(2);
                result.add(5);
                result.add(6);
                result.add(14);
                break;
            case LATHE:
                result.add(2);
                result.add(3);
                result.add(5);
                result.add(6);
                break;
            case NONE:
                break;
            case OVEN:
                result.add(11);
                break;
            case PRESS:
                result.add(10);
                break;
            case GRINDINGWHEEL:
                result.add(4);
                result.add(7);
            default:
                break;
        }

        return result;
    }
    
}