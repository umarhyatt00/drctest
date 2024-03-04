package com.yeel.drc.api.basicdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yeel.drc.api.countylist.CountryListData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.login.LoginUserDetails;

import java.util.List;

public class BasicDetailsResponse {
    @SerializedName("status") @Expose private String status;
    @SerializedName("message") @Expose private String message;
    @SerializedName("total_currencies") @Expose private String total_currencies;
    @SerializedName("user_details") @Expose private LoginUserDetails userDetails;
    @SerializedName("kyc_details") @Expose private KYCDetailsData kycDetails;
    @SerializedName("bill_payment_options_list") @Expose private List<BillPaymentOptionsData> billPaymentOptionsList;
    @SerializedName("payment_methods") @Expose private List<PaymentMethodsData> paymentMethodsList;
    @SerializedName("user_currencies") @Expose private List<CurrencyListData> currencyList;
    @SerializedName("min_max_values") @Expose private MinMaxValues min_max_values;
    @SerializedName("user_country_details") @Expose private CountryListData countryListData;

    public CountryListData getCountryListData() {
        return countryListData;
    }

    public void setCountryListData(CountryListData countryListData) {
        this.countryListData = countryListData;
    }

    public MinMaxValues getMin_max_values() {
        return min_max_values;
    }

    public void setMin_max_values(MinMaxValues min_max_values) {
        this.min_max_values = min_max_values;
    }

    public String getTotal_currencies() {
        return total_currencies;
    }

    public void setTotal_currencies(String total_currencies) {
        this.total_currencies = total_currencies;
    }

    public List<CurrencyListData> getCurrencyList() {
        return currencyList;
    }

    public void setCurrencyList(List<CurrencyListData> currencyList) {
        this.currencyList = currencyList;
    }

    public List<PaymentMethodsData> getPaymentMethodsList() {
        return paymentMethodsList;
    }

    public void setPaymentMethodsList(List<PaymentMethodsData> paymentMethodsList) {
        this.paymentMethodsList = paymentMethodsList;
    }

    public List<BillPaymentOptionsData> getBillPaymentOptionsList() {
        return billPaymentOptionsList;
    }

    public void setBillPaymentOptionsList(List<BillPaymentOptionsData> billPaymentOptionsList) {
        this.billPaymentOptionsList = billPaymentOptionsList;
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

    public LoginUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(LoginUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public KYCDetailsData getKycDetails() {
        return kycDetails;
    }

    public void setKycDetails(KYCDetailsData kycDetails) {
        this.kycDetails = kycDetails;
    }
}
