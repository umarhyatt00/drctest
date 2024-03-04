package com.yeel.drc.api.transactiondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.userdetails.UserDetailsData;

import java.io.Serializable;
import java.util.List;

public class TransactionDetailsData implements Serializable {
    @SerializedName("transaction_date") @Expose private String transaction_date;
    @SerializedName("amount") @Expose private String amount;
    @SerializedName("sender_name") @Expose private String sender_name;
    @SerializedName("receiver_name") @Expose private String receiver_name;
    @SerializedName("transaction_type") @Expose private String transaction_type;
    @SerializedName("status") @Expose private String status;
    @SerializedName("transaction_description") @Expose private String remarks;
    @SerializedName("ydb_ref_id") @Expose private String ydb_ref_id;
    @SerializedName("receiver_account_no") @Expose private String receiver_account_no;
    @SerializedName("sender_account_no") @Expose private String sender_account_no;
    @SerializedName("receiver_id") @Expose private String receiver_id;
    @SerializedName("sender_id") @Expose private String sender_id;
    @SerializedName("mobile") @Expose private String mobile;
    @SerializedName("country_code") @Expose private String countryCode;
    @SerializedName("profile_image") @Expose private String profile_image;
    @SerializedName("commission_amount") @Expose private String commission_amount;
    @SerializedName("total_transaction_amount") @Expose private String total_transaction_amount;
    @SerializedName("purpose") @Expose private String purpose;
    @SerializedName("mobile_money_seafire_id") @Expose private String mobile_money_seafire_id;
    @SerializedName("beneficiary_detail") @Expose private BeneficiaryData beneficiaryDetails;
    @SerializedName("remitter_details") @Expose private BeneficiaryData remitterDetails;
    @SerializedName("agent_detail") @Expose private AgentData agentDetails;
    @SerializedName("sender_agent_detail") @Expose private AgentData sender_agent_detail;
    @SerializedName("receiver_agent_detail") @Expose private AgentData receiver_agent_detail;
    @SerializedName("sender_detail") @Expose private UserDetailsData sender_detail;
    @SerializedName("collection_date") @Expose private String collectionData;
    @SerializedName("beneficiary_docs") @Expose private BeneficiaryDocDetails beneficiary_docs;
    @SerializedName("beneficiary_docs_url") @Expose private String beneficiary_docs_url;
    @SerializedName("receiver_gets_amt") @Expose private String receiver_gets_amt;
    @SerializedName("exchange_rate") @Expose private String exchange_rate;
    @SerializedName("mmoney_receiver_country_code") @Expose private String mMoneyReceiverCountryCode;
    @SerializedName("mmoney_receiver_mobile") @Expose private String mMoneyReceiverMobile;
    @SerializedName("yeel_commission_amount") @Expose private String yeelCommissionAmount;
    @SerializedName("agent_currency_detail") @Expose private List<CurrencyListData> currencyList;
    @SerializedName("bill_paid_details") @Expose private BillPaymentDetails billPaymentDetails;
    @SerializedName("utility_provider_detail") @Expose private ProvidersListData providerData;
    @SerializedName("provider_currency_detail") @Expose private List<CurrencyListData> providerCurrencyList;

    @SerializedName("amount_ssp") @Expose private String amountSSP;
    @SerializedName("amount_usd") @Expose private String amountUSD;


    public String getAmountSSP() {
        return amountSSP;
    }

    public void setAmountSSP(String amountSSP) {
        this.amountSSP = amountSSP;
    }

    public String getAmountUSD() {
        return amountUSD;
    }

    public void setAmountUSD(String amountUSD) {
        this.amountUSD = amountUSD;
    }

    public String getYeelCommissionAmount() {
        return yeelCommissionAmount;
    }

    public void setYeelCommissionAmount(String yeelCommissionAmount) {
        this.yeelCommissionAmount = yeelCommissionAmount;
    }

    public String getMobile_money_seafire_id() {
        return mobile_money_seafire_id;
    }

    public void setMobile_money_seafire_id(String mobile_money_seafire_id) {
        this.mobile_money_seafire_id = mobile_money_seafire_id;
    }

    public AgentData getReceiver_agent_detail() {
        return receiver_agent_detail;
    }

