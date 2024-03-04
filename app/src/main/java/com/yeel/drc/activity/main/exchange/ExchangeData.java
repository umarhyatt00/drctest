package com.yeel.drc.activity.main.exchange;

import java.io.Serializable;

public class ExchangeData implements Serializable {

    String sendAmount;
    String getAmount;
    String receiverCurrency;
    String senderCurrency;
    String conversionAmount;


 /*   String feesAmount;
    String totalAmount;
    String feesType = "";
    String feesValue = "";
    String referenceNumber;*/

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

    public String getReceiverCurrency() {
        return receiverCurrency;
    }

    public void setReceiverCurrency(String receiverCurrency) {
        this.receiverCurrency = receiverCurrency;
    }

    public String getSenderCurrency() {
        return senderCurrency;
    }

    public void setSenderCurrency(String senderCurrency) {
        this.senderCurrency = senderCurrency;
    }

    public String getConversionAmount() {
        return conversionAmount;
    }

    public void setConversionAmount(String conversionAmount) {
        this.conversionAmount = conversionAmount;
    }
}
