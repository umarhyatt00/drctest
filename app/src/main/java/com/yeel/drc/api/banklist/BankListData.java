package com.yeel.drc.api.banklist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankListData implements Serializable {
    @SerializedName("bankName") @Expose private String bankName;
    @SerializedName("bankCode") @Expose private String bankCode;
    @SerializedName("status") @Expose private String status;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
