package com.yeel.drc.api.agentdashboard;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentDashBoardResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("result") @Expose private AgentDashBoardData agentDashBoardData;

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

    public AgentDashBoardData getAgentDashBoardData() {
        return agentDashBoardData;
    }

    public void setAgentDashBoardData(AgentDashBoardData agentDashBoardData) {
        this.agentDashBoardData = agentDashBoardData;
    }
}
