package com.project;

import com.vaadin.flow.component.html.Image;

public class Operation {
    
    public int idtype;
    public int idproduct;
    public int opbef;
    public int opaft;
    Image img = new Image("images/empty-plant.png", "placeholder plant");

    Operation(int idtype, int idproduct, int opbef, int opaft)
    {
        this.idtype = idtype;
        this.idproduct = idproduct;
        this.opbef = opbef;
        this.opaft = opaft;
    }

}
