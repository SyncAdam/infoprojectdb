package com.project;

public class Operation {
    
    public int idtype;
    public String refproduct;
    public int opbef;
    public int opaft;
    public int id;

    Operation(int id, int idtype, String ref, int opbef, int opaft)
    {
        this.id = id;
        this.idtype = idtype;
        this.refproduct = ref;
        this.opbef = opbef;
        this.opaft = opaft;
    }

}
