package com.yeel.drc.api.agentdashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentDashBoardData {
    @SerializedName("totalCashOut") @Expose private String totalCashOut;
    @SerializedName("totalCashOutCommission") @Expose private String totalCashOutCommission;

    @SerializedName("totalRemittances") @Expose private String totalRemittances;
    @SerializedName("totalRemittancesCommission") @Expose private String totalRemittancesCommission;


    @SerializedName("totalCashIn") @Expose private String totalCashIn;
    @SerializedName("totalCashInCommission") @Expose private String totalCashInCommission;

    @SerializedName("totalTransactions") @Expose private String totalTransactions;
    @SerializedName("totalCommission") @Expose private String totalCommission;


    @SerializedName("agentTotalCommission") @Expose private String agentTotalCommission;

    public String getAgentTotalCommission() {
        return agentTotalCommission;
    }

    public void setAgentTotalCommission(String agentTotalCommission) {
        this.agentTotalCommission = agentTotalCommission;
    }

    public String getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(String totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public String getTotalCashOutCommission() {
        return totalCashOutCommission;
    }

    public void setTotalCashOutCommission(String totalCashOutCommission) {
        this.totalCashOutCommission = totalCashOutCommission;
    }

    public String getTotalRemittances() {
        return totalRemittances;
    }

    public void setTotalRemittances(String totalRemittances) {
        this.totalRemittances = totalRemittances;
    }

    public String getTotalRemittancesCommission() {
        return totalRemittancesCommission;
    }

    public void setTotalRemittancesCommission(String totalRemittancesCommission) {
        this.totalRemittancesCommission = totalRemittancesCommission;
    }

    public String getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(String totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public String getTotalCashInCommission() {
        return totalCashInCommission;
    }

    public void setTotalCashInCommission(String totalCashInCommission) {
        this.totalCashInCommission = totalCashInCommission;
    }

    public String getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(String totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }
}
