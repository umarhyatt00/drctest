package com.yeel.drc.api.cashpickuppurpose;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurposeListData {
    int selectedStatus = 0;
    @SerializedName("id") @Expose private String id;
    @SerializedName("name") @Expose private String name;

    public Integer getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(Integer selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
