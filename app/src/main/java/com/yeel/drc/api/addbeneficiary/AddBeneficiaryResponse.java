package com.yeel.drc.api.addbeneficiary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddBeneficiaryResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("beneficiaryId") @Expose private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
