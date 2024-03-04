package com.yeel.drc.api.agentaddfundhistroy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddFundHistoryResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("request_list") @Expose private List<AddFundHistoryListData> list;

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

    public List<AddFundHistoryListData> getList() {
        return list;
    }

    public void setList(List<AddFundHistoryListData> list) {
        this.list = list;
    }
}
