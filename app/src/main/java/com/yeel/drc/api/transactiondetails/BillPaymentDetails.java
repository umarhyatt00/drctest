package com.yeel.drc.api.transactiondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BillPaymentDetails implements Serializable {
    @SerializedName("provider_customer_name") @Expose private String name;
    @SerializedName("provider_customer_mobile") @Expose private String mobile;
    @SerializedName("provider_customer_country_code") @Expose private String countyCode;
    @SerializedName("provider_customer_id") @Expose private String id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
