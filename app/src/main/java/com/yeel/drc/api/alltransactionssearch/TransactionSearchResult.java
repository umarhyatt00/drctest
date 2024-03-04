package com.yeel.drc.api.alltransactionssearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.recenttransactions.TransactionsData;

import java.util.List;

public class TransactionSearchResult {
    @SerializedName("all_transactions") @Expose private List<TransactionsData> transactionList;

    public List<TransactionsData> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<TransactionsData> transactionList) {
        this.transactionList = transactionList;
    }
}
