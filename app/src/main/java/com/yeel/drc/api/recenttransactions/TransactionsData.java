package com.yeel.drc.api.recenttransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.api.transactiondetails.BillPaymentDetails;

import java.io.Serializable;
import java.util.List;

public class TransactionsData implements Serializable {

    @SerializedName("id") @Expose private String id;
    @SerializedName("ydb_ref_id") @Expose private String ydb_ref_id;
    @SerializedName("transaction_date") @Expose private String transaction_date;
    @SerializedName("amount") @Expose private String amount;
    @SerializedName("sender_account_no") @Expose private String sender_account_no;
    @SerializedName("sender_id") @Expose private String sender_id;
    @SerializedName("sender_name") @Expose private String sender_name;
    @SerializedName("receiver_account_no") @Expose private String receiver_account_no;
    @SerializedName("receiver_id") @Expose private String receiver_id;
    @SerializedName("receiver_name") @Expose private String receiver_name;
    @SerializedName("transaction_description") @Expose private String transaction_description;
    @SerializedName("transaction_type") @Expose private String transaction_type;
    @SerializedName("transaction_flag") @Expose private String transaction_flag;
    @SerializedName("status") @Expose private String status;
    @SerializedName("remarks") @Expose private String remarks;
    @SerializedName("profile_image") @Expose private String profile_image;
    @SerializedName("collection_status") @Expose private String collection_status;
    @SerializedName("beneficiary_detail") @Expose private BeneficiaryData beneficiaryDetails;
    @SerializedName("remitter_details") @Expose private BeneficiaryData remitterDetails;
    @SerializedName("agent_detail") @Expose private AgentData agentDetails;
    @SerializedName("agent_currency_detail") @Expose private List<CurrencyListData> currencyList;
    @SerializedName("reversed_transaction") @Expose private String reversed_transaction;
    @SerializedName("previous_transactionId") @Expose private String previous_transactionId;

    @SerializedName("bill_paid_details") @Expose private BillPaymentDetails billPaymentDetails;
    @SerializedName("utility_provider_detail") @Expose private ProvidersListData providerData;
    @SerializedName("provider_currency_detail") @Expose private List<CurrencyListData> providerCurrencyList;

    public BeneficiaryData getRemitterDetails() {
        return remitterDetails;
    }

    public void setRemitterDetails(BeneficiaryData remitterDetails) {
        this.remitterDetails = remitterDetails;
    }

    public BillPaymentDetails getBillPaymentDetails() {
        return billPaymentDetails;
    }



    public void setBillPaymentDetails(BillPaymentDetails billPaymentDetails) {
        this.billPaymentDetails = billPaymentDetails;
    }

    public ProvidersListData getProviderData() {
        return providerData;
    }

    public void setProviderData(ProvidersListData providerData) {
        this.providerData = providerData;
    }

    public List<CurrencyListData> getProviderCurrencyList() {
        return providerCurrencyList;
    }

    public void setProviderCurrencyList(List<CurrencyListData> providerCurrencyList) {
        this.providerCurrencyList = providerCurrencyList;
    }

    public String getPrevious_transactionId() {
        return previous_transactionId;
    }

    public void setPrevious_transactionId(String previous_transactionId) {
        this.previous_transactionId = previous_transactionId;
    }

    public String getReversed_transaction() {
        return reversed_transaction;
    }

    public void setReversed_transaction(String reversed_transaction) {
        this.reversed_transaction = reversed_transaction;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }

    public AgentData getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(AgentData agentDetails) {
        this.agentDetails = agentDetails;
    }

    public String getCollection_status() {
        return collection_status;
    }

    public void setCollection_status(String collection_status) {
        this.collection_status = collection_status;
    }

    public BeneficiaryData getBeneficiaryDetails() {
        return beneficiaryDetails;
    }

    public void setBeneficiaryDetails(BeneficiaryData beneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails;
    }

    String nameToShow;
    String formattedDate;
    Boolean showHeading;

    public Boolean getShowHeading() {
        return showHeading;
    }

    public void setShowHeading(Boolean showHeading) {
        this.showHeading = showHeading;
    }

    public TransactionsData(String id) {
        this.id = id;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getNameToShow() {
        return nameToShow;
    }

    public void setNameToShow(String nameToShow) {
        this.nameToShow = nameToShow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYdb_ref_id() {
        return ydb_ref_id;
    }

    public void setYdb_ref_id(String ydb_ref_id) {
        this.ydb_ref_id = ydb_ref_id;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSender_account_no() {
        return sender_account_no;
    }

    public void setSender_account_no(String sender_account_no) {
        this.sender_account_no = sender_account_no;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_account_no() {
        return receiver_account_no;
    }

    public void setReceiver_account_no(String receiver_account_no) {
        this.receiver_account_no = receiver_account_no;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getTransaction_description() {
        return transaction_description;
    }

    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getTransaction_flag() {
        return transaction_flag;
    }

    public void setTransaction_flag(String transaction_flag) {
        this.transaction_flag = transaction_flag;
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

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
