package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MinMaxData {
    @SerializedName("FUNDTRANSFER_MIN") @Expose private String FUNDTRANSFER_MIN;
    @SerializedName("FUNDTRANSFER_MAX") @Expose private String FUNDTRANSFER_MAX;
    @SerializedName("CASHOUT_MIN") @Expose private String CASHOUT_MIN;
    @SerializedName("CASHOUT_MAX") @Expose private String CASHOUT_MAX;
    @SerializedName("CASHPICKUP_MIN") @Expose private String CASHPICKUP_MIN;
    @SerializedName("CASHPICKUP_MAX") @Expose private String CASHPICKUP_MAX;
    @SerializedName("MERCHANT_TRANSFER_MIN") @Expose private String MERCHANT_TRANSFER_MIN;
    @SerializedName("MERCHANT_TRANSFER_MAX") @Expose private String MERCHANT_TRANSFER_MAX;
    
    @SerializedName("ADD_FUND_MIN") @Expose private String ADD_FUND_MIN;
    @SerializedName("ADD_FUND_MAX") @Expose private String ADD_FUND_MAX;

    @SerializedName("MPESA_KENYA_MAX") @Expose private String MPESA_KENYA_MAX;
    @SerializedName("MPESA_KENYA_MIN") @Expose private String MPESA_KENYA_MIN;
    @SerializedName("MTN_UGANDA_MAX") @Expose private String MTN_UGANDA_MAX;
    @SerializedName("MTN_UGANDA_MIN") @Expose private String MTN_UGANDA_MIN;
    @SerializedName("AIRTEL_UGANDA_MAX") @Expose private String AIRTEL_UGANDA_MAX;
    @SerializedName("AIRTEL_UGANDA_MIN") @Expose private String AIRTEL_UGANDA_MIN;


    @SerializedName("AIRTEL_DAILY_LIMIT") @Expose private String AIRTEL_DAILY_LIMIT;
    @SerializedName("MPESA_DAILY_LIMIT") @Expose private String MPESA_DAILY_LIMIT;

    public String getAIRTEL_DAILY_LIMIT() {
        return AIRTEL_DAILY_LIMIT;
    }

    public void setAIRTEL_DAILY_LIMIT(String AIRTEL_DAILY_LIMIT) {
        this.AIRTEL_DAILY_LIMIT = AIRTEL_DAILY_LIMIT;
    }

    public String getMPESA_DAILY_LIMIT() {
        return MPESA_DAILY_LIMIT;
    }

    public void setMPESA_DAILY_LIMIT(String MPESA_DAILY_LIMIT) {
        this.MPESA_DAILY_LIMIT = MPESA_DAILY_LIMIT;
    }

    public String getMPESA_KENYA_MAX() {
        return MPESA_KENYA_MAX;
    }

    public void setMPESA_KENYA_MAX(String MPESA_KENYA_MAX) {
        this.MPESA_KENYA_MAX = MPESA_KENYA_MAX;
    }

    public String getMPESA_KENYA_MIN() {
        return MPESA_KENYA_MIN;
    }

    public void setMPESA_KENYA_MIN(String MPESA_KENYA_MIN) {
        this.MPESA_KENYA_MIN = MPESA_KENYA_MIN;
    }

    public String getMTN_UGANDA_MAX() {
        return MTN_UGANDA_MAX;
    }

    public void setMTN_UGANDA_MAX(String MTN_UGANDA_MAX) {
        this.MTN_UGANDA_MAX = MTN_UGANDA_MAX;
    }

    public String getMTN_UGANDA_MIN() {
        return MTN_UGANDA_MIN;
    }

    public void setMTN_UGANDA_MIN(String MTN_UGANDA_MIN) {
        this.MTN_UGANDA_MIN = MTN_UGANDA_MIN;
    }

    public String getAIRTEL_UGANDA_MAX() {
        return AIRTEL_UGANDA_MAX;
    }

    public void setAIRTEL_UGANDA_MAX(String AIRTEL_UGANDA_MAX) {
        this.AIRTEL_UGANDA_MAX = AIRTEL_UGANDA_MAX;
    }

    public String getAIRTEL_UGANDA_MIN() {
        return AIRTEL_UGANDA_MIN;
    }

    public void setAIRTEL_UGANDA_MIN(String AIRTEL_UGANDA_MIN) {
        this.AIRTEL_UGANDA_MIN = AIRTEL_UGANDA_MIN;
    }

    public String getADD_FUND_MIN() {
        return ADD_FUND_MIN;
    }

    public void setADD_FUND_MIN(String ADD_FUND_MIN) {
        this.ADD_FUND_MIN = ADD_FUND_MIN;
    }

    public String getADD_FUND_MAX() {
        return ADD_FUND_MAX;
    }

    public void setADD_FUND_MAX(String ADD_FUND_MAX) {
        this.ADD_FUND_MAX = ADD_FUND_MAX;
    }

    public String getMERCHANT_TRANSFER_MIN() {
        return MERCHANT_TRANSFER_MIN;
    }

    public void setMERCHANT_TRANSFER_MIN(String MERCHANT_TRANSFER_MIN) {
        this.MERCHANT_TRANSFER_MIN = MERCHANT_TRANSFER_MIN;
    }

    public String getMERCHANT_TRANSFER_MAX() {
        return MERCHANT_TRANSFER_MAX;
    }

    public void setMERCHANT_TRANSFER_MAX(String MERCHANT_TRANSFER_MAX) {
        this.MERCHANT_TRANSFER_MAX = MERCHANT_TRANSFER_MAX;
    }

    public String getFUNDTRANSFER_MIN() {
        return FUNDTRANSFER_MIN;
    }

    public void setFUNDTRANSFER_MIN(String FUNDTRANSFER_MIN) {
        this.FUNDTRANSFER_MIN = FUNDTRANSFER_MIN;
    }

    public String getFUNDTRANSFER_MAX() {
        return FUNDTRANSFER_MAX;
    }

    public void setFUNDTRANSFER_MAX(String FUNDTRANSFER_MAX) {
        this.FUNDTRANSFER_MAX = FUNDTRANSFER_MAX;
    }

    public String getCASHOUT_MIN() {
        return CASHOUT_MIN;
    }

    public void setCASHOUT_MIN(String CASHOUT_MIN) {
        this.CASHOUT_MIN = CASHOUT_MIN;
    }

    public String getCASHOUT_MAX() {
        return CASHOUT_MAX;
    }

    public void setCASHOUT_MAX(String CASHOUT_MAX) {
        this.CASHOUT_MAX = CASHOUT_MAX;
    }

    public String getCASHPICKUP_MIN() {
        return CASHPICKUP_MIN;
    }

    public void setCASHPICKUP_MIN(String CASHPICKUP_MIN) {
        this.CASHPICKUP_MIN = CASHPICKUP_MIN;
    }

    public String getCASHPICKUP_MAX() {
        return CASHPICKUP_MAX;
    }

    public void setCASHPICKUP_MAX(String CASHPICKUP_MAX) {
        this.CASHPICKUP_MAX = CASHPICKUP_MAX;
    }
}
