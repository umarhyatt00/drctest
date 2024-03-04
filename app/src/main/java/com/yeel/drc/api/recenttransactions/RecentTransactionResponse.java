package com.yeel.drc.api.recenttransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentTransactionResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private RecentTransactionsResult result;

    public RecentTransactionsResult getResult() {
        return result;
    }

    public void setResult(RecentTransactionsResult result) {
        this.result = result;
    }

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
}
