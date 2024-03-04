package com.yeel.drc.api.forceupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForceUpdateResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("result") @Expose private ForceUpdateData data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ForceUpdateData getData() {
        return data;
    }

    public void setData(ForceUpdateData data) {
        this.data = data;
    }
}
