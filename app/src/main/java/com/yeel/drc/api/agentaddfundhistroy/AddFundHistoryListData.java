package com.yeel.drc.api.agentaddfundhistroy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddFundHistoryListData implements Serializable {
    @SerializedName("id") @Expose private String id;
    @SerializedName("req_id") @Expose private String req_id;
    @SerializedName("user_id") @Expose private String user_id;
    @SerializedName("bankName") @Expose private String bankName;
    @SerializedName("bankCode") @Expose private String bankCode;
    @SerializedName("amount") @Expose private String amount;
    @SerializedName("date") @Expose private String date;
    @SerializedName("bankReceiptImage") @Expose private String bankReceiptImage;
    @SerializedName("refNo") @Expose private String refNo;
    @SerializedName("status") @Expose private String status;
    @SerializedName("remarks") @Expose private String remarks;
    @SerializedName("common_image_url") @Expose private String common_image_url;
    @SerializedName("receipt_date") @Expose private String receipt_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReq_id() {
        return req_id;
    }

    public void setReq_id(String req_id) {
        this.req_id = req_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBankReceiptImage() {
        return bankReceiptImage;
    }

    public void setBankReceiptImage(String bankReceiptImage) {
        this.bankReceiptImage = bankReceiptImage;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCommon_image_url() {
        return common_image_url;
    }

    public void setCommon_image_url(String common_image_url) {
        this.common_image_url = common_image_url;
    }

    public String getReceipt_date() {
        return receipt_date;
    }

    public void setReceipt_date(String receipt_date) {
        this.receipt_date = receipt_date;
    }
}
