package com.yeel.drc.api.userdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetailsData implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("firstname") @Expose private String firstname;
    @SerializedName("middlename") @Expose private String middlename;
    @SerializedName("lastname") @Expose private String lastname;
    @SerializedName("email") @Expose private String email;
    @SerializedName("country_code") @Expose private String country_code;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("qrCode") @Expose private String qrCode;
    @SerializedName("profile_image") @Expose private String profileImage;
    @SerializedName("user_type") @Expose private String user_type;
    @SerializedName("business_name") @Expose private String business_name;
    @SerializedName("agent_type") @Expose private String agentType;
    @SerializedName("account_no") @Expose private String account_no;
    @SerializedName("district") @Expose private String district;
    @SerializedName("province") @Expose private String province;
    @SerializedName("locality") @Expose private String locality;
    @SerializedName("addressline1") @Expose private String addressline1;
    @SerializedName("addressline2") @Expose private String addressline2;
    String amount;
    String receiverName;
    String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserDetailsData(String receiverName, String profileImage, String receiverAccountNumber, String receiverId, String user_type) {
        this.receiverName=receiverName;
        this.profileImage=profileImage;
        this.account_no=receiverAccountNumber;
        this.id=receiverId;
        this.user_type=user_type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
