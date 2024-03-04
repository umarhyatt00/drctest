package com.yeel.drc.api.providersList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProvidersListData implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("provider_name") @Expose private String provider_name;
    @SerializedName("status") @Expose private String status;
    @SerializedName("image") @Expose private String image;
    @SerializedName("service_type") @Expose private String service_type;
    @SerializedName("bill_sample") @Expose private String bill_sample;
    @SerializedName("country_code") @Expose private String countryCode;
    @SerializedName("mobile") @Expose private String phone;
    @SerializedName("accountNumber") @Expose private String accountNumber;
    @SerializedName("receiver_id") @Expose private String receiver_id;
    @SerializedName("purpose_id") @Expose private String purpose_id;
    @SerializedName("is_external") @Expose private String is_external;
    @SerializedName("multi_channel") @Expose private String multi_channel;
    @SerializedName("currency_id") @Expose private String currency_id;

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getBill_sample() {
        return bill_sample;
    }

    public void setBill_sample(String bill_sample) {
        this.bill_sample = bill_sample;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getPurpose_id() {
        return purpose_id;
    }

    public void setPurpose_id(String purpose_id) {
        this.purpose_id = purpose_id;
    }

    public String getIs_external() {
        return is_external;
    }

    public void setIs_external(String is_external) {
        this.is_external = is_external;
    }

    public String getMulti_channel() {
        return multi_channel;
    }

    public void setMulti_channel(String multi_channel) {
        this.multi_channel = multi_channel;
    }
}
