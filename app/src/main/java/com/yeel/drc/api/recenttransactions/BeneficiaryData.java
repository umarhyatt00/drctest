package com.yeel.drc.api.recenttransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BeneficiaryData implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("firstname") @Expose private String firstname;
    @SerializedName("middlename") @Expose private String middleName;
    @SerializedName("lastname") @Expose private String lastname;
    @SerializedName("email") @Expose private String email;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("country_code") @Expose private String country_code;
    @SerializedName("address") @Expose private String address;
    @SerializedName("state") @Expose private String state;
    @SerializedName("city") @Expose private String city;
    @SerializedName("country") @Expose private String country;

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getId() {
        return id;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
