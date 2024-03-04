package com.yeel.drc.model;

public class TransactionDateTypeModel {
    private String transactionType;
    private String transactionTYpeValue;
    private boolean selected;

    public TransactionDateTypeModel(String transactionType, String transactionTYpeValue, boolean selected) {
        this.transactionType = transactionType;
        this.transactionTYpeValue = transactionTYpeValue;
        this.selected = selected;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionTYpeValue() {
        return transactionTYpeValue;
    }

    public void setTransactionTYpeValue(String transactionTYpeValue) {
        this.transactionTYpeValue = transactionTYpeValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
