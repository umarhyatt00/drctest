package com.yeel.drc.api.mpesafees;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MpesaFeeResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("data") @Expose private MpesaFeeData data;

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

    public MpesaFeeData getData() {
        return data;
    }

    public void setData(MpesaFeeData data) {
        this.data = data;
    }
}
