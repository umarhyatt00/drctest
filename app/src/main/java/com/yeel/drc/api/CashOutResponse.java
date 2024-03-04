package com.yeel.drc.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashOutResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("ydb_ref_id") @Expose private String ydb_ref_id;

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

    public String getYdb_ref_id() {
        return ydb_ref_id;
    }

    public void setYdb_ref_id(String ydb_ref_id) {
        this.ydb_ref_id = ydb_ref_id;
    }
}
