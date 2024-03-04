package com.yeel.drc.api.mpesapayment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MepsaPaymentResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("data") @Expose private MpesaPaymentResponseData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MpesaPaymentResponseData getData() {
        return data;
    }

    public void setData(MpesaPaymentResponseData data) {
        this.data = data;
    }
}
