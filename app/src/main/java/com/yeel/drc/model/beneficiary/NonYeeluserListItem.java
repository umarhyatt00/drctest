package com.yeel.drc.model.beneficiary;

import com.google.gson.annotations.SerializedName;

public class NonYeeluserListItem {

    @SerializedName("country")
    private String country;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("middlename")
    private String middlename;

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("non_yeel_id_image")
    private String beneficiaryIDImage;

    public String getBeneficiaryIDImage() {
        return beneficiaryIDImage;
    }

    public void setBeneficiaryIDImage(String beneficiaryIDImage) {
        this.beneficiaryIDImage = beneficiaryIDImage;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLastname() {
        return lastname;
    }
}