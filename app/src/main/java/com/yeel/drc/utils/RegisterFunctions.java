package com.yeel.drc.utils;

import android.content.Context;

import com.yeel.drc.model.SignUpData;

public class RegisterFunctions {

    Context context;
    SharedPrefUtil sharedPrefUtil;

    public RegisterFunctions(Context context) {
        this.context = context;
        sharedPrefUtil = new SharedPrefUtil(context);
    }


    public void clearSignUpProgress() {
       /* sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_COUNTRY_CODE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_MOBILE_NUMBER", "");*/
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ACCOUNT_TYPE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_FIRST_NAME","");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_MIDDLE_NAME", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_LAST_NAME","");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_DOB", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_EMAIL_ADDRESS", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_PROVINCES", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_DISTRICT", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_LOCALITY", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_NAME", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_ONE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_TWO", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_TAX_ID", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_COMPANY_REGISTRATION_NUMBER", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_ONE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_TWO", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_URL", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_SELFIE_URL", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_SIGNATURE_URL", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ISSUING_COUNTRY", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_TYPE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_NUMBER", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_EXPIRY_DATE", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_PIN", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_USER_ID", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_CURRENCY", "");
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_KYC_ID", "");
    }


    public void saveRegisterDetails(SignUpData signUpData) {
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_COUNTRY_CODE", signUpData.getCountryCode());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_MOBILE_NUMBER", signUpData.getMobileNumber());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ACCOUNT_TYPE", signUpData.getAccountType());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_FIRST_NAME", signUpData.getFirstName());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_MIDDLE_NAME", signUpData.getMiddleName());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_LAST_NAME", signUpData.getLastName());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_DOB", signUpData.getDob());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_EMAIL_ADDRESS", signUpData.getEmailAddress());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_PROVINCES", signUpData.getProvinces());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_DISTRICT", signUpData.getDistrict());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_LOCALITY", signUpData.getLocality());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_NAME", signUpData.getBusinessName());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_ONE", signUpData.getAuthorizedPersonOne());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_TWO", signUpData.getAuthorizedPersonTwo());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_TAX_ID", signUpData.getTaxId());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_COMPANY_REGISTRATION_NUMBER", signUpData.getCompanyRegistrationNumber());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_ONE", signUpData.getBusinessAddressLineOne());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_TWO", signUpData.getBusinessAddressLineTwo());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_URL", signUpData.getIdUrl());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_SELFIE_URL", signUpData.getSelfieUrl());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_SIGNATURE_URL", signUpData.getSignatureUrl());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ISSUING_COUNTRY", signUpData.getIssuingCountry());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_TYPE", signUpData.getIdType());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_NUMBER", signUpData.getIdNumber());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_ID_EXPIRY_DATE", signUpData.getIdExpiryDate());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_PIN", signUpData.getPin());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_USER_ID", signUpData.getUserId());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_CURRENCY", signUpData.getCurrencyId());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_KYC_ID", signUpData.getKycId());
        sharedPrefUtil.setEncryptedStringPreference("SIGN_UP_PRE_APPROVED_LIMIT", signUpData.getPreApprovedLimit());
    }

    public String getSignUpPreApprovedLimitID() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_PRE_APPROVED_LIMIT", "");
    }

    public String getKycID() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_KYC_ID", "");
    }

    public String getCurrencyID() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_CURRENCY", "");
    }

    public String getCompanyRegistrationNumber() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_COMPANY_REGISTRATION_NUMBER", "");
    }

    public String getUserId() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_USER_ID", "");
    }

    public String getCountryCode() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_COUNTRY_CODE", "");
    }

    public String getMobileNumber() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_MOBILE_NUMBER", "");
    }

    public String getAccountType() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ACCOUNT_TYPE", "");
    }

    public String getFirstName() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_FIRST_NAME", "");
    }

    public String getMiddleName() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_MIDDLE_NAME", "");
    }

    public String getLastName() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_LAST_NAME", "");
    }

    public String getDOB() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_DOB", "");
    }

    public String getEmailAddress() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_EMAIL_ADDRESS", "");
    }

    public String getProvinces() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_PROVINCES", "");
    }

    public String getDistrict() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_DISTRICT", "");
    }

    public String getLocality() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_LOCALITY", "");
    }

    public String getBusinessName() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_BUSINESS_NAME", "");
    }

    public String getAuthorizedPersonOne() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_ONE", "");
    }

    public String getAuthorizedPersonTwo() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_AUTHORIZED_PERSON_TWO", "");
    }

    public String gettaxId() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_TAX_ID", "");
    }

    public String getBusinessAddressLineOne() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_ONE", "");
    }

    public String getBusinessAddressLineTwo() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_BUSINESS_ADDRESS_LINE_TWO", "");
    }

    public String getIdURL() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ID_URL", "");
    }

    public String getSelfieURL() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_SELFIE_URL", "");
    }

    public String getSignatureURL() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_SIGNATURE_URL", "");
    }

    public String getIssuingCountry() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ISSUING_COUNTRY", "");
    }


    public String getIDType() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ID_TYPE", "");
    }

    public String getIDNumber() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ID_NUMBER", "");
    }

    public String getExpiryDate() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_ID_EXPIRY_DATE", "");
    }

    public String getPIN() {
        return sharedPrefUtil.getEncryptedStringPreference("SIGN_UP_PIN", "");
    }


}
