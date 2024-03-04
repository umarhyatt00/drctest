package com.yeel.drc.api.uploadbenificiaryid;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadIdResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("id_image_name") @Expose private String id_image_name;

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

    public String getId_image_name() {
        return id_image_name;
    }

    public void setId_image_name(String id_image_name) {
        this.id_image_name = id_image_name;
    }
}
