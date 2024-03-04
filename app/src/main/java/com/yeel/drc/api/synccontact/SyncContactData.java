package com.yeel.drc.api.synccontact;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class SyncContactData {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("contact_id")
    private long contactId;

    @SerializedName("phone")
    @Expose
    @ColumnInfo(name = "phone", defaultValue = "0")
    private String phone;

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name", defaultValue = "0")
    private String name;

    @SerializedName("ydb_name")
    @Expose
    @ColumnInfo(name = "ydb_name", defaultValue = "0")
    private String ydb_name;


    @SerializedName("yeel_status")
    @Expose
    @ColumnInfo(name = "yeel_status", defaultValue = "0")
    private int yeel_status;


    @SerializedName("account_id")
    @Expose
    @ColumnInfo(name = "account_id", defaultValue = "0")
    private String account_id;


    @SerializedName("user_id")
    @Expose
    @ColumnInfo(name = "user_id", defaultValue = "0")
    private String user_id;

    @SerializedName("profile_image")
    @Expose
    @ColumnInfo(name = "profile_image", defaultValue = "0")
    private String profile_image;


    public SyncContactData(String phone, String name, String ydb_name, int yeel_status, String account_id, String user_id, String profile_image) {
        this.phone = phone;
        this.name = name;
        this.ydb_name = ydb_name;
        this.yeel_status = yeel_status;
        this.account_id = account_id;
        this.user_id = user_id;
        this.profile_image = profile_image;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYdb_name() {
        return ydb_name;
    }

    public void setYdb_name(String ydb_name) {
        this.ydb_name = ydb_name;
    }

    public int getYeel_status() {
        return yeel_status;
    }

    public void setYeel_status(int yeel_status) {
        this.yeel_status = yeel_status;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
