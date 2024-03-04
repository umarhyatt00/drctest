package com.yeel.drc.api.providersList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProvidersListResponse {
    @SerializedName("status") @Expose
    private String status;
    @SerializedName("results") @Expose private List<ProvidersListData> currencyList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProvidersListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<ProvidersListData> currencyList) {
        this.currencyList = currencyList;
    }
}
