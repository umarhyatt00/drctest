package com.yeel.drc.model.cashpickup;

import java.io.Serializable;

public class CashPickupHistoryData implements Serializable {
    String name;
    String cashMode;
//    boolean isSelected;
//    String amount;
//    String date;

    public CashPickupHistoryData() {
    }

    public CashPickupHistoryData(String name, String cashMode) {
        this.name = name;
        this.cashMode = cashMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCashMode() {
        return cashMode;
    }

    public void setCashMode(String cashMode) {
        this.cashMode = cashMode;
    }
}
