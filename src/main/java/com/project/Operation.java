package com.project;

public class Operation {
    
    public int idtype;
    public String refproduct;
    public int nepoch;
    public int id;

    Operation(int id, int idtype, String ref, int nepochs)
    {
        this.id = id;
        this.idtype = idtype;
        this.refproduct = ref;
        this.nepoch = nepochs;
    }

}
