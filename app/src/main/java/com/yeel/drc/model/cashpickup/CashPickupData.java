package com.yeel.drc.model.cashpickup;

import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.model.BeneficiaryCommonData;

import java.io.Serializable;

public class CashPickupData implements Serializable {
    BeneficiaryCommonData cashPickupReceiverData;
    BeneficiaryCommonData cashPickupSenderData;
    AgentData cashPickupAgentData;

    String purpose;
    String additionalNotes;
    String amount;
    String totalAmount;
    String feesAmount;
    String referenceNumber;
    String feesValue;
    String feesTierName;
    String cashPickupType;

    public String getCashPickupType() {
        return cashPickupType;
    }

    public void setCashPickupType(String cashPickupType) {
        this.cashPickupType = cashPickupType;
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

    public BeneficiaryCommonData getCashPickupSenderData() {
        return cashPickupSenderData;
    }

    public void setCashPickupSenderData(BeneficiaryCommonData cashPickupSenderData) {
        this.cashPickupSenderData = cashPickupSenderData;
    }

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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BeneficiaryCommonData getCashPickupReceiverData() {
        return cashPickupReceiverData;
    }

    public void setCashPickupReceiverData(BeneficiaryCommonData cashPickupReceiverData) {
        this.cashPickupReceiverData = cashPickupReceiverData;
    }


    public AgentData getCashPickupAgentData() {
        return cashPickupAgentData;
    }

    public void setCashPickupAgentData(AgentData cashPickupAgentData) {
        this.cashPickupAgentData = cashPickupAgentData;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
