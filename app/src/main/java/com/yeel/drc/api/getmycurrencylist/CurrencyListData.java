package com.yeel.drc.api.getmycurrencylist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurrencyListData implements Serializable {
    private Integer selectedStatus = 0;
    @SerializedName("currency_id") @Expose private String currency_id;
    @SerializedName("accountNumber") @Expose private String account_number;
    @SerializedName("default_currency") @Expose private String default_currency;
    @SerializedName("status") @Expose private String status;
    @SerializedName("currency_code") @Expose private String currency_code;
    @SerializedName("currency_name") @Expose private String currency_name;
    @SerializedName("currency_image") @Expose private String currency_image;



    public Integer getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(Integer selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getDefault_currency() {
        return default_currency;
    }

    public void setDefault_currency(String default_currency) {
        this.default_currency = default_currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public String getCurrency_image() {
        return currency_image;
    }

    public void setCurrency_image(String currency_image) {
        this.currency_image = currency_image;
    }


    public CurrencyListData(Integer selectedStatus, String currency_id, String account_number, String default_currency, String status, String currency_code, String currency_name, String currency_image) {
        this.selectedStatus = selectedStatus;
        this.currency_id = currency_id;
        this.account_number = account_number;
        this.default_currency = default_currency;
        this.status = status;
        this.currency_code = currency_code;
        this.currency_name = currency_name;
    }
}
