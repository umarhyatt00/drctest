package com.yeel.drc.api.mpesadetailsfrommobile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VerifyMobileData implements Serializable {

    @SerializedName("customerName") @Expose private String customerName;
    @SerializedName("partnerTransactionID") @Expose private String partnerTransactionID;
    @SerializedName("mmTransactionID") @Expose private String mmTransactionID;
    @SerializedName("conversationID") @Expose private String conversationID;



    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPartnerTransactionID() {
        return partnerTransactionID;
    }

    public void setPartnerTransactionID(String partnerTransactionID) {
        this.partnerTransactionID = partnerTransactionID;
    }

    public String getMmTransactionID() {
        return mmTransactionID;
    }

    public void setMmTransactionID(String mmTransactionID) {
        this.mmTransactionID = mmTransactionID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }
}
