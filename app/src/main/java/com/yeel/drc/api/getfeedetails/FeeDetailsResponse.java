package com.yeel.drc.api.getfeedetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeeDetailsResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("commission") @Expose private FeeDetailsData commission;

    public FeeDetailsData getCommission() {
        return commission;
    }

    public void setCommission(FeeDetailsData commission) {
        this.commission = commission;
    }

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
}
