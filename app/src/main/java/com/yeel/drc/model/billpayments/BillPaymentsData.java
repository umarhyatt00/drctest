package com.yeel.drc.model.billpayments;

import com.yeel.drc.api.providersList.ProvidersListData;

import java.io.Serializable;

public class BillPaymentsData implements Serializable {
    ProvidersListData providersListData;
    String studentName;
    String studentId;
    String studentMobileNumber;
    String studentMobileCountyCode;
    String remark;
    String amount;

    public String getStudentMobileCountyCode() {
        return studentMobileCountyCode;
    }

    public void setStudentMobileCountyCode(String studentMobileCountyCode) {
        this.studentMobileCountyCode = studentMobileCountyCode;
    }

    public ProvidersListData getProvidersListData() {
        return providersListData;
    }

    public void setProvidersListData(ProvidersListData providersListData) {
        this.providersListData = providersListData;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentMobileNumber() {
        return studentMobileNumber;
    }

    public void setStudentMobileNumber(String studentMobileNumber) {
        this.studentMobileNumber = studentMobileNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
