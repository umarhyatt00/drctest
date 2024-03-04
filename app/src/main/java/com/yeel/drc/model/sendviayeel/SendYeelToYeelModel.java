package com.yeel.drc.model.sendviayeel;

import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.model.BeneficiaryCommonData;

import java.io.Serializable;

public class SendYeelToYeelModel implements Serializable {

    BeneficiaryCommonData senderData;
    UserDetailsData receiverData;
    String amount;
    String totalAmount;
    String feesAmount;
    String purpose;
    String feesValue;
    String feesTierName;
    String referenceNumber;


    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(String feesAmount) {
        this.feesAmount = feesAmount;
    }

    public String getFeesValue() {
        return feesValue;
    }

    public void setFeesValue(String feesValue) {
        this.feesValue = feesValue;
    }

    public String getFeesTierName() {
        return feesTierName;
    }

    public void setFeesTierName(String feesTierName) {
        this.feesTierName = feesTierName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BeneficiaryCommonData getSenderData() {
        return senderData;
    }

    public void setSenderData(BeneficiaryCommonData senderData) {
        this.senderData = senderData;
    }

    public UserDetailsData getReceiverData() {
        return receiverData;
    }

    public void setReceiverData(UserDetailsData receiverData) {
        this.receiverData = receiverData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }


}
