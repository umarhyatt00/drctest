package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinMaxValues {
    @SerializedName("SSP") @Expose private MinMaxData SSP;
    @SerializedName("USD") @Expose private MinMaxData USD;
    @SerializedName("Airtel_Mpesa_limits") @Expose private MobilePayLimits mobilePayLimits;

    public MobilePayLimits getMobilePayLimits() {
        return mobilePayLimits;
    }

    public void setMobilePayLimits(MobilePayLimits mobilePayLimits) {
        this.mobilePayLimits = mobilePayLimits;
    }

    public MinMaxData getSSP() {
        return SSP;
    }

    public void setSSP(MinMaxData SSP) {
        this.SSP = SSP;
    }

    public MinMaxData getUSD() {
        return USD;
    }

    public void setUSD(MinMaxData USD) {
        this.USD = USD;
    }
}
