package com.yeel.drc.api.getmycurrencylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyCurrencyResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("total_currencies") @Expose private String total_currencies;
    @SerializedName("results") @Expose private List<CurrencyListData> currencyList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal_currencies() {
        return total_currencies;
    }

    public void setTotal_currencies(String total_currencies) {
        this.total_currencies = total_currencies;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }
}
