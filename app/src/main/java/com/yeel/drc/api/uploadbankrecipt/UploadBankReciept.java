package com.yeel.drc.api.uploadbankrecipt;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadBankReciept {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("bank_receipt_image") @Expose private String bank_receipt_image;

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

    public String getBank_receipt_image() {
        return bank_receipt_image;
    }

    public void setBank_receipt_image(String bank_receipt_image) {
        this.bank_receipt_image = bank_receipt_image;
    }
}
