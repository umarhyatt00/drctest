package com.yeel.drc.api.getfeedetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeeDetailsData {
    @SerializedName("tier_name") @Expose private String tierName;
    @SerializedName("percentage_rate") @Expose private String percentageRate;
    @SerializedName("flat_rate") @Expose private String flatRate;

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getPercentageRate() {
        return percentageRate;
    }

    public void setPercentageRate(String percentageRate) {
        this.percentageRate = percentageRate;
    }

    public String getFlatRate() {
        return flatRate;
    }

    public void setFlatRate(String flatRate) {
        this.flatRate = flatRate;
    }
}
