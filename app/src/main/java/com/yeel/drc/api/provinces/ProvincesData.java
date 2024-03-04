package com.yeel.drc.api.provinces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProvincesData implements Serializable {
    @SerializedName("provincesName") @Expose private String provincesName;
    @SerializedName("district") @Expose private List<String> district;

    public String getProvincesName() {
        return provincesName;
    }

    public void setProvincesName(String provincesName) {
        this.provincesName = provincesName;
    }

    public List<String> getDistrict() {
        return district;
    }

    public void setDistrict(List<String> district) {
        this.district = district;
    }
}
