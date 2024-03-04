package com.yeel.drc.api.kycdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.basicdetails.KYCDetailsData;

public class KYCDetailsResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private KYCDetailsData kycDetails;

    public KYCDetailsData getKycDetails() {
        return kycDetails;
    }

    public void setKycDetails(KYCDetailsData kycDetails) {
        this.kycDetails = kycDetails;
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
