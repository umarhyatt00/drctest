package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BillPaymentOptionsData {
    @SerializedName("id") @Expose private String id;
    @SerializedName("option_name") @Expose private String option_name;
    @SerializedName("sorder") @Expose private String sorder;
    @SerializedName("status") @Expose private String status;
    @SerializedName("parent_option_id") @Expose private String parent_option_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

    public String getSorder() {
        return sorder;
    }

    public void setSorder(String sorder) {
        this.sorder = sorder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParent_option_id() {
        return parent_option_id;
    }

    public void setParent_option_id(String parent_option_id) {
        this.parent_option_id = parent_option_id;
    }
}
