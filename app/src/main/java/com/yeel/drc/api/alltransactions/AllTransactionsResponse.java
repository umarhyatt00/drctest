package com.yeel.drc.api.alltransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.recenttransactions.TransactionsData;

import java.util.List;

public class AllTransactionsResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private List<TransactionsData> list;

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

    public List<TransactionsData> getList() {
        return list;
    }

    public void setList(List<TransactionsData> list) {
        this.list = list;
    }
}
