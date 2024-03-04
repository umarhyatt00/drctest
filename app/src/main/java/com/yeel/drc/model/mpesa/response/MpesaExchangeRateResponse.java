package com.yeel.drc.model.mpesa.response;

import com.google.gson.annotations.SerializedName;

public class MpesaExchangeRateResponse {

    @SerializedName("data")
    private MpesaExchangeRateData mpesaExchangeRateData;

    @SerializedName("sessionValid")
    private String sessionValid;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public MpesaExchangeRateData getData() {
        return mpesaExchangeRateData;
    }

    public MpesaExchangeRateData getMpesaExchangeRateData() {
        return mpesaExchangeRateData;
    }

    public void setMpesaExchangeRateData(MpesaExchangeRateData mpesaExchangeRateData) {
        this.mpesaExchangeRateData = mpesaExchangeRateData;
    }

    public String getSessionValid() {
        return sessionValid;
    }

    public void setSessionValid(String sessionValid) {
        this.sessionValid = sessionValid;
    }

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
}