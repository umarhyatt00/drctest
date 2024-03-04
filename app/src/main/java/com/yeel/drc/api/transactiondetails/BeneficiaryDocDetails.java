package com.yeel.drc.api.transactiondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeneficiaryDocDetails implements Serializable {
    @SerializedName("signature_image") @Expose private String signature_image;
    @SerializedName("id_image") @Expose private String id_image;

    public String getSignature_image() {
        return signature_image;
    }

    public void setSignature_image(String signature_image) {
        this.signature_image = signature_image;
    }

    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }
}
