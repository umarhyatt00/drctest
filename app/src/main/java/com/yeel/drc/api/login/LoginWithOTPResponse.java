package com.yeel.drc.api.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginWithOTPResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("error_type") @Expose private String error_type;
    @SerializedName("retry_attempts") @Expose private String retry_attempts;
    @SerializedName("data") @Expose private LoginResponse data;

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

    public String getRetry_attempts() {
        return retry_attempts;
    }

    public void setRetry_attempts(String retry_attempts) {
        this.retry_attempts = retry_attempts;
    }

    public LoginResponse getData() {
        return data;
    }

    public void setData(LoginResponse data) {
        this.data = data;
    }
}

