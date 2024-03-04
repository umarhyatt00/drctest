package com.yeel.drc.api.accountsummary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountSummaryResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private List<AccountSummaryData> accountSummaryList;

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

    public List<AccountSummaryData> getAccountSummaryList() {
        return accountSummaryList;
    }

    public void setAccountSummaryList(List<AccountSummaryData> accountSummaryList) {
        this.accountSummaryList = accountSummaryList;
    }
}
