package com.yeel.drc.api.mpesapayment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MpesaPaymentResponseData {
    @SerializedName("transactionId") @Expose private String transactionId;
    @SerializedName("partnerTransactionId") @Expose private String partnerTransactionId;
    @SerializedName("paymentCode") @Expose private String paymentCode;


    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPartnerTransactionId() {
        return partnerTransactionId;
    }

    public void setPartnerTransactionId(String partnerTransactionId) {
        this.partnerTransactionId = partnerTransactionId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

}
