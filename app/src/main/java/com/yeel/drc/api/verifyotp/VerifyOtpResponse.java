package com.yeel.drc.api.verifyotp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyOtpResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("retry_attempts") @Expose private String retry_attempts;

    public String getRetry_attempts() {
        return retry_attempts;
    }

    public void setRetry_attempts(String retry_attempts) {
        this.retry_attempts = retry_attempts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
