package com.yeel.drc.api.banklist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BankListResponse implements Serializable {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private List<BankListData> provinces;

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

    public List<BankListData> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<BankListData> provinces) {
        this.provinces = provinces;
    }
}
