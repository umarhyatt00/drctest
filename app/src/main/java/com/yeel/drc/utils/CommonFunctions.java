package com.yeel.drc.utils;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.yeel.drc.R;
import com.yeel.drc.activity.login.ReturningUserActivity;
import com.yeel.drc.activity.main.HomeActivity;
import com.yeel.drc.activity.main.settings.kyc.ViewKYCImagesActivity;
import com.yeel.drc.activity.signup.ChooseLinkOrNewActivity;
import com.yeel.drc.activity.signup.CurrencySelectionActivity;
import com.yeel.drc.activity.signup.FirstTimeLanguageSelectionActivity;
import com.yeel.drc.activity.signup.KYCDetailsActivity;
import com.yeel.drc.activity.signup.agent.AgentAddressActivity;
import com.yeel.drc.activity.signup.agent.BusinessAgentInformationActivity;
import com.yeel.drc.activity.signup.agent.IndividualAgentInformation;
import com.yeel.drc.activity.signup.business.BusinessAddressActivity;
import com.yeel.drc.activity.signup.business.BusinessInformationActivity;
import com.yeel.drc.activity.signup.EmailAddressActivity;
import com.yeel.drc.activity.signup.business.BusinessKYCUploadActivity;
import com.yeel.drc.activity.signup.personal.PersonalInformationActivity;
import com.yeel.drc.activity.signup.personal.PersonalKYCUploadActivity;
import com.yeel.drc.activity.signup.personal.ResidenceDetailsActivity;
import com.yeel.drc.activity.signup.SelectAccountTypeActivity;
import com.yeel.drc.activity.WelcomeActivity;
import com.yeel.drc.activity.signup.personal.UploadPersonalDocumentActivity;
import com.yeel.drc.api.basicdetails.KYCDetailsData;
import com.yeel.drc.api.basicdetails.MinMaxValues;
import com.yeel.drc.api.basicdetails.PaymentMethodsData;
import com.yeel.drc.api.countylist.CountryListData;
import com.yeel.drc.api.forceupdate.ForceUpdateData;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.login.LoginUserDetails;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.userdetails.UserDetailsData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonFunctions {
    Context context;
    SharedPrefUtil sharedPrefUtil;

    public CommonFunctions(Context context) {
        this.context = context;
        sharedPrefUtil = new SharedPrefUtil(context);

    }


    public void redirectionFromSplash() {
        Intent intent = null;
        if (getPage().equals("")) {
            if (getLoginStatus()) {
                if (isSequrityEnabled()) {
                    if (getPIN().equals("")) {
                        intent = new Intent(context, ReturningUserActivity.class);
                    } else {
                        intent = new Intent(context, HomeActivity.class);
                    }
                } else {
                    intent = new Intent(context, ReturningUserActivity.class);
                }
            } else {
                if (isShownLanguageSelection()) {
                    intent = new Intent(context, WelcomeActivity.class);
                } else {
                    intent = new Intent(context, FirstTimeLanguageSelectionActivity.class);
                }

            }

        } else if (getPage().equals("mobile_enter")) {
            intent = new Intent(context, ChooseLinkOrNewActivity.class);
        } else  if(getPage().equals("create_account")){
            intent = new Intent(context, CurrencySelectionActivity.class);
        }else if(getPage().equals("link_account")){

        }else if (getPage().equals("currency_selection")) {
            intent = new Intent(context, SelectAccountTypeActivity.class);
        } else if (getPage().equals("account_type_i")) {
            intent = new Intent(context, PersonalInformationActivity.class);
        } else if (getPage().equals("account_type_b")) {
            intent = new Intent(context, BusinessInformationActivity.class);
        } else if (getPage().equals("account_type_i_a")) {
            intent = new Intent(context, IndividualAgentInformation.class);
        } else if (getPage().equals("account_type_b_a")) {
            intent = new Intent(context, BusinessAgentInformationActivity.class);
        } else if (getPage().equals("personal_info")) {
            intent = new Intent(context, UploadPersonalDocumentActivity.class);
        } else if (getPage().equals("business_info")) {
            intent = new Intent(context, EmailAddressActivity.class);
        } else if (getPage().equals("individual_agent_info")) {
            intent = new Intent(context, EmailAddressActivity.class);
        } else if (getPage().equals("business_agent_info")) {
            intent = new Intent(context, EmailAddressActivity.class);
        } else if (getPage().equals("email_enter_i")) {
            intent = new Intent(context, ResidenceDetailsActivity.class);
        } else if (getPage().equals("email_enter_b")) {
            intent = new Intent(context, BusinessAddressActivity.class);
        } else if (getPage().equals("email_enter_a")) {
            intent = new Intent(context, AgentAddressActivity.class);
        } else if (getPage().equals("business_address")) {
            intent = new Intent(context, BusinessKYCUploadActivity.class);
        } else if (getPage().equals("residence_details")) {
            intent = new Intent(context, PersonalKYCUploadActivity.class);
        } else if (getPage().equals("agent_individual_address")) {
            intent = new Intent(context, PersonalKYCUploadActivity.class);
        } else if (getPage().equals("agent_business_address")) {
            intent = new Intent(context, BusinessKYCUploadActivity.class);
        } else if (getPage().equals("kyc_doc")) {
            intent = new Intent(context, KYCDetailsActivity.class);
        } else if (getPage().equals("kyc_details")) {
            intent = new Intent(context, KYCDetailsActivity.class);
        }
        assert intent != null;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public void callHomeIntent() {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void callReturningUserIntent(String from) {
        setLogoutStatus(true);
        Intent intent = new Intent(context, ReturningUserActivity.class);
        intent.putExtra("from-logout", from);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void setAppCurrency() {
        if (getCurrencyID().equals("1")) {
            SthiramValues.SELECTED_CURRENCY_SYMBOL = "SSP";
            SthiramValues.SELECTED_CURRENCY_ID = "1";
        } else {
            SthiramValues.SELECTED_CURRENCY_SYMBOL = "USD";
            SthiramValues.SELECTED_CURRENCY_ID = "2";
        }
    }

    public void setUserDetailsAfterLogin(String accessToken, LoginUserDetails loginUserDetails) {
        setAccessToken(accessToken);
        setUserId(loginUserDetails.getId());
        setFirstName(loginUserDetails.getFirstname());
        setMiddleName(loginUserDetails.getMiddlename());
        setLastName(loginUserDetails.getLastname());

        String fullName = "";
        if (loginUserDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            fullName = generateFullName(loginUserDetails.getFirstname(), loginUserDetails.getMiddlename(), loginUserDetails.getLastname());
        } else if (loginUserDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            fullName = loginUserDetails.getBusinessName();
        } else if (loginUserDetails.getUser_type().equals(ACCOUNT_TYPE_AGENT)) {
            if (loginUserDetails.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                fullName = generateFullName(loginUserDetails.getFirstname(), loginUserDetails.getMiddlename(), loginUserDetails.getLastname());
            } else {
                fullName = loginUserDetails.getBusinessName();
            }
        }
        setFullName(fullName);
        setEmail(loginUserDetails.getEmail());
        setCountryCode(loginUserDetails.getCountryCode());
        setMobile(loginUserDetails.getMobile());
        setQRImage(loginUserDetails.getQrImage());
        setQRCode(loginUserDetails.getQrCode());
        setNotificationCount(loginUserDetails.getNotCount());
        setProfileImage(loginUserDetails.getProfile_image());
        setLoginStatus(true);
        setLogoutStatus(false);
        setSecurityShownStatus(true);
        setUserType(loginUserDetails.getUser_type());
        setLocality(loginUserDetails.getLocality());
        setDistrict(loginUserDetails.getDistrict());
        setProvince(loginUserDetails.getProvince());
        setBusinessName(loginUserDetails.getBusinessName());
        setAuthorizedPersonOne(loginUserDetails.getAuthorized_person1());
        setAuthorizedPersonTwo(loginUserDetails.getAuthorized_person2());
        setAddressLineOne(loginUserDetails.getAddressline1());
        setAddressLineTwo(loginUserDetails.getAddressline2());
        setAgentType(loginUserDetails.getAgent_type());
        setTaxId(loginUserDetails.getTax_id());
        setUserAccountNumber(loginUserDetails.getAccount_no());
        setCurrencyID(loginUserDetails.getCurrency_id());
        setAppCurrency();
    }

    public void setUserDetailsFromBasicDetails(LoginUserDetails loginUserDetails) {
        setUserId(loginUserDetails.getId());
        setFirstName(loginUserDetails.getFirstname());
        setMiddleName(loginUserDetails.getMiddlename());
        setLastName(loginUserDetails.getLastname());

        String fullName = "";
        if (loginUserDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            fullName = generateFullName(loginUserDetails.getFirstname(), loginUserDetails.getMiddlename(), loginUserDetails.getLastname());
        } else if (loginUserDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            fullName = loginUserDetails.getBusinessName();
        } else if (loginUserDetails.getUser_type().equals(ACCOUNT_TYPE_AGENT)) {
            if (loginUserDetails.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                fullName = generateFullName(loginUserDetails.getFirstname(), loginUserDetails.getMiddlename(), loginUserDetails.getLastname());
            } else {
                fullName = loginUserDetails.getBusinessName();
            }
        }
        setFullName(fullName);

        setEmail(loginUserDetails.getEmail());
        setCountryCode(loginUserDetails.getCountryCode());
        setMobile(loginUserDetails.getMobile());
        setQRImage(loginUserDetails.getQrImage());
        setQRCode(loginUserDetails.getQrCode());
        setNotificationCount(loginUserDetails.getNotCount());
        setProfileImage(loginUserDetails.getProfile_image());
        setLoginStatus(true);
        setLogoutStatus(false);
        setSecurityShownStatus(true);
        setUserType(loginUserDetails.getUser_type());
        setLocality(loginUserDetails.getLocality());
        setDistrict(loginUserDetails.getDistrict());
        setProvince(loginUserDetails.getProvince());
        setBusinessName(loginUserDetails.getBusinessName());
        setAuthorizedPersonOne(loginUserDetails.getAuthorized_person1());
        setAuthorizedPersonTwo(loginUserDetails.getAuthorized_person2());
        setAddressLineOne(loginUserDetails.getAddressline1());
        setAddressLineTwo(loginUserDetails.getAddressline2());
        setAgentType(loginUserDetails.getAgent_type());
        setTaxId(loginUserDetails.getTax_id());
        setUserAccountBalance(loginUserDetails.getBalance());
        setUserAccountNumber(loginUserDetails.getAccount_no());
        setCurrencyID(loginUserDetails.getCurrency_id());
        setPreApproved(loginUserDetails.getPreapproved());
        if (getCurrencyID().equals("1")) {
            //SSP
            setPreApprovedLimit(loginUserDetails.getPreapproved_limit_SSP());
        } else {
            //USD
            setPreApprovedLimit(loginUserDetails.getPreapproved_limit_USD());
        }

        setPreApprovedLimitSSP(loginUserDetails.getPreapproved_limit_SSP());
        setPreApprovedLimitUSD(loginUserDetails.getPreapproved_limit_USD());

        setAppCurrency();
    }

    public void setMinAndMaximumDetails(MinMaxValues minAndMaximumDetails, String currencyId) {
        if (currencyId.equals("1")) {
            setFundTransferMin(minAndMaximumDetails.getSSP().getFUNDTRANSFER_MIN());
            setFundTransferMax(minAndMaximumDetails.getSSP().getFUNDTRANSFER_MAX());

            setCashOutMin(minAndMaximumDetails.getSSP().getCASHOUT_MIN());
            setCashOutMax(minAndMaximumDetails.getSSP().getCASHOUT_MAX());

            setCashPickupMin(minAndMaximumDetails.getSSP().getCASHPICKUP_MIN());
            setCashPickupMax(minAndMaximumDetails.getSSP().getCASHPICKUP_MAX());

            setMerchantTransferMin(minAndMaximumDetails.getSSP().getMERCHANT_TRANSFER_MIN());
            setCMerchantTransferMax(minAndMaximumDetails.getSSP().getMERCHANT_TRANSFER_MAX());

            setAddFundMin(minAndMaximumDetails.getSSP().getADD_FUND_MIN());
            setAddFundMax(minAndMaximumDetails.getSSP().getADD_FUND_MAX());

            setMpesaMin(minAndMaximumDetails.getSSP().getMPESA_KENYA_MIN());
            setMpesaMax(minAndMaximumDetails.getSSP().getMPESA_KENYA_MAX());

            setAirtelMin(minAndMaximumDetails.getSSP().getAIRTEL_UGANDA_MIN());
            setAirtelMax(minAndMaximumDetails.getSSP().getAIRTEL_UGANDA_MAX());

            setMTNMin(minAndMaximumDetails.getSSP().getMTN_UGANDA_MIN());
            setMTNMax(minAndMaximumDetails.getSSP().getMTN_UGANDA_MAX());


        } else {
            setFundTransferMin(minAndMaximumDetails.getUSD().getFUNDTRANSFER_MIN());
            setFundTransferMax(minAndMaximumDetails.getUSD().getFUNDTRANSFER_MAX());

            setCashOutMin(minAndMaximumDetails.getUSD().getCASHOUT_MIN());
            setCashOutMax(minAndMaximumDetails.getUSD().getCASHOUT_MAX());

            setCashPickupMin(minAndMaximumDetails.getUSD().getCASHPICKUP_MIN());
            setCashPickupMax(minAndMaximumDetails.getUSD().getCASHPICKUP_MAX());

            setMerchantTransferMin(minAndMaximumDetails.getUSD().getMERCHANT_TRANSFER_MIN());
            setCMerchantTransferMax(minAndMaximumDetails.getUSD().getMERCHANT_TRANSFER_MAX());

            setAddFundMin(minAndMaximumDetails.getUSD().getADD_FUND_MIN());
            setAddFundMax(minAndMaximumDetails.getUSD().getADD_FUND_MAX());

            setMpesaMin(minAndMaximumDetails.getUSD().getMPESA_KENYA_MIN());
            setMpesaMax(minAndMaximumDetails.getUSD().getMPESA_KENYA_MAX());

            setAirtelMin(minAndMaximumDetails.getUSD().getAIRTEL_UGANDA_MIN());
            setAirtelMax(minAndMaximumDetails.getUSD().getAIRTEL_UGANDA_MAX());

            setMTNMin(minAndMaximumDetails.getUSD().getMTN_UGANDA_MIN());
            setMTNMax(minAndMaximumDetails.getUSD().getMTN_UGANDA_MAX());

        }

        if (minAndMaximumDetails.getMobilePayLimits() != null) {
            setMpesaDailyLimit(minAndMaximumDetails.getMobilePayLimits().getMPESA_DAILY_LIMIT_KES());
            setAirtelDailyLimit(minAndMaximumDetails.getMobilePayLimits().getAIRTEL_DAILY_LIMIT_UGX());
        }
    }


    public void setKYCDetails(KYCDetailsData kycDetails) {
        setKYCStatus(kycDetails.getKyc_status());
        setKYCId(kycDetails.getId());
    }

    public void setCountryDetails(CountryListData kycDetails) {
        try {
            if (kycDetails != null) {
                setCountryFlag(kycDetails.getFlag());
                setMobileNumberFormat(kycDetails.getFormat());
            }
        } catch (Exception ignored) {

        }
    }


    public void setPaymentMethodStatus(List<PaymentMethodsData> paymentMethodsList) {
        for (int i = 0; i < paymentMethodsList.size(); i++) {
            String methodCode = paymentMethodsList.get(i).getMethodCode();
            String methodStatus = paymentMethodsList.get(i).getStatus();
            switch (methodCode) {
                case SthiramValues.SEND_VIA_YEEL_CODE:
                    setSendViaYeelStatus(methodStatus);
                    break;
                case SthiramValues.CASH_PICKUP_CODE:
                    setCashPickupStatus(methodStatus);
                    break;
                case SthiramValues.MPESA_KENYA_CODE:
                    setMpesaKenyaStatus(methodStatus);
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    setAirtelUgandaStatus(methodStatus);
                    break;
                case SthiramValues.EXUSD_CODE:
                    setExchangeStatus(methodStatus);
                    break;
            }
        }
    }

    //Exchange Status
    public void setExchangeStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("EXCHANGE_STATUS", value);
    }

    //get exchange status
    public String getExchangeStatus() {
        return sharedPrefUtil.getEncryptedStringPreference("EXCHANGE_STATUS", "");
    }


    //SEND_VIA_YEEL_STATUS
    public void setSendViaYeelStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("SEND_VIA_YEEL_STATUS", value);
    }

    //CASH_PICKUP status
    public void setCashPickupStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("CASH_PICKUP_STATUS", value);
    }


    //MPESA_KENYA status
    public void setMpesaKenyaStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MPESA_KENYA_STATUS", value);
    }

    public String getMpesaKenyaStatus() {
        return sharedPrefUtil.getEncryptedStringPreference("MPESA_KENYA_STATUS", "");
    }

    //AIRTAL_UGANDA status
    public void setAirtelUgandaStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("AIRTAL_UGANDA_STATUS", value);
    }

    public String getAirtelUgandaStatus() {
        return sharedPrefUtil.getEncryptedStringPreference("AIRTAL_UGANDA_STATUS", "");
    }


    //getMobile number format
    public String getMobileNumberFormat() {
        return sharedPrefUtil.getEncryptedStringPreference("MOBILE_NUMBER_FORMAT", "");
    }

    public void setMobileNumberFormat(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MOBILE_NUMBER_FORMAT", value);
    }


    //agent Type
    public String getCountyFlag() {
        return sharedPrefUtil.getEncryptedStringPreference("USER_COUNTY_FLAG", "");
    }

    public void setCountryFlag(String value) {
        sharedPrefUtil.setEncryptedStringPreference("USER_COUNTY_FLAG", value);
    }


    //min and maximum values
    //fund transfer
    public void setFundTransferMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_FUND_TRANSFER_MIN", value);
    }

    public String getFundTransferMin() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_FUND_TRANSFER_MIN", "0");
    }

    public void setFundTransferMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_FUND_TRANSFER_MAX", value);
    }

    public String getFundTransferMax() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_FUND_TRANSFER_MAX", "0");
    }

    //cash out
    public void setCashOutMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_CASH_OUT_MIN", value);
    }

    public String getCashOutMin() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_CASH_OUT_MIN", "0");
    }

    public void setCashOutMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_CASH_OUT_MAX", value);
    }

    public String getCashOutMax() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_CASH_OUT_MAX", "0");
    }

    //cash pickup
    public void setCashPickupMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_CASH_PICKUP_MIN", value);
    }

    public String getCashPickupMin() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_CASH_PICKUP_MIN", "0");
    }

    public void setCashPickupMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_CASH_PICKUP_MAX", value);
    }

    public String getCashPickupMax() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_CASH_PICKUP_MAX", "0");
    }

    //MERCHANT_TRANSFER
    public void setMerchantTransferMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_MERCHANT_TRANSFER_MIN", value);
    }

    public String getMerchantTransferMin() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_MERCHANT_TRANSFER_MIN", "0");
    }

    public void setCMerchantTransferMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_MERCHANT_TRANSFER_MAX", value);
    }

    public String getMerchantTransferMax() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_MERCHANT_TRANSFER_MAX", "0");
    }


    //add fund
    public void setAddFundMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_ADD_FUND_MIN", value);
    }

    public String getAddFundMin() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ADD_FUND_MIN", "0");
    }

    //add fund
    public void setAddFundMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_ADD_FUND_MAX", value);
    }

    public String getAddFundMax() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ADD_FUND_MAX", "0");
    }

    //mpesa min
    public void setMpesaMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MPESA_KENYA_MIN", value);
    }

    public String getMpesaMin() {
        return sharedPrefUtil.getEncryptedStringPreference("MPESA_KENYA_MIN", "0");
    }

    //mpesa max
    public void setMpesaMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MPESA_KENYA_MAX", value);
    }

    public String getMpesaMax() {
        return sharedPrefUtil.getEncryptedStringPreference("MPESA_KENYA_MAX", "0");
    }


    //airtel min
    public void setAirtelMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("AIRTEL_UDANDA_MIN", value);
    }

    public String getAirtelMin() {
        return sharedPrefUtil.getEncryptedStringPreference("AIRTEL_UDANDA_MIN", "0");
    }

    //airtel max
    public void setAirtelMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("AIRTEL_UDANDA_MAX", value);
    }

    public String getAirtelMax() {
        return sharedPrefUtil.getEncryptedStringPreference("AIRTEL_UDANDA_MAX", "0");
    }

    //mtn min
    public void setMTNMin(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MTN_UDANDA_MIN", value);
    }


    //mtn max
    public void setMTNMax(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MTN_UDANDA_MAX", value);
    }


    //set Mpesa Daily Limit
    public void setMpesaDailyLimit(String value) {
        sharedPrefUtil.setEncryptedStringPreference("MPESA_DAILY_LIMIT", value);
    }

    public String getMpesaDailyLimit() {
        return sharedPrefUtil.getEncryptedStringPreference("MPESA_DAILY_LIMIT", "0");
    }


    //airtel daily limit
    public void setAirtelDailyLimit(String value) {
        sharedPrefUtil.setEncryptedStringPreference("AIRTEL_DAILY_LIMIT", value);
    }

    public String getAirtelDailyLimit() {
        return sharedPrefUtil.getEncryptedStringPreference("AIRTEL_DAILY_LIMIT", "0");
    }


    //total number of currency
    public String getTotalCurrency() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_TOTAL_CURRENCY", "0");
    }

    public void setTotalCurrency(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_TOTAL_CURRENCY", value);
    }

    //pre- approved limit
    public String getPreApprovedLimit() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT", "0");
    }

    public void setPreApprovedLimit(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT", accessToken);
    }

    public void setPreApprovedLimitSSP(String limit) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT_SSP", limit);
    }

    public void setPreApprovedLimitUSD(String limit) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT_USD", limit);
    }

    public String getPreApprovedLimitSSP() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT_SSP", "0");
    }

    public String getPreApprovedLimitUSD() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PRE_APPROVED_LIMIT_USD", "0");
    }


    //pre- approved
    public String getPreApproved() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PRE_APPROVED", "");
    }

    public void setPreApproved(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PRE_APPROVED", accessToken);
    }


    //balance
    public String getUserAccountNumber() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_ACCOUNT_NUMBER", "");
    }

    public void setUserAccountNumber(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_ACCOUNT_NUMBER", accessToken);
    }

    //balance
    public String getUserAccountBalance() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_ACCOUNT_BALANCE", "");
    }

    public void setUserAccountBalance(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_ACCOUNT_BALANCE", accessToken);
    }

    //tax id
    public String getTaxId() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_TAX_ID", "");
    }

    public void setTaxId(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_TAX_ID", accessToken);
    }


    //fcm token
    public String getFCMToken() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_FCM_TOKEN", "");
    }

    public void setFCMToken(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_FCM_TOKEN", accessToken);
    }


    //agent Type
    public String getKYCId() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_KYC_ID", "");
    }

    public void setKYCId(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_KYC_ID", value);
    }


    //agent Type
    public String getKYCStatus() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_KYC_STATUS", "");
    }

    public void setKYCStatus(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_KYC_STATUS", value);
    }


    //agent Type
    public String getAgentType() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_AGENT_TYPE", "");
    }

    public void setAgentType(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_AGENT_TYPE", value);
    }


    //Address Line Two
    public String getAddressLineTwo() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ADDRESS_LINE_TWO", "");
    }

    public void setAddressLineTwo(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_ADDRESS_LINE_TWO", value);
    }


    //Address Line One
    public String getAddressLineOne() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ADDRESS_LINE_ONE", "");
    }

    public void setAddressLineOne(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_ADDRESS_LINE_ONE", value);
    }

    //business Person 1
    public String getAuthorizedPersonTwo() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_BUSINESS_AUTHORIZED_PERSON_TWO", "");
    }

    public void setAuthorizedPersonTwo(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_BUSINESS_AUTHORIZED_PERSON_TWO", value);
    }

    //business Person 1
    public String getAuthorizedPersonOne() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_BUSINESS_AUTHORIZED_PERSON_ONE", "");
    }

    public void setAuthorizedPersonOne(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_BUSINESS_AUTHORIZED_PERSON_ONE", value);
    }

    //user business name
    public String getBusinessName() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_BUSINESS_NAME", "");
    }

    public void setBusinessName(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_BUSINESS_NAME", value);
    }

    //user province
    public String getProvince() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PROVINCE", "");
    }

    public void setProvince(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PROVINCE", page);
    }

    //user District
    public String getDistrict() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_DISTRICT", "");
    }

    public void setDistrict(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_DISTRICT", page);
    }

    //user locality
    public String getLocality() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_LOCALITY", "");
    }

    public void setLocality(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_LOCALITY", page);
    }


    //user type
    public String getUserType() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_TYPE", "");
    }

    public void setUserType(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_TYPE", page);
    }


    //page
    public String getPIN() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_PIN", "");
    }

    public void setPIN(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_PIN", page);
    }


    //page
    public String getPage() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_PAGE_NAME", "");
    }

    public void setPage(String page) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_PAGE_NAME", page);
    }

    //user login status
    public boolean getLoginStatus() {
        return sharedPrefUtil.getBooleanPreference("KEY_USER_LOGIN_STATUS", false);

    }

    public void setLoginStatus(boolean loginStatus) {
        sharedPrefUtil.setBooleanPreference("KEY_USER_LOGIN_STATUS", loginStatus);
    }


    //agent notification status
    public boolean getAgentNotificationStatus() {
        return sharedPrefUtil.getBooleanPreference("KEY_AGENT_NOTIFICATION_STATUS", false);

    }

    public void setAgentNotificationStatus(boolean loginStatus) {
        sharedPrefUtil.setBooleanPreference("KEY_AGENT_NOTIFICATION_STATUS", loginStatus);
    }

    //user logout status
    public boolean getLogoutStatus() {
        return sharedPrefUtil.getBooleanPreference("KEY_USER_LOGOUT_STATUS", true);
    }

    public void setLogoutStatus(boolean logoutStatus) {
        sharedPrefUtil.setBooleanPreference("KEY_USER_LOGOUT_STATUS", logoutStatus);
    }

    //show sequrity
    public void setSecurityShownStatus(boolean securityStatus) {
        sharedPrefUtil.setBooleanPreference("KEY_USER_SECURITY_STATUS", securityStatus);
    }

    public boolean getSecurityShownStatus() {
        return sharedPrefUtil.getBooleanPreference("KEY_USER_SECURITY_STATUS", false);

    }

    //finger print enabled
    public boolean isSequrityEnabled() {
        return sharedPrefUtil.getBooleanPreference("KEY_USER_FINGER_PRINT_ENABLED", false);
    }

    public void setSequrityEnabled(boolean fingerPrintEnabled) {
        sharedPrefUtil.setBooleanPreference("KEY_USER_FINGER_PRINT_ENABLED", fingerPrintEnabled);
    }

    //finger print skipped
    public boolean isSkipFingerPrint() {
        return sharedPrefUtil.getBooleanPreference("KEY_USER_FINGER_PRINT_SKIP", false);
    }

    public void setSkipFingerPrint(boolean fingerPrintEnabled) {
        sharedPrefUtil.setBooleanPreference("KEY_USER_FINGER_PRINT_SKIP", fingerPrintEnabled);
    }


    //access token
    public void setAccessToken(String accessToken) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_ACCESS_TOKEN", accessToken);
    }

    public String getAccessToken() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ACCESS_TOKEN", "");
    }


    //user id
    public void setUserId(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_USER_ID", value);
    }

    public String getUserId() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_ID", "");
    }

    //firstname
    public void setFirstName(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_FIRST_NAME", value);
    }

    public String getFirstName() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_FIRST_NAME", "");
    }

    //middle name
    public void setMiddleName(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_MIDDLE_NAME", value);
    }

    public String getMiddleName() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_MIDDLE_NAME", "");
    }

    //last name
    public void setLastName(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_LAST_NAME", value);
    }

    public String getLastName() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_LAST_NAME", "");
    }

    //full name
    public void setFullName(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_FULL_NAME", value);
    }

    public String getFullName() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_FULL_NAME", "");
    }

    //Email
    public void setEmail(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_EMAIL", value);
    }

    public String getEmail() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_EMAIL", "");
    }

    //country code
    public void setCountryCode(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_COUNTRY_CODE", value);
    }

    public String getCountryCode() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_COUNTRY_CODE", "");
    }

    //mobile number
    public void setMobile(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_MOBILE", value);
    }

    public String getMobile() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_MOBILE", "");
    }

    //QR Image
    public void setQRImage(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_QR_IMAGE", value);
    }

    public String getQRImage() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_QR_IMAGE", "");
    }

    //QR Code
    public void setQRCode(String value) {
        sharedPrefUtil.setEncryptedStringPreference("KEY_QRCODE", value);
    }

    public String getQRCode() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_QRCODE", "");
    }


    //Notification count
    public void setNotificationCount(String value) {
        sharedPrefUtil.setBooleanPreference("KEY_NOTIFICATION_COUNT", !value.equals("") && !value.equals("0"));
    }

    public Boolean getNotificationCount() {
        return sharedPrefUtil.getBooleanPreference("KEY_NOTIFICATION_COUNT", false);
    }

    //profile image
    public void setProfileImage(String value) {
        if (value == null) {
            sharedPrefUtil.setEncryptedStringPreference("KEY_PROFILE_IMAGE", "");
        } else {
            sharedPrefUtil.setEncryptedStringPreference("KEY_PROFILE_IMAGE", value);
        }

    }

    public String getProfileImage() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_PROFILE_IMAGE", "");
    }

    //get total commission
    public void setTotalCommission(String value) {
        sharedPrefUtil.setStringPreference("KEY_TOTAL_COMMISSION", value);
    }

    public String getTotalCommission() {
        return sharedPrefUtil.getStringPreference("KEY_TOTAL_COMMISSION", "0");
    }


    //finger print skipped
    public boolean isShownLanguageSelection() {
        return sharedPrefUtil.getBooleanPreference("KEY_LAN_SELECTION_SCREEN", false);
    }

    public void setShownLanguageSelection(boolean value) {
        sharedPrefUtil.setBooleanPreference("KEY_LAN_SELECTION_SCREEN", value);
    }

    //Default Currency
    public void setCurrencyID(String value) {
        sharedPrefUtil.setStringPreference("KEY_CURRENCY_ID", value);
    }

    public String getCurrencyID() {
        return sharedPrefUtil.getStringPreference("KEY_CURRENCY_ID", "1");
    }


    //Store api response


    //store commission list
    public void setCommissionList(String value) {
        sharedPrefUtil.setStringPreference("KEY_ALL_COMMISSION", value);
    }

    public String getCommissionList() {
        return sharedPrefUtil.getStringPreference("KEY_ALL_COMMISSION", "");
    }


    //store basic details
    public void setBasicDetails(String value) {
        sharedPrefUtil.setStringPreference("KEY_BASIC_DETAILS", value);
    }

    public String getBasicDetails() {
        return sharedPrefUtil.getStringPreference("KEY_BASIC_DETAILS", "");
    }

    //store home transaction
    public void setHomeTransactionDetails(String value) {
        sharedPrefUtil.setStringPreference("KEY_HOME_TRANSACTION_DETAILS", value);
    }

    public String getHomeTransactionDetails() {
        return sharedPrefUtil.getStringPreference("KEY_HOME_TRANSACTION_DETAILS", "");
    }

    //store account transaction
    public void setAccountTransactionDetails(String value) {
        sharedPrefUtil.setStringPreference("KEY_ACCOUNT_TRANSACTION_DETAILS", value);
    }

    public String getAccountTransactionDetails() {
        return sharedPrefUtil.getStringPreference("KEY_ACCOUNT_TRANSACTION_DETAILS", "");
    }

    //store all transaction
    public void setAllTransactions(String value) {
        sharedPrefUtil.setStringPreference("KEY_ALL_TRANSACTIONS", value);
    }

    public String getAllTransactions() {
        return sharedPrefUtil.getStringPreference("KEY_ALL_TRANSACTIONS", "");
    }

    //store all transaction agent
    public void setAllTransactionsAgent(String value) {
        sharedPrefUtil.setStringPreference("KEY_ALL_TRANSACTIONS_AGENT", value);
    }

    public String getAllTransactionsAgent() {
        return sharedPrefUtil.getStringPreference("KEY_ALL_TRANSACTIONS_AGENT", "");
    }


    //store in transaction
    public void setInTransactions(String value) {
        sharedPrefUtil.setStringPreference("KEY_IN_TRANSACTIONS", value);
    }

    public String getInTransactions() {
        return sharedPrefUtil.getStringPreference("KEY_IN_TRANSACTIONS", "");
    }


    //store out transaction
    public void setOutTransactions(String value) {
        sharedPrefUtil.setStringPreference("KEY_OUT_TRANSACTIONS", value);
    }

    public String getOutTransactions() {
        return sharedPrefUtil.getStringPreference("KEY_OUT_TRANSACTIONS", "");
    }


    //agent dash board
    public void setAgentDashBoard(String value) {
        sharedPrefUtil.setStringPreference("KEY_AGENT_DASH_BOARD", value);
    }

    public String getAgentDashBoard() {
        return sharedPrefUtil.getStringPreference("KEY_AGENT_DASH_BOARD", "");
    }


    //get all notifications
    public void setNotifications(String value) {
        sharedPrefUtil.setStringPreference("KEY_ALL_NOTIFICATIONS", value);
    }

    public String getNotifications() {
        return sharedPrefUtil.getStringPreference("KEY_ALL_NOTIFICATIONS", "");
    }

    //purpose list
    public void setPurposeListCashPickup(String value) {
        sharedPrefUtil.setStringPreference("KEY_PURPOSE_LIST_FOR_CASH_PICKUP", value);
    }

    public String getPurposeListCashPickup() {
        return sharedPrefUtil.getStringPreference("KEY_PURPOSE_LIST_FOR_CASH_PICKUP", "");
    }

    //my currency list
    public void setMyCurrencyList(String value) {
        sharedPrefUtil.setStringPreference("KEY_MY_CURRENCY_LIST", value);
    }

    public String getMyCurrencyList() {
        return sharedPrefUtil.getStringPreference("KEY_MY_CURRENCY_LIST", "");
    }

    //cash pickup transactions - agent tab
    public void setCashPickupTabList(String value) {
        sharedPrefUtil.setStringPreference("KEY_ALL_CASH_PICKUP_TRANSACTIONS_AGENT", value);
    }

    public String getCashPickupTabList() {
        return sharedPrefUtil.getStringPreference("KEY_ALL_CASH_PICKUP_TRANSACTIONS_AGENT", "");
    }

    //contact list
    public void setContactList(String value) {
        sharedPrefUtil.setStringPreference("KEY_CONTACT_LIST", value);
    }

    public String getContactList() {
        return sharedPrefUtil.getStringPreference("KEY_CONTACT_LIST", "");
    }


    //get & set all notifications
    public void setCountryLists(String value) {
        sharedPrefUtil.setStringPreference("KEY_COUNTRY_LIST", value);
    }

    public String getCountryLists() {
        return sharedPrefUtil.getStringPreference("KEY_COUNTRY_LIST", "");
    }


    public void clearLocalStorage() {
        setBasicDetails("");
        setHomeTransactionDetails("");
        setAccountTransactionDetails("");
        setAllTransactions("");
        setInTransactions("");
        setOutTransactions("");
        setAgentDashBoard("");
        setAllTransactionsAgent("");
        setNotifications("");
        setCommissionList("");
        setCashPickupTabList("");
        setMyCurrencyList("");
    }


    //**-----------------Local Storage END-----------------**//

    public String generateFullName(String firstName, String middleName, String lastName) {
        String fullName;
        if (middleName == null) {
            middleName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        if (middleName.equals("")) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = firstName + " " + middleName + " " + lastName;
        }
        return fullName;
    }


    //=====================================Validations=============================================================================================================================

    //validate mobile number
    public boolean validateMobileNumber(String mobileNumber) {
        return mobileNumber.matches("[0-9]+") && mobileNumber.length() == SthiramValues.MOBILE_NUMBER_DIGIT;
    }

    //validate mobile number
    public boolean validateMobileNumberDynamic(String mobileNumber, String mobileNumberLength) {
        int length = Integer.parseInt(mobileNumberLength);
        return mobileNumber.matches("[0-9]+") && mobileNumber.length() == length;
    }

    //validate first name

    public boolean validateFistName(String name) {
        if (name.equals("")) {
            return false;
        } else return name.matches("^[A-Za-z ]+$") && name.length() > 1;
    }


    //validate middle name
    public boolean validateMiddleName(String name) {
        if (name.equals("")) {
            return true;
        } else return name.matches("^[A-Za-z ]+$") && name.length() > 1;
    }

    //validate last name
    public boolean validateLastName(String name) {
        if (name.equals("")) {
            return false;
        } else return name.matches("^[A-Za-z ]+$") && name.length() > 1;
    }


    //validate DOB
    public boolean validateDOB(String dob) {
        if (dob.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date d1 = sdf.parse("01/01/1910");
                Date d2 = sdf.parse(dob);
                String minAge = getMinAge(System.currentTimeMillis());
                Date d3 = sdf.parse(minAge);
                assert d2 != null;
                if (d2.compareTo(d1) >= 0) {
                    return d2.compareTo(d3) < 0;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        } else {
            return false;
        }
    }

    //validate DOB
    public boolean validateExpiryDate(String expiryDate) {
        if (expiryDate.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date d1 = sdf.parse(getTodayDate(System.currentTimeMillis()));
                Date d2 = sdf.parse(expiryDate);
                Date d3 = sdf.parse(getTodayDateAdd10Years());
                assert d2 != null;
                if (d2.compareTo(d1) >= 0) {
                    return d2.compareTo(d3) < 0;
                } else {
                    return false;
                }
            } catch (Exception e) {

                return false;
            }
        } else {
            return false;
        }
    }


    public String getMinAge(Long milliSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds - 568025136000L);
        return formatter.format(calendar.getTime());
    }

    public String getTodayDate(Long milliSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public String getTodayDateAdd10Years() {
        Calendar cmin = Calendar.getInstance();
        int maxy = cmin.get(Calendar.YEAR) + 20;
        int maxm = cmin.get(Calendar.MONTH) + 1;
        int maxd = cmin.get(Calendar.DAY_OF_MONTH);
        return maxd + "/" + maxm + "/" + maxy;
    }

    public String dobFormat(String value) {
        String datenew = null;
        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        try {
            Date oneWayTripDate = input.parse(value);
            assert oneWayTripDate != null;
            datenew = output.format(oneWayTripDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datenew;
    }

    public String dobFormattwo(String value) {
        String datenew = null;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        try {
            Date oneWayTripDate = input.parse(value);
            assert oneWayTripDate != null;
            datenew = output.format(oneWayTripDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datenew;
    }

    //validate email
    public boolean isEmailValid(String email) {
        if (email.equals("")) {
            return false;
        } else {
            String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }

    }

    //validate email
    public boolean isEmailValidOptional(String email) {
        if (email.equals("")) {
            return true;
        } else {
            String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }

    }

    //validate business name
    public boolean validateBusinessName(String name) {


        if (name.equals("")) {
            return false;
        } else return name.matches("^[A-Za-z .-]+$") && name.length() > 1;

    }


    //validate business name
    public boolean validateAuthorizedPersonOne(String name) {
        if (name.equals("")) {
            return false;
        } else return name.matches("^[A-Za-z ]+$") && name.length() > 1;
    }

    //validate business name
    public boolean validateAuthorizedPersonTwo(String name) {
        if (name.equals("")) {
            return true;
        } else return name.matches("^[A-Za-z ]+$") && name.length() > 1;
    }

    //validate business name
    public boolean validateRemark(String value) {
        if (value.equals("")) {
            return true;
        } else return value.matches("[a-zA-Z 0-9]+");
    }


    public boolean validateTaxId(String name) {
        if (name.equals("")) {
            return false;
        } else return name.matches("^[A-Za-z0-9]+$") && name.length() >= 5 && name.length() <= 11;
    }


    public boolean validateAddressLine1(String value) {
        if (value.matches("[#.0-9a-zA-Z\\s,-]*") && value.length() > 1) {
            return !value.matches("[0-9]+");
        } else {
            return false;
        }

    }

    public boolean validateKYCIDNumber(String name) {
        if (name.equals("")) {
            return false;
        } else return name.matches("[a-zA-Z 0-9]+") && name.length() >= 5 && name.length() <= 11;
    }

    public boolean validateStudentIDNumber(String value) {
        if (value.equals("")) {
            return false;
        } else return value.matches("[a-zA-Z 0-9]+");
    }


    public boolean validateAddressLine2(String value) {
        if (value.equals("")) {
            return true;
        } else if (value.matches("[#.0-9a-zA-Z\\s,-]*")) {
            return !value.matches("[0-9]+");
        } else {
            return false;
        }
    }

    public boolean validateLocality(String value) {
        if (value.matches("[#.0-9a-zA-Z\\s,-]*") && value.length() > 1) {
            return !value.matches("[0-9]+");
        } else {
            return false;
        }
    }


    public boolean validateReffNumber(String value) {
        if (value.equals("")) {
            return true;
        } else return value.matches("[a-zA-Z 0-9]+");
    }

    //amount validation before a paytment
    public boolean validAmount(String value) {
        boolean validAmount = false;
        try {
            if (!value.equals("")) {
                double amount = Double.parseDouble(value);
                validAmount = amount >= SthiramValues.MINIMUM_TRANSACTION_AMOUNT && amount <= SthiramValues.MAXIMUM_TRANSACTION_AMOUNT;
            }
        } catch (Exception ignored) {
        }
        return validAmount;
    }


    public String maximumTransactionLimitMessage(double v) {
        String formattedAmount = setAmount(v + "");
        String message;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals(SthiramValues.ARABIC_LANGUAGE_ID)) {
            message = " لا يمكن تجاوز مبلغ المعاملة عن " + SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount;
        } else if (SthiramValues.SELECTED_LANGUAGE_ID.equals(SthiramValues.TURKISH_LANGUAGE_ID)) {
            message = "İşlem tutarı " + formattedAmount + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL + "'yi aşamaz";
        } else {
            message = "Transaction amount cannot exceed " + SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount;
        }
        return message;
    }

    public String minimumTransactionLimitMessage(double v) {
        String formattedAmount = setAmount(v + "");
        String message;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            message = " يجب أن يكون مبلغ المعاملة أقل من " + SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount;
        } else if (SthiramValues.SELECTED_LANGUAGE_ID.equals("3")) {
            message = "İşlem tutarı en az " + formattedAmount + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL + " olmalıdır";
        } else {
            message = "Transaction amount must be at least " + SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount;
        }
        return message;
    }


    public String checkTransactionIsPossible(String totalAmount, String enteredAmount, String type) {
        double min;
        double max;
        if (type.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT)) {
            //yeel to yeel- USer
            min = Double.parseDouble(getFundTransferMin());
            max = Double.parseDouble(getFundTransferMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
            //cash out- User
            min = Double.parseDouble(getCashOutMin());
            max = Double.parseDouble(getCashOutMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)) {
            //cash pickup- User
            min = Double.parseDouble(getCashPickupMin());
            max = Double.parseDouble(getCashPickupMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)) {
            //yeel to yeel- Agent
            min = Double.parseDouble(getMerchantTransferMin());
            max = Double.parseDouble(getMerchantTransferMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS)) {
            //bill payments- User
            min = Double.parseDouble(SthiramValues.MINIMUM_TRANSACTION_AMOUNT + "");
            max = Double.parseDouble(SthiramValues.MAXIMUM_TRANSACTION_AMOUNT + "");
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_MPESA)) {
            //mpesa- User & Agent
            min = Double.parseDouble(getMpesaMin());
            max = Double.parseDouble(getMpesaMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA)) {
            //Airtel -Uganda user & Agent
            min = Double.parseDouble(getAirtelMin());
            max = Double.parseDouble(getAirtelMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL)) {
            //send via yeel agent side
            min = Double.parseDouble(getFundTransferMin());
            max = Double.parseDouble(getFundTransferMax());
        } else if (type.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)) {
            //agent cash pickup
            min = Double.parseDouble(getCashPickupMin());
            max = Double.parseDouble(getCashPickupMax());
        } else {
            //others
            min = Double.parseDouble(SthiramValues.MINIMUM_TRANSACTION_AMOUNT + "");
            max = Double.parseDouble(SthiramValues.MAXIMUM_TRANSACTION_AMOUNT + "");

            logAValue("Type Status", "Type not added yet");
        }
        logAValue("Transaction Type", type);

        String validationMessage = "";
        String kycStatus = getKYCStatus();
        double runningBalance = Double.parseDouble(getUserAccountBalance());
        /*  try {*/
        if (!totalAmount.equals("")) {
            double amount = Double.parseDouble(totalAmount);
            if (amount != 0) {
                if (amount >= min) {
                    if (amount <= max) {
                        if (amount <= Double.parseDouble(getPreApprovedLimit())
                                && getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)
                                && (getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_YES) || getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_UPGRADED))
                        ) {
                            if (runningBalance >= amount) {
                                if (kycStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
                                    //rejected
                                    validationMessage = context.getString(R.string.kyc_rejected_message);
                                    kycErrorDialogue(validationMessage, context.getString(R.string.submit_again));
                                } else if (kycStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)) {
                                    //deactivated
                                    validationMessage = context.getString(R.string.kyc_deactivated_message);
                                    ShowOtherErrors(validationMessage);
                                } else if (kycStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
                                    //expired
                                    validationMessage = context.getString(R.string.kyc_expired_message);
                                    kycErrorDialogue(validationMessage, context.getString(R.string.upload));
                                } else {
                                    validationMessage = SthiramValues.SUCCESS;
                                }

                            } else {
                                validationMessage = context.getString(R.string.insufficient_balance);
                                ShowOtherErrors(validationMessage);
                            }

                        } else {
                            if (kycStatus.equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)) {
                                //not yet submitted
                                validationMessage = context.getString(R.string.not_yet_submitted_message);
                                ShowOtherErrors(validationMessage);
                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_PRE_APPROVED)) {
                                validationMessage = "";
                                kycErrorDialogueForPreApproved();
                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_SUBMITTED)) {
                                //submitted
                                validationMessage = context.getString(R.string.kyc_submitted_message);
                                ShowOtherErrors(validationMessage);
                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
                                //rejected
                                validationMessage = context.getString(R.string.kyc_rejected_message);
                                kycErrorDialogue(validationMessage, context.getString(R.string.submit_again));
                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)) {
                                //deactivated
                                validationMessage = context.getString(R.string.kyc_deactivated_message);
                                ShowOtherErrors(validationMessage);
                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
                                //expired
                                validationMessage = context.getString(R.string.kyc_expired_message);
                                kycErrorDialogue(validationMessage, context.getString(R.string.upload));

                            } else if (kycStatus.equals(SthiramValues.KYC_STATUS_APPROVED)) {
                                if (runningBalance >= amount) {
                                    validationMessage = SthiramValues.SUCCESS;
                                } else {
                                    validationMessage = context.getString(R.string.insufficient_balance);
                                    ShowOtherErrors(validationMessage);
                                }
                            }
                        }
                    } else {
                        validationMessage = maximumTransactionLimitMessage(max);
                        ShowOtherErrors(validationMessage);
                    }
                } else {
                    validationMessage = minimumTransactionLimitMessage(min);
                    ShowOtherErrors(validationMessage);
                }
            } else {
                validationMessage = minimumTransactionLimitMessage(min);
                ShowOtherErrors(validationMessage);
            }
        } else {
            Log.e("Error", "1");
            validationMessage = context.getString(R.string.enter_valid_amount);
            ShowOtherErrors(validationMessage);
        }
        return validationMessage;
    }

    public String checkExchangePossible(){
        String validationMessage = "";
        String kycStatus = getKYCStatus();
        if (kycStatus.equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)) {
            //not yet submitted
            validationMessage = context.getString(R.string.not_yet_submitted_message);
            ShowOtherErrors(validationMessage);
        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_PRE_APPROVED)) {
            validationMessage = "";
            kycErrorDialogueForPreApprovedExchange();
        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_SUBMITTED)) {
            //submitted
            validationMessage = context.getString(R.string.kyc_submitted_message);
            ShowOtherErrors(validationMessage);
        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
            //rejected
            validationMessage = context.getString(R.string.kyc_rejected_message);
            kycErrorDialogue(validationMessage, context.getString(R.string.submit_again));
        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)) {
            //deactivated
            validationMessage = context.getString(R.string.kyc_deactivated_message);
            ShowOtherErrors(validationMessage);
        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
            //expired
            validationMessage = context.getString(R.string.kyc_expired_message);
            kycErrorDialogue(validationMessage, context.getString(R.string.upload));

        } else if (kycStatus.equals(SthiramValues.KYC_STATUS_APPROVED)) {
            validationMessage = SthiramValues.SUCCESS;
        }
        return  validationMessage;
    }

    public String checkAddFundPossible(String totalAmount, String enteredAmount, String type) {
        double min;
        double max;
        if (type.equals(SthiramValues.TRANSACTION_TYPE_ADD_FUND)) {
            min = Double.parseDouble(getAddFundMin());
            max = Double.parseDouble(getAddFundMax());
        } else {
            min = Double.parseDouble(SthiramValues.MINIMUM_TRANSACTION_AMOUNT + "");
            max = Double.parseDouble(SthiramValues.MAXIMUM_TRANSACTION_AMOUNT + "");
        }
        String validationMessage;
        try {
            if (!totalAmount.equals("")) {
                double amount = Double.parseDouble(totalAmount);
                if (amount != 0) {
                    if (amount >= min) {
                        if (amount <= max) {
                            validationMessage = SthiramValues.SUCCESS;
                        } else {
                            validationMessage = maximumTransactionLimitMessage(max);
                            ShowOtherErrors(validationMessage);
                        }
                    } else {
                        validationMessage = minimumTransactionLimitMessage(min);
                        ShowOtherErrors(validationMessage);
                    }
                } else {
                    validationMessage = minimumTransactionLimitMessage(min);
                    ShowOtherErrors(validationMessage);
                }
            } else {
                validationMessage = context.getString(R.string.enter_valid_amount);
                ShowOtherErrors(validationMessage);
            }
        } catch (Exception e) {
            validationMessage = context.getString(R.string.enter_valid_amount);
            ShowOtherErrors(validationMessage);
        }
        return validationMessage;
    }


    @SuppressLint("SetTextI18n")
    private void kycErrorDialogueForPreApproved() {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_verification);
        TextView btnNow = success.findViewById(R.id.text_now);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        TextView btnLater = success.findViewById(R.id.text_later);
        String formattedAmount = setAmount(getPreApprovedLimit());
        textLimitAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount);
        btnLater.setOnClickListener(v -> success.dismiss());
        btnNow.setOnClickListener(v -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            context.startActivity(in);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }


    @SuppressLint("SetTextI18n")
    private void kycErrorDialogueForPreApprovedExchange() {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_verification);
        TextView btnNow = success.findViewById(R.id.text_now);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        textLimitAmount.setVisibility(View.GONE);
        TextView tvMessage=success.findViewById(R.id.tvMessage);
        tvMessage.setText(R.string.exchnage_not_possible_for_pre_approved_user);
        TextView btnLater = success.findViewById(R.id.text_later);
        btnLater.setOnClickListener(v -> success.dismiss());
        btnNow.setOnClickListener(v -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            context.startActivity(in);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    @SuppressLint("SetTextI18n")
    private void kycErrorDialogue(String message, String buttonLabel) {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_validation_design);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        String formattedAmount = setAmount(getPreApprovedLimit());
        textLimitAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount);
        TextView tvMessage = success.findViewById(R.id.tv_message);
        TextView tvCancel = success.findViewById(R.id.tv_cancel);
        TextView tvOk = success.findViewById(R.id.tv_ok);
        tvMessage.setText(message);
        tvCancel.setText(context.getString(R.string.skip));
        tvOk.setText(buttonLabel);
        tvCancel.setOnClickListener(v -> success.dismiss());
        tvOk.setOnClickListener(view -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            context.startActivity(in);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    private void ShowOtherErrors(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public double calculateCommission(String feesType, String amount, String feesValue) {
        double calculatedFees = 0.0;
        double mainAmount;
        try {
            mainAmount = Double.parseDouble(amount);
            if (feesType.equals("PERC")) {
                calculatedFees = (mainAmount * Double.parseDouble(feesValue)) / 100;
            } else {
                calculatedFees = Double.parseDouble(feesValue);
            }
        } catch (Exception ignored) {

        }
        return calculatedFees;
    }

    public String setAmount(String amount) {
        try {
            double value = Double.parseDouble(amount + "");
            BigDecimal credit = new BigDecimal(value);
            BigDecimal roundOffC = credit.setScale(2, RoundingMode.HALF_EVEN);
            String s = NumberFormat.getNumberInstance(Locale.US).format(roundOffC);
            if (s.contains(".")) {
                String[] y = s.split("\\.");
                if (y[1].length() == 1) {
                    s = s + "0";
                }
            } else {
                s = s + ".00";
            }
            return s;

        } catch (NumberFormatException nme) {
            return "0.00";
        }

    }




    public String formatAMobileNumber(String mobileNumber, String countyCode, String pattern) {
        StringBuilder phone = new StringBuilder(mobileNumber);
        int count = mobileNumber.length();
        for (int i = 0; i < count; i++) {
            char c = pattern.charAt(i);
            if ((c != 'X') && (c != mobileNumber.charAt(i))) {
                phone.insert(i, c);
            }
        }
        String finalMobileNumber = phone.toString();
        finalMobileNumber = countyCode + " " + finalMobileNumber;
        return finalMobileNumber;
    }

    public void setLastContactSyncDate(String date) {
        sharedPrefUtil.setEncryptedStringPreference("CONTACT_SYNC_DATE", date);
    }

    public String getLastContactSyncDate() {
        return sharedPrefUtil.getEncryptedStringPreference("CONTACT_SYNC_DATE", "");
    }

    public boolean lastSyncDateStatus(String strDate) {
        boolean status = false;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date todayDate = sdf.parse(strDate);
            Date lastUpdateDate = null;
            if (!getLastContactSyncDate().equals("")) {
                lastUpdateDate = sdf.parse(getLastContactSyncDate());
                assert todayDate != null;
                assert lastUpdateDate != null;
                if (todayDate.getTime() > lastUpdateDate.getTime()) {
                    status = true;
                }
            } else {
                status = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    public String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();
        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString().replaceAll("  ", " ");
    }

    public String getGalilioDateFormat(String value, String type) {
        String galilioTime;
        if (type.equals("time")) {
            galilioTime = dateWithTimeFormat(getGalilioTimeZoneFormat(value));
        } else if (type.equals("date2")) {
            galilioTime = dateOnlyFormatTwo(getGalilioTimeZoneFormat(value));
        } else {
            galilioTime = dateOnlyFormat(getGalilioTimeZoneFormat(value));
        }
        return galilioTime;
    }

    //time zone format
    public String getGalilioTimeZoneFormat(String value) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(value);
            df.setTimeZone(TimeZone.getDefault());
            assert date != null;
            formattedDate = df.format(date);
        } catch (Exception ignored) {
        }
        return formattedDate;
    }

    //date only format
    //dateFormat
    public String dateOnlyFormat(String value) {
        String datenew = null;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        try {
            Date oneWayTripDate = input.parse(value);
            assert oneWayTripDate != null;
            datenew = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datenew;
    }

    //dateFormat
    public String dateOnlyFormatTwo(String value) {
        String datenew = null;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd, MMMM yyyy", Locale.US);
        try {
            Date oneWayTripDate = input.parse(value);
            assert oneWayTripDate != null;
            datenew = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datenew;
    }

    //date with time format
    public String dateWithTimeFormat(String value) {
        String datenew = null;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy, hh:mm aaa", Locale.US);
        try {
            Date oneWayTripDate = input.parse(value);
            assert oneWayTripDate != null;
            datenew = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datenew;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public String dateDifference() {
        try {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date d1 = sdf.parse(currentYear + "-01-01");
            Date d2 = sdf.parse(getDateFromMilliseconds(System.currentTimeMillis()));
            assert d2 != null;
            assert d1 != null;
            long diff = (d2.getTime() - d1.getTime()) + 1;
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            return numOfDays + "";
        } catch (ParseException pe) {
            return "";
        }
    }

    public String getDateFromMilliseconds(Long milliSeconds) {
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(milliSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void logAValue(String label, String values) {
        Log.e(label, values);
    }

    public void toastOTP(String values) {
        if (values != null) {
            Toast.makeText(context, values, Toast.LENGTH_LONG).show();
        }
    }

    public void loginAndLogoutStatus() {
        if (getLoginStatus()) {
            //Log.e("Login", "Yes");
        } else {
            // Log.e("Login", "No");
        }

        if (getLogoutStatus()) {
            // Log.e("Logout", "Yes");
        } else {
            // Log.e("Logout", "No");
        }

    }

    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public String getAgentNameWithFullAddress(AgentData agentData) {
        String address;
        String name;
        if (agentData.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
            address = agentData.getAddressline1() + ", " + agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            name = generateFullName(agentData.getFirstname(), agentData.getMiddlename(), agentData.getLastname());

        } else {
            address = agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            name = agentData.getBusiness_name();
        }
        return name + ", " + address;
    }

    public String getFullAddressOfAUser(UserDetailsData userDetailsData) {
        String fullAddress;
        if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            if (userDetailsData.getAddressline2() != null && !userDetailsData.getAddressline2().equals("")) {
                fullAddress = userDetailsData.getAddressline1() + ", " + userDetailsData.getAddressline2();
            } else {
                fullAddress = userDetailsData.getAddressline1();
            }
            fullAddress = fullAddress + "," + userDetailsData.getLocality() + ", " + userDetailsData.getProvince() + ", " + userDetailsData.getDistrict();
        } else {
            if (userDetailsData.getLocality() != null && !userDetailsData.getLocality().equals("")) {
                fullAddress = userDetailsData.getLocality() + ", " + userDetailsData.getProvince() + ", " + userDetailsData.getDistrict();
            } else {
                fullAddress = "";
            }

        }
        return fullAddress;
    }

    public String getFullName(UserDetailsData userDetailsData) {
        String username = "";
        if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
            if (userDetailsData.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                username = generateFullName(userDetailsData.getFirstname(), userDetailsData.getMiddlename(), userDetailsData.getLastname());
            } else {
                username = userDetailsData.getBusiness_name();
            }
        } else if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            username = generateFullName(userDetailsData.getFirstname(), userDetailsData.getMiddlename(), userDetailsData.getLastname());
        } else if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            username = userDetailsData.getBusiness_name();
        }
        return username;
    }


    public String getReceiverAccountNumberFormCurrencyList(List<CurrencyListData> currencyList) {
        String receiverAccountNumber = "";
        if (currencyList != null && currencyList.size() != 0) {
            for (int i = 0; i < currencyList.size(); i++) {
                if (getCurrencyID().equals(currencyList.get(i).getCurrency_id())) {
                    receiverAccountNumber = currencyList.get(i).getAccount_number();
                    break;
                }
            }
        } else {
            receiverAccountNumber = "";
        }
        return receiverAccountNumber;
    }

    public String generateFullNameFromUserDetails(UserDetailsData receiverData) {
        String receiverName = "";
        if (receiverData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
            if (receiverData.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                receiverName = generateFullName(receiverData.getFirstname(), receiverData.getMiddlename(), receiverData.getLastname());
            } else {
                receiverName = receiverData.getBusiness_name();
            }
        } else if (receiverData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            receiverName = generateFullName(receiverData.getFirstname(), receiverData.getMiddlename(), receiverData.getLastname());
        } else if (receiverData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            receiverName = receiverData.getBusiness_name();
        }
        return receiverName;
    }

    public String getBeneficiaryFullAddress(BeneficiaryData beneficiaryData, String receiverMobileNumber) {
        String fullAddress;
        String fullName = generateFullName(beneficiaryData.getFirstname(), beneficiaryData.getMiddleName(), beneficiaryData.getLastname());
        String senderEmail;
        if (beneficiaryData.getEmail() == null || beneficiaryData.getEmail().isEmpty()) {
            senderEmail = "";
        } else {
            senderEmail = ", " + beneficiaryData.getEmail();
        }
        fullAddress = fullName + ", " + receiverMobileNumber + senderEmail;
        return fullAddress;
    }


    public String getFullDetailsOfAUser(String receiverName, String receiverMobileNumber, String receiverEmail, String receiverFullAddress) {
        String fullAddress;
        fullAddress = receiverName;
        if (receiverMobileNumber != null && !receiverMobileNumber.equals("")) {
            fullAddress = fullAddress + ",\n" + receiverMobileNumber;
        }
        if (receiverEmail != null && !receiverEmail.equals("")) {
            fullAddress = fullAddress + ", " + receiverEmail;
        }
        if (receiverFullAddress != null && !receiverFullAddress.equals("")) {
            fullAddress = fullAddress + ", " + receiverFullAddress;
        }
        return fullAddress;
    }

    public boolean checkUpdateAvailable(ForceUpdateData forceUpdateData, int versionCode) {
        boolean updateAvailable = false;
        int newVersionCode;
        if (forceUpdateData.getVersionCode() != null && !forceUpdateData.getVersionCode().equals("")) {
            newVersionCode = Integer.parseInt(forceUpdateData.getVersionCode());
        } else {
            newVersionCode = 0;
        }
        if (newVersionCode != 0) {
            if (versionCode < newVersionCode) {
                updateAvailable = true;
            }
        }
        return updateAvailable;
    }
    private void setLocale(String lang) {
        logAValue("Language", lang);
        LocaleUtils.initialize(context, lang);
    }
}
