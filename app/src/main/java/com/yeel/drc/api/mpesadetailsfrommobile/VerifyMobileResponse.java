package com.yeel.drc.api.mpesadetailsfrommobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VerifyMobileResponse implements Serializable {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("yeeltid") @Expose private String yeeltid;
    @SerializedName("data") @Expose private VerifyMobileData data;

    public VerifyMobileData getData() {
        return data;
    }

    public void setData(VerifyMobileData data) {
        this.data = data;
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


    public String getYeeltid() {
        return yeeltid;
    }

    public void setYeeltid(String yeeltid) {
        this.yeeltid = yeeltid;
    }
}
