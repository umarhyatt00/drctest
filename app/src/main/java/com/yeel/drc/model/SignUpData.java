package com.yeel.drc.model;

import java.io.Serializable;

public class SignUpData implements Serializable {

    String mobileNumber;
    String countryCode;
    String accountType;
    String firstName;
    String middleName;
    String lastName;
    String dob;
    String emailAddress;
    String provinces;
    String district;
    String locality;
    String businessName;
    String authorizedPersonOne;
    String authorizedPersonTwo;
    String taxId;
    String businessAddressLineOne;
    String businessAddressLineTwo;
    String idUrl;
    String selfieUrl;
    String signatureUrl;
    String issuingCountry;
    String idType;
    String idNumber;
    String idExpiryDate;
    String pin;
    String userId;
    String companyRegistrationNumber;
    String currencyId;
    String kycId;
    private String preApprovedLimit;

    String accountCreationType;

    public String getAccountCreationType() {
        return accountCreationType;
    }

    public void setAccountCreationType(String accountCreationType) {
        this.accountCreationType = accountCreationType;
    }

    public String getPreApprovedLimit() {
        return preApprovedLimit;
    }

    public void setPreApprovedLimit(String preApprovedLimit) {
        this.preApprovedLimit = preApprovedLimit;
    }

    public String getKycId() {
        return kycId;
    }

    public void setKycId(String kycId) {
        this.kycId = kycId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getIssuingCountry() {
        return issuingCountry;
    }

    public void setIssuingCountry(String issuingCountry) {
        this.issuingCountry = issuingCountry;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdExpiryDate() {
        return idExpiryDate;
    }

    public void setIdExpiryDate(String idExpiryDate) {
        this.idExpiryDate = idExpiryDate;
    }

    public String getIdUrl() {
        return idUrl;
    }

    public void setIdUrl(String idUrl) {
        this.idUrl = idUrl;
    }

    public String getSelfieUrl() {
        return selfieUrl;
    }

    public void setSelfieUrl(String selfieUrl) {
        this.selfieUrl = selfieUrl;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getBusinessAddressLineOne() {
        return businessAddressLineOne;
    }

    public void setBusinessAddressLineOne(String businessAddressLineOne) {
        this.businessAddressLineOne = businessAddressLineOne;
    }

    public String getBusinessAddressLineTwo() {
        return businessAddressLineTwo;
    }

    public void setBusinessAddressLineTwo(String businessAddressLineTwo) {
        this.businessAddressLineTwo = businessAddressLineTwo;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAuthorizedPersonOne() {
        return authorizedPersonOne;
    }

    public void setAuthorizedPersonOne(String authorizedPersonOne) {
        this.authorizedPersonOne = authorizedPersonOne;
    }

    public String getAuthorizedPersonTwo() {
        return authorizedPersonTwo;
    }

    public void setAuthorizedPersonTwo(String authorizedPersonTwo) {
        this.authorizedPersonTwo = authorizedPersonTwo;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getProvinces() {
        return provinces;
    }

    public void setProvinces(String provinces) {
        this.provinces = provinces;
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
}
