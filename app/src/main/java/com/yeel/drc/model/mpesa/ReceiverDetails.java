package com.yeel.drc.model.mpesa;

import java.io.Serializable;

public class ReceiverDetails implements Serializable{
    String name;
    String mobile;



    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
