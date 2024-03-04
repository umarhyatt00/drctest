package com.yeel.drc.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonResponse {
    @SerializedName("daagini") @Expose private String kuttoosan;

    public String getKuttoosan() {
        return kuttoosan;
    }

    public void setKuttoosan(String kuttoosan) {
        this.kuttoosan = kuttoosan;
    }
}
