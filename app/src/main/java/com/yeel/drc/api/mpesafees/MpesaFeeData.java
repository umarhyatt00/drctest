package com.yeel.drc.api.mpesafees;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MpesaFeeData {
    @SerializedName("type") @Expose private String type;
    @SerializedName("fees") @Expose private String fees;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }
}
