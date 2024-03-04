package com.yeel.drc.model;

import java.io.Serializable;

public class BeneficiaryCommonData implements Serializable {

    String firstName;
    String middleName;
    String lastName;
    String mobileNumber;
    String mobileCountryCode;
    String countyFlag;
    String emailAddress;
    String countryName;
    String beneficiaryId;
    String beneficiaryIdImage;

    public String getBeneficiaryIdImage() {
        return beneficiaryIdImage;
    }

    public void setBeneficiaryIdImage(String beneficiaryIdImage) {
        this.beneficiaryIdImage = beneficiaryIdImage;
    }

    public String getCountyFlag() {
        return countyFlag;
    }

    public void setCountyFlag(String countyFlag) {
        this.countyFlag = countyFlag;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(String mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
