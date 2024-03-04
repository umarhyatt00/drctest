package com.yeel.drc.api.quicklinklist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuickLinkListResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private QuickLinkListResult result;

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

    public QuickLinkListResult getResult() {
        return result;
    }

    public void setResult(QuickLinkListResult result) {
        this.result = result;
    }
}
