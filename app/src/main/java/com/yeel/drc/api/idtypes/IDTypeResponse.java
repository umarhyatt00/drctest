package com.yeel.drc.api.idtypes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IDTypeResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("id_types") @Expose private List<String> id_types;

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

    public List<String> getId_types() {
        return id_types;
    }

    public void setId_types(List<String> id_types) {
        this.id_types = id_types;
    }
}
