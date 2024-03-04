package com.yeel.drc.api.notaddedcurrencylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;

import java.util.List;

public class NotAddedCurrencyResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("results") @Expose private List<CurrencyListData> currencyList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }
}
