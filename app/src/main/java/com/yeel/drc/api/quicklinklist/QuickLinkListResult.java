package com.yeel.drc.api.quicklinklist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.recenttransactions.TransactionsData;

import java.util.List;

public class QuickLinkListResult {
    @SerializedName("quick_link_list") @Expose private List<TransactionsData> quickLinkList;

    public List<TransactionsData> getQuickLinkList() {
        return quickLinkList;
    }

    public void setQuickLinkList(List<TransactionsData> quickLinkList) {
        this.quickLinkList = quickLinkList;
    }
}
