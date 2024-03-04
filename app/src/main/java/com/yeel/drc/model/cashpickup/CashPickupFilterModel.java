package com.yeel.drc.model.cashpickup;

import java.io.Serializable;

public class CashPickupFilterModel implements Serializable {
    String name;
//    String cashMode;
    boolean isSelected;
//    String amount;
//    String date;

    public CashPickupFilterModel() {
    }

    public CashPickupFilterModel(String name,boolean isSelected) {
        this.name = name;
//        this.cashMode = cashMode;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
