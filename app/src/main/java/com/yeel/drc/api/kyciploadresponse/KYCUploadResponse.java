package com.yeel.drc.api.kyciploadresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.login.LoginUserDetails;

public class KYCUploadResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("kyc_status") @Expose private String kyc_status;
    @SerializedName("yeel_kyc_id") @Expose private String yeel_kyc_id;
    @SerializedName("selfie_image") @Expose private String selfie_image;
    @SerializedName("id_image") @Expose private String id_image;
    @SerializedName("signature_image") @Expose private String signature_image;
    @SerializedName("user_details") @Expose private LoginUserDetails user_details;

    public LoginUserDetails getUser_details() {
        return user_details;
    }

    public void setUser_details(LoginUserDetails user_details) {
        this.user_details = user_details;
    }

    public String getKyc_status() {
        return kyc_status;
    }

    public void setKyc_status(String kyc_status) {
        this.kyc_status = kyc_status;
    }

    public String getYeel_kyc_id() {
        return yeel_kyc_id;
    }

    public void setYeel_kyc_id(String yeel_kyc_id) {
        this.yeel_kyc_id = yeel_kyc_id;
    }

    public String getSelfie_image() {
        return selfie_image;
    }

    public void setSelfie_image(String selfie_image) {
        this.selfie_image = selfie_image;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public String getSignature_image() {
        return signature_image;
    }

    public void setSignature_image(String signature_image) {
        this.signature_image = signature_image;
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
