package com.yeel.drc.api.transactiondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetailsResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private TransactionDetailsData transactionDetailsData;

    public TransactionDetailsData getTransactionDetailsData() {
        return transactionDetailsData;
    }

    public void setTransactionDetailsData(TransactionDetailsData transactionDetailsData) {
        this.transactionDetailsData = transactionDetailsData;
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
