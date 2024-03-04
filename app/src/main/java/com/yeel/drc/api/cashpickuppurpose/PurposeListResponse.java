package com.yeel.drc.api.cashpickuppurpose;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurposeListResponse {

    @SerializedName("status") @Expose
    private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private List<PurposeListData> purposeList;

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

    public List<PurposeListData> getPurposeList() {
        return purposeList;
    }

    public void setPurposeList(List<PurposeListData> purposeList) {
        this.purposeList = purposeList;
    }
}