    public void setReceiver_agent_detail(AgentData receiver_agent_detail) {
        this.receiver_agent_detail = receiver_agent_detail;
    }

    public AgentData getSender_agent_detail() {
        return sender_agent_detail;
    }

    public void setSender_agent_detail(AgentData sender_agent_detail) {
        this.sender_agent_detail = sender_agent_detail;
    }

    public List<CurrencyListData> getProviderCurrencyList() {
        return providerCurrencyList;
    }

    public BeneficiaryData getRemitterDetails() {
        return remitterDetails;
    }

    public void setRemitterDetails(BeneficiaryData remitterDetails) {
        this.remitterDetails = remitterDetails;
    }

    public void setProviderCurrencyList(List<CurrencyListData> providerCurrencyList) {
        this.providerCurrencyList = providerCurrencyList;
    }

    public ProvidersListData getProviderData() {
        return providerData;
    }

    public void setProviderData(ProvidersListData providerData) {
        this.providerData = providerData;
    }

    public BillPaymentDetails getBillPaymentDetails() {
        return billPaymentDetails;
    }

    public void setBillPaymentDetails(BillPaymentDetails billPaymentDetails) {
        this.billPaymentDetails = billPaymentDetails;
    }

    public String getmMoneyReceiverCountryCode() {
        return mMoneyReceiverCountryCode;
    }

    public void setmMoneyReceiverCountryCode(String mMoneyReceiverCountryCode) {
        this.mMoneyReceiverCountryCode = mMoneyReceiverCountryCode;
    }

    public String getmMoneyReceiverMobile() {
        return mMoneyReceiverMobile;
    }

    public void setmMoneyReceiverMobile(String mMoneyReceiverMobile) {
        this.mMoneyReceiverMobile = mMoneyReceiverMobile;
    }

    public String getExchange_rate() {
        return exchange_rate;
    }

    public void setExchange_rate(String exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getReceiver_gets_amt() {
        return receiver_gets_amt;
    }

    public void setReceiver_gets_amt(String receiver_gets_amt) {
        this.receiver_gets_amt = receiver_gets_amt;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BeneficiaryDocDetails getBeneficiary_docs() {
        return beneficiary_docs;
    }

    public void setBeneficiary_docs(BeneficiaryDocDetails beneficiary_docs) {
        this.beneficiary_docs = beneficiary_docs;
    }

    public String getBeneficiary_docs_url() {
        return beneficiary_docs_url;
    }

    public void setBeneficiary_docs_url(String beneficiary_docs_url) {
        this.beneficiary_docs_url = beneficiary_docs_url;
    }

    public UserDetailsData getSender_detail() {
        return sender_detail;
    }

    public void setSender_detail(UserDetailsData sender_detail) {
        this.sender_detail = sender_detail;
    }

    public String getCollectionData() {
        return collectionData;
    }

    public void setCollectionData(String collectionData) {
        this.collectionData = collectionData;
    }

    public AgentData getAgentDetails() {
        return agentDetails;
    }

    public void setAgentDetails(AgentData agentDetails) {
        this.agentDetails = agentDetails;
    }

    public BeneficiaryData getBeneficiaryDetails() {
        return beneficiaryDetails;
    }

    public void setBeneficiaryDetails(BeneficiaryData beneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getCommission_amount() {
        return commission_amount;
    }

    public void setCommission_amount(String commission_amount) {
        this.commission_amount = commission_amount;
    }

    public String getTotal_transaction_amount() {
        return total_transaction_amount;
    }

    public void setTotal_transaction_amount(String total_transaction_amount) {
        this.total_transaction_amount = total_transaction_amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
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

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
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

    public String getYdb_ref_id() {
        return ydb_ref_id;
    }

    public void setYdb_ref_id(String ydb_ref_id) {
        this.ydb_ref_id = ydb_ref_id;
    }

    public String getReceiver_account_no() {
        return receiver_account_no;
    }

    public void setReceiver_account_no(String receiver_account_no) {
        this.receiver_account_no = receiver_account_no;
    }

    public String getSender_account_no() {
        return sender_account_no;
    }

    public void setSender_account_no(String sender_account_no) {
        this.sender_account_no = sender_account_no;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }
}

