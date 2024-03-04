package com.yeel.drc.api.accountsummary;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountSummaryData {
    @SerializedName("id") @Expose private String id;
    @SerializedName("user_id") @Expose private String user_id;
    @SerializedName("account_no") @Expose private String account_no;
    @SerializedName("transaction_description") @Expose private String transaction_description;
    @SerializedName("beneficiary_remitter_name") @Expose private String beneficiary_remitter_name;
    @SerializedName("entry_type") @Expose private String entry_type;
    @SerializedName("yeel_ref_id") @Expose private String yeel_ref_id;
    @SerializedName("amount") @Expose private String amount;
    @SerializedName("new_balance") @Expose private String new_balance;
    @SerializedName("transaction_date") @Expose private String transaction_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getTransaction_description() {
        return transaction_description;
    }

    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    public String getBeneficiary_remitter_name() {
        return beneficiary_remitter_name;
    }

    public void setBeneficiary_remitter_name(String beneficiary_remitter_name) {
        this.beneficiary_remitter_name = beneficiary_remitter_name;
    }

    public String getEntry_type() {
        return entry_type;
    }

    public void setEntry_type(String entry_type) {
        this.entry_type = entry_type;
    }

    public String getYeel_ref_id() {
        return yeel_ref_id;
    }

    public void setYeel_ref_id(String yeel_ref_id) {
        this.yeel_ref_id = yeel_ref_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNew_balance() {
        return new_balance;
    }

    public void setNew_balance(String new_balance) {
        this.new_balance = new_balance;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }
}
