package com.yeel.drc.model;

public class MonthModelModel {
    private String monthName;
    private boolean selected;
    private String value;

    public MonthModelModel(String monthName, boolean selected, String value) {
        this.monthName = monthName;
        this.selected = selected;
        this.value = value;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
