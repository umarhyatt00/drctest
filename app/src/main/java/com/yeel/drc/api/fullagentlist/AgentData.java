package com.yeel.drc.api.fullagentlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;

import java.io.Serializable;
import java.util.List;

public class AgentData implements Serializable {
    String nameToShow;
    @SerializedName("id") @Expose private String id;
    @SerializedName("account_no") @Expose private String account_no;
   // @SerializedName("agent_name") @Expose private String agentName;
    @SerializedName("agent_type") @Expose private String agent_type;
    @SerializedName("email") @Expose private String email;
    @SerializedName("country_code") @Expose private String country_code;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("addressline1") @Expose private String addressline1;
    @SerializedName("addressline2") @Expose private String addressline2;
    @SerializedName("locality") @Expose private String locality;
    @SerializedName("district") @Expose private String district;
    @SerializedName("province") @Expose private String province;
    @SerializedName("agent_code") @Expose private String agent_code;
    @SerializedName("profile_image") @Expose private String profile_image;

    @SerializedName("firstname") @Expose private String firstname;
    @SerializedName("middlename") @Expose private String middlename;
    @SerializedName("lastname") @Expose private String lastname;
    @SerializedName("business_name") @Expose private String business_name;
    @SerializedName("user_curreny_detail") @Expose private List<CurrencyListData> currencyList;

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getNameToShow() {
        return nameToShow;
    }

    public void setNameToShow(String nameToShow) {
        this.nameToShow = nameToShow;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
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

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getAgent_code() {
        return agent_code;
    }

    public void setAgent_code(String agent_code) {
        this.agent_code = agent_code;
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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
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

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
