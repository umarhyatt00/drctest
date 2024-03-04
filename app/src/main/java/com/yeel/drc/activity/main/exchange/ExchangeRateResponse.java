package com.yeel.drc.activity.main.exchange;

import com.google.gson.annotations.SerializedName;

public class ExchangeRateResponse {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("exchange_rate") private ExchangeRateData exchangeRate;

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

    public ExchangeRateData getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(ExchangeRateData exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
