package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentMethodsData {
    @SerializedName("methodName")
    @Expose
    private String methodName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("methodCode")
    @Expose
    private String methodCode;
    @SerializedName("type")
    @Expose
    private String type;

    public PaymentMethodsData(String methodName, String status, String methodCode, String type) {
        this.methodName = methodName;
        this.status = status;
        this.methodCode = methodCode;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
