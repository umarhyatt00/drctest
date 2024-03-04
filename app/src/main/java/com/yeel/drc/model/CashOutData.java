package com.yeel.drc.model;

import com.yeel.drc.api.fullagentlist.AgentData;

import java.io.Serializable;

public class CashOutData implements Serializable {

    AgentData agentData;
    String amount;
    String remark;
    String feesValue;
    String feesTierName;

    public AgentData getAgentData() {
        return agentData;
    }

    public void setAgentData(AgentData agentData) {
        this.agentData = agentData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFeesValue() {
        return feesValue;
    }

    public void setFeesValue(String feesValue) {
        this.feesValue = feesValue;
    }

    public String getFeesTierName() {
        return feesTierName;
    }

    public void setFeesTierName(String feesTierName) {
        this.feesTierName = feesTierName;
    }
}
