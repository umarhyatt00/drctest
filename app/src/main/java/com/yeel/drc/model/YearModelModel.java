package com.yeel.drc.model;

public class YearModelModel {
    private String yearName;
    private boolean selected;

    public YearModelModel(String yearName, boolean selected) {
        this.yearName = yearName;
        this.selected = selected;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
