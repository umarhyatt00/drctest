package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KYCDetailsData implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("issuing_country") @Expose private String issuing_country;
    @SerializedName("issuing_country_flag") @Expose private String issuing_country_flag;
    @SerializedName("id_type") @Expose private String id_type;
    @SerializedName("id_number") @Expose private String id_number;
    @SerializedName("id_image") @Expose private String id_image;
    @SerializedName("selfie_img") @Expose private String selfie_img;
    @SerializedName("signature_img") @Expose private String signature_img;
    @SerializedName("yeel_kyc_id") @Expose private String yeel_kyc_id;
    @SerializedName("kyc_status") @Expose private String kyc_status;
    @SerializedName("expiryDate") @Expose private String expiryDate;

    public String getIssuing_country_flag() {
        return issuing_country_flag;
    }

    public void setIssuing_country_flag(String issuing_country_flag) {
        this.issuing_country_flag = issuing_country_flag;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssuing_country() {
        return issuing_country;
    }

    public void setIssuing_country(String issuing_country) {
        this.issuing_country = issuing_country;
    }

    public String getId_type() {
        return id_type;
    }

    public void setId_type(String id_type) {
        this.id_type = id_type;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    public String getSelfie_img() {
        return selfie_img;
    }

    public void setSelfie_img(String selfie_img) {
        this.selfie_img = selfie_img;
    }

    public String getSignature_img() {
        return signature_img;
    }

    public void setSignature_img(String signature_img) {
        this.signature_img = signature_img;
    }

    public String getYeel_kyc_id() {
        return yeel_kyc_id;
    }

    public void setYeel_kyc_id(String yeel_kyc_id) {
        this.yeel_kyc_id = yeel_kyc_id;
    }

    public String getKyc_status() {
        return kyc_status;
    }

    public void setKyc_status(String kyc_status) {
        this.kyc_status = kyc_status;
    }
}
