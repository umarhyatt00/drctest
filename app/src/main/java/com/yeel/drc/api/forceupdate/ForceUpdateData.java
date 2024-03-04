package com.yeel.drc.api.forceupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForceUpdateData {
    @SerializedName("version") @Expose private String versionCode;
    @SerializedName("fourceUpdate") @Expose private String forceUpdate;
    @SerializedName("hybrid") @Expose private String hybrid;

    public String getHybrid() {
        return hybrid;
    }

    public void setHybrid(String hybrid) {
        this.hybrid = hybrid;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}

