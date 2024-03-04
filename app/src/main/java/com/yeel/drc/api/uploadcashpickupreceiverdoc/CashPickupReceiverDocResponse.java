package com.yeel.drc.api.uploadcashpickupreceiverdoc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashPickupReceiverDocResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("collection_date") @Expose private String collection_date;

    public String getCollection_date() {
        return collection_date;
    }

    public void setCollection_date(String collection_date) {
        this.collection_date = collection_date;
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
