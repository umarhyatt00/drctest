package com.yeel.drc.model.cashpickup;

import java.io.Serializable;

public class CashPickupAgentData implements Serializable {
    String agentName;

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
