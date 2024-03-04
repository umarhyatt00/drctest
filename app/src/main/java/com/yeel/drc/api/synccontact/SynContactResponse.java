package com.yeel.drc.api.synccontact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SynContactResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("sync_time") @Expose private String sync_time;
    @SerializedName("result") @Expose private List<SyncContactData> syncContactList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SyncContactData> getSyncContactList() {
        return syncContactList;
    }

    public void setSyncContactList(List<SyncContactData> syncContactList) {
        this.syncContactList = syncContactList;
    }
}
