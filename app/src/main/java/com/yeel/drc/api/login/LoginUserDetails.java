package com.yeel.drc.api.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginUserDetails implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("account_no") @Expose private String account_no;
    @SerializedName("firstname") @Expose private String firstname;
    @SerializedName("middlename") @Expose private String middlename;
    @SerializedName("lastname") @Expose private String lastname;
    @SerializedName("email") @Expose private String email;
    @SerializedName("country_code") @Expose private String countryCode;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("addressline1") @Expose private String addressline1;
    @SerializedName("addressline2") @Expose private String addressline2;
    @SerializedName("zipcode") @Expose private String zipcode;
    @SerializedName("qrImage") @Expose private String qrImage;
    @SerializedName("qr_image_storage") @Expose private String qr_image_storage;
    @SerializedName("qrCode") @Expose private String qrCode;
    @SerializedName("notCount") @Expose private String notCount;
    @SerializedName("profile_image_storage") @Expose private String profile_image_storage;
    @SerializedName("profile_image") @Expose private String profile_image;
    @SerializedName("user_type") @Expose private String user_type;
    @SerializedName("agent_type") @Expose private String agent_type;
    @SerializedName("locality") @Expose private String locality;
    @SerializedName("district") @Expose private String district;
    @SerializedName("province") @Expose private String province;
    @SerializedName("business_name") @Expose private String businessName;
    @SerializedName("authorized_person1") @Expose private String authorized_person1;
    @SerializedName("authorized_person2") @Expose private String authorized_person2;
    @SerializedName("tax_id") @Expose private String tax_id;
    @SerializedName("balance") @Expose private String balance;
    @SerializedName("account_status") @Expose private String account_status;
    @SerializedName("currency_id") @Expose private String currency_id;
    @SerializedName("preapproved") @Expose private String preapproved;
    @SerializedName("total_currencies") @Expose private String total_currencies;
    @SerializedName("preapproved_limit") @Expose private String preapproved_limit;
    @SerializedName("preapproved_limit_SSP") @Expose private String preapproved_limit_SSP;
    @SerializedName("preapproved_limit_USD") @Expose private String preapproved_limit_USD;

    public String getPreapproved_limit_SSP() {
        return preapproved_limit_SSP;
    }

    public void setPreapproved_limit_SSP(String preapproved_limit_SSP) {
        this.preapproved_limit_SSP = preapproved_limit_SSP;
    }

    public String getPreapproved_limit_USD() {
        return preapproved_limit_USD;
    }

    public void setPreapproved_limit_USD(String preapproved_limit_USD) {
        this.preapproved_limit_USD = preapproved_limit_USD;
    }

    public String getTotal_currencies() {
        return total_currencies;
    }

    public void setTotal_currencies(String total_currencies) {
        this.total_currencies = total_currencies;
    }

    public String getPreapproved_limit() {
        return preapproved_limit;
    }

    public void setPreapproved_limit(String preapproved_limit) {
        this.preapproved_limit = preapproved_limit;
    }

    public String getPreapproved() {
        return preapproved;
    }

    public void setPreapproved(String preapproved) {
        this.preapproved = preapproved;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getAuthorized_person1() {
        return authorized_person1;
    }

    public void setAuthorized_person1(String authorized_person1) {
        this.authorized_person1 = authorized_person1;
    }

    public String getAuthorized_person2() {
        return authorized_person2;
    }

    public void setAuthorized_person2(String authorized_person2) {
        this.authorized_person2 = authorized_person2;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getAgent_type() {
        return agent_type;
    }

    public void setAgent_type(String agent_type) {
        this.agent_type = agent_type;
    }

    public String getId() {
        return id;
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

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddressline1() {
        return addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getQrImage() {
        return qrImage;
    }

    public void setQrImage(String qrImage) {
        this.qrImage = qrImage;
    }

    public String getQr_image_storage() {
        return qr_image_storage;
    }

    public void setQr_image_storage(String qr_image_storage) {
        this.qr_image_storage = qr_image_storage;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getNotCount() {
        return notCount;
    }

    public void setNotCount(String notCount) {
        this.notCount = notCount;
    }

    public String getProfile_image_storage() {
        return profile_image_storage;
    }

    public void setProfile_image_storage(String profile_image_storage) {
        this.profile_image_storage = profile_image_storage;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

}
