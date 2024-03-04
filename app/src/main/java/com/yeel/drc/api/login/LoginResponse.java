package com.yeel.drc.api.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    @SerializedName("access_token") @Expose private String access_token;
    @SerializedName("user_details") @Expose private LoginUserDetails user_details;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public LoginUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(LoginUserDetails user_details) {
        this.user_details = user_details;
    }
}
