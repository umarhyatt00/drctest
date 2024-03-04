package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MobilePayLimits {
    @SerializedName("AIRTEL_DAILY_LIMIT_UGX") @Expose private String AIRTEL_DAILY_LIMIT_UGX;
    @SerializedName("MPESA_DAILY_LIMIT_KES") @Expose private String MPESA_DAILY_LIMIT_KES;


    public String getAIRTEL_DAILY_LIMIT_UGX() {
        return AIRTEL_DAILY_LIMIT_UGX;
    }

    public void setAIRTEL_DAILY_LIMIT_UGX(String AIRTEL_DAILY_LIMIT_UGX) {
        this.AIRTEL_DAILY_LIMIT_UGX = AIRTEL_DAILY_LIMIT_UGX;
    }

    public String getMPESA_DAILY_LIMIT_KES() {
        return MPESA_DAILY_LIMIT_KES;
    }

    public void setMPESA_DAILY_LIMIT_KES(String MPESA_DAILY_LIMIT_KES) {
        this.MPESA_DAILY_LIMIT_KES = MPESA_DAILY_LIMIT_KES;
    }
}
