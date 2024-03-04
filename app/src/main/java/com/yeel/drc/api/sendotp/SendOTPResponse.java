package com.yeel.drc.api.sendotp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendOTPResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("error_type") @Expose private String error_type;
    @SerializedName("otp_id") @Expose private String otp_id;
    @SerializedName("otp") @Expose private String otp;
    @SerializedName("retry_attempts") @Expose private String retry_attempts;

    public String getError_type() {
        return error_type;
    }

    public void setError_type(String error_type) {
        this.error_type = error_type;
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

    public String getOtp_id() {
        return otp_id;
    }

    public void setOtp_id(String otp_id) {
        this.otp_id = otp_id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRetry_attempts() {
        return retry_attempts;
    }

    public void setRetry_attempts(String retry_attempts) {
        this.retry_attempts = retry_attempts;
    }
}
