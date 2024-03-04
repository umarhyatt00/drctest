package com.yeel.drc.api.agentcommission;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.recenttransactions.TransactionsData;

import java.util.List;

public class AgentCommissionResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("agentTotalCommission") @Expose private String agentTotalCommission;
    @SerializedName("commisson_list") @Expose private List<TransactionsData> commissionList;

    public String getAgentTotalCommission() {
        return agentTotalCommission;
    }

    public void setAgentTotalCommission(String agentTotalCommission) {
        this.agentTotalCommission = agentTotalCommission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransactionsData> getCommissionList() {
        return commissionList;
    }

    public void setCommissionList(List<TransactionsData> commissionList) {
        this.commissionList = commissionList;
    }
}
