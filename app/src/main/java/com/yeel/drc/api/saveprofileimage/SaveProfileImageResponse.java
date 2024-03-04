package com.yeel.drc.api.saveprofileimage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveProfileImageResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("saved_profile_image") @Expose private String saved_profile_image;

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

    public String getSaved_profile_image() {
        return saved_profile_image;
    }

    public void setSaved_profile_image(String saved_profile_image) {
        this.saved_profile_image = saved_profile_image;
    }
}
