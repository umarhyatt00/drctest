package com.yeel.drc.api.signup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpResponseData {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("user_id") @Expose private String user_id;
    @SerializedName("preapproved_limit") @Expose private String preApprovedLimit;
    @SerializedName("preapproved_limit_SSP") @Expose private String preapproved_limit_SSP;
    @SerializedName("preapproved_limit_USD") @Expose private String preapproved_limit_USD;


    public String getPreapproved_limit_SSP() {
        return preapproved_limit_SSP;
    }

    public void setPreapproved_limit_SSP(String preapproved_limit_SSP) {
        this.preapproved_limit_SSP = preapproved_limit_SSP;
    }

    public String getPreapproved_limit_USD() {
        return preapproved_limit_USD;
    }

    public void setPreapproved_limit_USD(String preapproved_limit_USD) {
        this.preapproved_limit_USD = preapproved_limit_USD;
    }

    public String getPreApprovedLimit() {
        return preApprovedLimit;
    }

    public void setPreApprovedLimit(String preApprovedLimit) {
        this.preApprovedLimit = preApprovedLimit;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
