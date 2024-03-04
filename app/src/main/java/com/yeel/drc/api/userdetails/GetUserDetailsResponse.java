package com.yeel.drc.api.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;

import java.io.Serializable;
import java.util.List;

public class GetUserDetailsResponse implements Serializable {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("user_curreny_detail") @Expose private List<CurrencyListData> currencyList;
    @SerializedName("result") @Expose private UserDetailsData userDetails;

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }

    public UserDetailsData getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsData userDetails) {
        this.userDetails = userDetails;
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
