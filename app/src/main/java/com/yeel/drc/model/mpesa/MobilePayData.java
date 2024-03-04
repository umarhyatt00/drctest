package com.yeel.drc.model.mpesa;

import com.yeel.drc.api.mpesadetailsfrommobile.VerifyMobileData;
import com.yeel.drc.model.BeneficiaryCommonData;

import java.io.Serializable;

public class MobilePayData implements Serializable {
    BeneficiaryCommonData senderDetails;
    VerifyMobileData receiverDetails;

    String receiverCountryCodeWithOutPlus;
    String mobilePayCode;
    String mobilePayName;

    String mobileNumber;
    String mobileCountyCode;
    String yeeltid;

    String sendAmount;
    String getAmount;
    String conversionAmount;

    String receiverCurrency;

   String feesAmount;
   String totalAmount;
   String referenceNumber;

    String feesType = "";
    String feesValue = "";

    public String getFeesType() {
        return feesType;
    }

    public void setFeesType(String feesType) {
        this.feesType = feesType;
    }

    public String getFeesValue() {
        return feesValue;
    }

    public void setFeesValue(String feesValue) {
        this.feesValue = feesValue;
    }

    public String getReceiverCountryCodeWithOutPlus() {
        return receiverCountryCodeWithOutPlus;
    }

    public void setReceiverCountryCodeWithOutPlus(String receiverCountryCodeWithOutPlus) {
        this.receiverCountryCodeWithOutPlus = receiverCountryCodeWithOutPlus;
    }

    public String getReceiverCurrency() {
        return receiverCurrency;
    }

    public void setReceiverCurrency(String receiverCurrency) {
        this.receiverCurrency = receiverCurrency;
    }

    public String getMobilePayCode() {
        return mobilePayCode;
    }

    public void setMobilePayCode(String mobilePayCode) {
        this.mobilePayCode = mobilePayCode;
    }

    public String getMobilePayName() {
        return mobilePayName;
    }

    public void setMobilePayName(String mobilePayName) {
        this.mobilePayName = mobilePayName;
    }

    public String getMobileCountyCode() {
        return mobileCountyCode;
    }

    public void setMobileCountyCode(String mobileCountyCode) {
        this.mobileCountyCode = mobileCountyCode;
    }

    public String getYeeltid() {
        return yeeltid;
    }

    public void setYeeltid(String yeeltid) {
        this.yeeltid = yeeltid;
    }

    public String getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(String feesAmount) {
        this.feesAmount = feesAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getConversionAmount() {
        return conversionAmount;
    }

    public void setConversionAmount(String conversionAmount) {
        this.conversionAmount = conversionAmount;
    }

    public String getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(String sendAmount) {
        this.sendAmount = sendAmount;
    }

    public String getGetAmount() {
        return getAmount;
    }

    public void setGetAmount(String getAmount) {
        this.getAmount = getAmount;
    }

    public VerifyMobileData getReceiverDetails() {
        return receiverDetails;
    }

    public void setReceiverDetails(VerifyMobileData receiverDetails) {
        this.receiverDetails = receiverDetails;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public BeneficiaryCommonData getSenderDetails() {
        return senderDetails;
    }

    public void setSenderDetails(BeneficiaryCommonData senderDetails) {
        this.senderDetails = senderDetails;
    }
}
