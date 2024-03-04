package com.yeel.drc.model;

public class LanguageSelectionModel {
    String languageID;
    boolean isSelected;
    String languageName;
    String languageIcon;
    String languageCode;

    public LanguageSelectionModel( String languageID,boolean isSelected, String languageName,String languageIcon, String languageCode) {
        this.languageID = languageID;
        this.isSelected = isSelected;
        this.languageName = languageName;
        this.languageIcon = languageIcon;
        this.languageCode = languageCode;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageIcon() {
        return languageIcon;
    }

    public void setLanguageIcon(String languageIcon) {
        this.languageIcon = languageIcon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
