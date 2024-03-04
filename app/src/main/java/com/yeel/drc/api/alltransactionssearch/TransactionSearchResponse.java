package com.yeel.drc.api.alltransactionssearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionSearchResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private TransactionSearchResult result;

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

    public TransactionSearchResult getResult() {
        return result;
    }

    public void setResult(TransactionSearchResult result) {
        this.result = result;
    }
}
