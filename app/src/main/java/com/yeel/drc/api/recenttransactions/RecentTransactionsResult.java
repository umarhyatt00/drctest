package com.yeel.drc.api.recenttransactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecentTransactionsResult {
    @SerializedName("recent_transactions") @Expose private List<TransactionsData> recentTransactions;

    public List<TransactionsData> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionsData> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
