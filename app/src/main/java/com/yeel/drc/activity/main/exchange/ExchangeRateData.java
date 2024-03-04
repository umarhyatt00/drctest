package com.yeel.drc.activity.main.exchange;

import com.google.gson.annotations.SerializedName;

public class ExchangeRateData {
    @SerializedName("conversion_rate") private String conversionRate;
    @SerializedName("conversion_date") private String conversionDate;

    public String getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(String conversionDate) {
        this.conversionDate = conversionDate;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }
}
