package com.yeel.drc.api;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yeel.drc.api.countylist.CountryListData;
import com.yeel.drc.api.countylist.CountryListResponse;
import com.yeel.drc.dialogboxes.CheckInternetConnectionDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.ServerMaintenanceDialog;
import com.yeel.drc.dialogboxes.SessionExpiredDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.utils.ByteArrays;
import com.yeel.drc.utils.ByteArraysThree;
import com.yeel.drc.utils.ByteArraysTwo;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.CustomDialogProgress;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.SharedPrefUtil;

import org.jetbrains.annotations.NotNull;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APICall {
    Context context;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    CustomDialogProgress customDialogProgress;
    CheckInternetConnectionDialog checkInternetConnectionDialog;
    ServerMaintenanceDialog serverMaintenanceDialog;
    SessionExpiredDialog sessionExpiredDialog;
    MultipleLoginDialog multipleLoginDialog;
    SharedPrefUtil sharedPrefUtil;

    public APICall(Context context, String type) {
        this.context = context;
        dialogProgress = new DialogProgress(context);
        customDialogProgress = new CustomDialogProgress(context);
        sharedPrefUtil = new SharedPrefUtil(context);

        checkInternetConnectionDialog = new CheckInternetConnectionDialog(context, type);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, type);

        serverMaintenanceDialog = new ServerMaintenanceDialog(context, SthiramValues.finish);
        sessionExpiredDialog = new SessionExpiredDialog(context, SthiramValues.finish);
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
    }

    //force update
    public void closeAccount(Boolean retry, Boolean progress, String userId, CustomCallback customCallback) {
        try {
            String apiName = "deactivateUser";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //force update
    public void forceUpdate(Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "forceUpdate";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "android");
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.FORCE_UPDATE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Send Mobile OTP From sign UP Flow
    public void sendOTP(String type, Boolean retry, String devicetoken, String mobileNumber, String countyCode, String otp_id, Boolean progress, CustomCallback customCallback) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("country_code", countyCode);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("otp_id", otp_id);
            jsonObject.addProperty("devicetoken", devicetoken);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            String url;
            String apiName;
            if (type.equals("SMS")) {
                apiName = "sendOtp";
                url = SthiramValues.APP_BASE_URL;
            } else {
                apiName = "sendWhatsAppOtp";
                url = SthiramValues.ANONYMOUS_URL;
            }
            callAPI(0, retry, jsonObject, apiName, progress, url, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Mobile OTP VERIFICATION
    public void otpVerification(Boolean retry, String mobileNumber, String countyCode, String otpId, String otp, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "verifyOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("otp", otp);
            jsonObject.addProperty("otp_id", otpId);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("country_code", countyCode);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Registration First Step
    public void userSignUp(Boolean retry,
                           String currencyId,
                           String firstname, String middlename, String lastname, String mobile,
                           String email, String dob,
                           String user_type, String locality,
                           String district, String province,
                           String business_name, String authorized_person1,
                           String authorized_person2, String tax_id,
                           String addressline1, String addressline2,
                           String agentType,
                           String companyRegNumber,
                           String country_code,
                           Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "userSignUp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("currency_id", currencyId);
            jsonObject.addProperty("firstname", firstname);
            jsonObject.addProperty("middlename", middlename);
            jsonObject.addProperty("lastname", lastname);
            jsonObject.addProperty("country_code", country_code);
            jsonObject.addProperty("mobile", mobile);
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("dob", dob);
            jsonObject.addProperty("user_type", user_type);
            jsonObject.addProperty("locality", locality);
            jsonObject.addProperty("district", district);
            jsonObject.addProperty("province", province);
            jsonObject.addProperty("business_name", business_name);
            jsonObject.addProperty("authorized_person1", authorized_person1);
            jsonObject.addProperty("authorized_person2", authorized_person2);
            jsonObject.addProperty("tax_id", tax_id);
            jsonObject.addProperty("addressline1", addressline1);
            jsonObject.addProperty("addressline2", addressline2);
            jsonObject.addProperty("agent_type", agentType);
            jsonObject.addProperty("company_reg_no", companyRegNumber);
            jsonObject.addProperty("iat", System.currentTimeMillis());


            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //Update Profile
    public void updateProfile(Boolean retry, String userId, String firstname, String middlename, String lastname, String mobile, String countyCode,
                              String email, String dob,
                              String user_type, String locality,
                              String district, String province,
                              String business_name, String authorized_person1,
                              String authorized_person2, String tax_id,
                              String addressline1, String addressline2,
                              String agentType,
                              Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "updateProfile";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("firstname", firstname);
            jsonObject.addProperty("middlename", middlename);
            jsonObject.addProperty("lastname", lastname);
            jsonObject.addProperty("country_code", countyCode);
            jsonObject.addProperty("mobile", mobile);
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("dob", dob);
            jsonObject.addProperty("user_type", user_type);
            jsonObject.addProperty("locality", locality);
            jsonObject.addProperty("district", district);
            jsonObject.addProperty("province", province);
            jsonObject.addProperty("business_name", business_name);
            jsonObject.addProperty("authorized_person1", authorized_person1);
            jsonObject.addProperty("authorized_person2", authorized_person2);
            jsonObject.addProperty("tax_id", tax_id);
            jsonObject.addProperty("addressline1", addressline1);
            jsonObject.addProperty("addressline2", addressline2);
            jsonObject.addProperty("agent_type", agentType);
            jsonObject.addProperty("iat", System.currentTimeMillis());

            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //Send Email OTP From sign UP Flow
    public void sendEmailOtp(Boolean retry, String email, String otp_id, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "sendEmailOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("otp_id", otp_id);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void createPin(Boolean retry, String currency_id, String user_id, String pin, String mobile, int preApproved, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "addPin";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", user_id);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("Preapproved", preApproved);
            jsonObject.addProperty("pin", pin);
            jsonObject.addProperty("mobile", mobile);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.ANONYMOUS_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void updatePin(Boolean retry, String user_id, String pin, String mobile, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "pinUpdate";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", user_id);
            jsonObject.addProperty("pin", pin);
            jsonObject.addProperty("mobile", mobile);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.ANONYMOUS_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Verify Email OTP From sign UP Flow
    public void verifyEmailOtp(Boolean retry, String email, String otp_id, String otp, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "verifyEmailOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("email", email);
            jsonObject.addProperty("otp_id", otp_id);
            jsonObject.addProperty("otp", otp);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Provinces  Districts list
    public void getProvincesNDistricts(Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getProvincesNdistricts";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Get Bank List
    public void getBankList(Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getBanks";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Get cash out fees list
    public void getFeesDetails(Boolean retry, Boolean progress, String type, String amount, String userId, String currency_id, CustomCallback customCallback) {
        try {
            String apiName = "getCommission";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("commission_type", type);
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("currency_id", currency_id);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //Get cash out fees list
    public void getMPesaFeesDetails(String type, Boolean retry, Boolean progress, String amount, String userId, CustomCallback customCallback) {
        try {
            String URL = "";
            switch (type) {
                case SthiramValues.MPESA_KENYA_CODE:
                    URL = SthiramValues.MPESA_URL;
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    URL = SthiramValues.AIRTAL_UGAND_URL;
                    break;
                case SthiramValues.MTN_UGANDA_CODE:
                    URL = SthiramValues.MTN_UGAND_URL;
                    break;
            }
            String apiName = "seaFire_getFees";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("userId", userId);
            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getMpesaExchangeRate(String type, Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            JsonObject jsonObject = new JsonObject();
            String URL = "";
            String apiName = "";
            switch (type) {
                case SthiramValues.MPESA_KENYA_CODE:
                    URL = SthiramValues.MPESA_URL;
                    apiName = "seaFire_mpesa_get_exchangerate_v2";
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    URL = SthiramValues.AIRTAL_UGAND_URL;
                    apiName = "seaFire_airtel_get_exchangerate_v2";
                    break;
                case SthiramValues.MTN_UGANDA_CODE:
                    URL = SthiramValues.MTN_UGAND_URL;
                    apiName = "seaFire_mtn_get_exchangerate_v2";
                    break;
            }
            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //Exchange rate
    public void getExchangeRate(String userId, Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            String URL = SthiramValues.TRANSACTION_URL;
            String apiName = "getExchangeRate";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("from_currency", "USD");
            jsonObject.addProperty("to_currency", "SSP");
            jsonObject.addProperty("user_id", userId);
            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }


    //get all currency list
    public void getAllCurrencyList(Boolean retry, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getAllCurrencies";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.MULTI_CURRENCY_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //get county list
    public void getCountyList(Boolean retry, Boolean progress, String type, CustomCallback customCallback) {
        try {
            String apiName = "availableCountriesList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("type", type);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.ANONYMOUS_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //get all currency list
    public void getProvidersList(Boolean retry, String option_id, Boolean progress, CustomCallback customCallback) {

        try {
            String apiName = "utilityProviderList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("option_id", option_id);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.BILL_PAYMENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }


    //get my currency list
    public void getMyCurrencyList(Boolean retry, Boolean progress, String userId, CustomCallback customCallback) {
        try {
            String apiName = "getUserCurrencies";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("userId", userId);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.MULTI_CURRENCY_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //change default currency
    public void changeDefaultCurrency(Boolean retry, Boolean progress, String userId, String defaultCurrency, CustomCallback customCallback) {
        try {
            String apiName = "changeDefaultCurrency";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("defaultCurrency", defaultCurrency);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.MULTI_CURRENCY_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //get not linked currency list
    public void getNotLinkedCurrencyList(Boolean retry, Boolean progress, String userId, String currencyId, CustomCallback customCallback) {
        try {
            String apiName = "getAccountsNotYetLinked";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("currency_id", currencyId);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.MULTI_CURRENCY_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //add a new currency
    public void addANewCurrency(Boolean retry, Boolean progress, String userId, String currency_id, CustomCallback customCallback) {
        try {
            String apiName = "addNewCurrency";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("currency_id", currency_id);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.MULTI_CURRENCY_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    public void getMpesaLimits(String type, Boolean retry, Boolean progress, String amount, String userId, CustomCallback customCallback) {
        try {
            String URL = "";
            String apiName = "";
            switch (type) {
                case SthiramValues.MPESA_KENYA_CODE:
                    URL = SthiramValues.MPESA_URL;
                    apiName = "getMpesaLimits_v2";
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    URL = SthiramValues.AIRTAL_UGAND_URL;
                    apiName = "getAirtelLimits_V2";
                    break;
                case SthiramValues.MTN_UGANDA_CODE:
                    URL = SthiramValues.MTN_UGAND_URL;
                    apiName = "getMTNLimits_v2";
                    break;
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userAmount", amount);
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    public void paymentRequestValidate(String type, Boolean retry, Boolean progress, String amount, String mobile, String userId, String name, String countyCode, CustomCallback customCallback) {
        try {
            String URL = "";
            String apiName = "";
            switch (type) {
                case SthiramValues.MPESA_KENYA_CODE:
                    URL = SthiramValues.MPESA_URL;
                    apiName = "paymentrequestvalidate_v1";
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    URL = SthiramValues.AIRTAL_UGAND_URL;
                    apiName = "paymentrequestvalidate_v1";
                    break;
                case SthiramValues.MTN_UGANDA_CODE:
                    URL = SthiramValues.MTN_UGAND_URL;
                    apiName = "paymentrequestvalidate_v1";
                    break;
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("mobile", countyCode + mobile);
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("name", "809049");
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    public void mpesaPaymentRequestAPI(Boolean retry, Boolean progress,
                                       String mobile,
                                       String userId,
                                       String name,
                                       String conversationID,
                                       String mmTransactionID,
                                       String amountinkes,
                                       String amountinusd,
                                       String usdtokesvalue,
                                       String yeeltid,
                                       String partnerTransactionID,
                                       String totalAmountInUsd,
                                       String feesType,
                                       String commissionPercentage,
                                       String currency_id,
                                       String currency_code,
                                       String fees,
                                       String type,
                                       String mpesa_receiver_mobile,
                                       String mpesa_receiver_country_code,
                                       String mpesa_receiver_country_code_without_plus,
                                       String receiver_currency_code,
                                       String senderUserType,
                                       String remitter_id,
                                       CustomCallback customCallback) {
        try {

            //transaction Type
            String transactionType = "";


            String URL = "";
            switch (type) {
                case SthiramValues.MPESA_KENYA_CODE:
                    URL = SthiramValues.MPESA_URL;
                    break;
                case SthiramValues.AIRTAL_UGANDA_CODE:
                    URL = SthiramValues.AIRTAL_UGAND_URL;
                    break;
                case SthiramValues.MTN_UGANDA_CODE:
                    URL = SthiramValues.MTN_UGAND_URL;

                    break;
            }


            //API name
            String apiName = "";
            if (senderUserType.equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
                if (type.equals(SthiramValues.MPESA_KENYA_CODE)) {
                    //agent mpesa
                    transactionType = SthiramValues.TRANSACTION_TYPE_MPESA_AGENT;
                    apiName = "sendMpesaAgent";
                } else if (type.equals(SthiramValues.AIRTAL_UGANDA_CODE)) {
                    //agent airtel uganda
                    transactionType = SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA_AGENT;
                    apiName = "sendpaymentrequest_agent_v1";
                }
            } else {
                apiName = "sendpaymentrequest_v1";
                if (type.equals(SthiramValues.MPESA_KENYA_CODE)) {
                    //user mpesa
                    transactionType = SthiramValues.TRANSACTION_TYPE_MPESA;
                } else if (type.equals(SthiramValues.AIRTAL_UGANDA_CODE)) {
                    //user airtel uganda
                    transactionType = SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA;
                }
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile", mpesa_receiver_country_code_without_plus + mobile);
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("conversationID", conversationID);
            jsonObject.addProperty("mmTransactionID", mmTransactionID);
            jsonObject.addProperty("amountinkes", amountinkes);
            jsonObject.addProperty("amountinusd", amountinusd);
            jsonObject.addProperty("usdtokesvalue", usdtokesvalue);
            jsonObject.addProperty("yeeltid", yeeltid);
            jsonObject.addProperty("partnerTransactionID", partnerTransactionID);
            jsonObject.addProperty("totalAmountInUsd", totalAmountInUsd);
            jsonObject.addProperty("feesType", feesType);
            jsonObject.addProperty("commissionPercentage", commissionPercentage);
            jsonObject.addProperty("paymentId", "AN" + UUID.randomUUID().toString());
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("fees", fees);
            jsonObject.addProperty("receiver_currency_code", receiver_currency_code);
            jsonObject.addProperty("mmoney_receiver_mobile", mpesa_receiver_mobile);
            jsonObject.addProperty("mmoney_receiver_country_code", mpesa_receiver_country_code);
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("remitter_id", remitter_id);
            jsonObject.addProperty("transaction_type", transactionType);
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());


            callAPI(0, retry, jsonObject, apiName, progress, URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }
    }

    //Get cash out fees list
    public void doCashOut(Boolean retry,
                          Boolean progress,
                          String senderUserType,
                          String senderAccountNumber,
                          String receiverAccountNumber,
                          String totalAmount,
                          String remark,
                          String senderId,
                          String senderName,
                          String receiverId,
                          String receiverName,
                          String feeAmount,
                          String receiverGetAmount,
                          String commissionRate,
                          String tireName,
                          String currency_id,
                          String currency_code,
                          CustomCallback customCallback) {
        try {
            String apiName = "cashOut";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmount);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("transaction_type", "Cash Out");
            jsonObject.addProperty("commission_amount", feeAmount);
            jsonObject.addProperty("receiver_gets_amt", receiverGetAmount);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("tier_name", tireName);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void doExchange(Boolean retry,
                           Boolean progress,
                           String senderUserType,
                           String senderAccountNumber,
                           String receiverAccountNumber,
                           String totalAmount,
                           String remark,
                           String senderId,
                           String senderName,
                           String receiverId,
                           String receiverName,
                           String feeAmount,
                           String receiverGetAmount,
                           String exchangeRate,
                           String sendAmount,
                           CustomCallback customCallback) {
        try {
            String apiName = "exchangeSSPtoUSD";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmount);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("transaction_type", SthiramValues.TRANSACTION_TYPE_EXCHANGE);
            jsonObject.addProperty("commission_amount", feeAmount);
            jsonObject.addProperty("receiver_gets_amt", receiverGetAmount);
            jsonObject.addProperty("exchange_rate", exchangeRate);
            jsonObject.addProperty("send_amount", sendAmount);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Get cash pickup
    public void doCasPickup(Boolean retry,
                            Boolean progress,
                            String senderUserType,
                            String senderAccountNumber,
                            String receiverAccountNumber,
                            String totalAmount,
                            String remark,
                            String senderId,
                            String senderName,
                            String receiverId,
                            String receiverName,
                            String feeAmount,
                            String receiverGetAmount,
                            String commissionRate,
                            String tireName,
                            String transaction_type,
                            String remitter_id,
                            String beneficiary_id,
                            String purpose,
                            String currency_id,
                            String currency_code,
                            CustomCallback customCallback) {
        try {
            String apiName = "cashPickUp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmount);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("note", remark);
            jsonObject.addProperty("purpose", purpose);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("commission_amount", feeAmount);
            jsonObject.addProperty("receiver_gets_amt", receiverGetAmount);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("tier_name", tireName);
            jsonObject.addProperty("transaction_type", transaction_type);
            jsonObject.addProperty("remitter_id", remitter_id);
            jsonObject.addProperty("beneficiary_id", beneficiary_id);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("beneficiary_id", beneficiary_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //cash pickup from agent side
    public void doCasPickupFromAgentSide(
            Boolean retry,
            Boolean progress,
            String senderUserType,
            String senderAccountNumber,
            String receiverAccountNumber,
            String totalAmount,
            String remark,
            String senderId,
            String senderName,
            String receiverId,
            String receiverName,
            String feeAmount,
            String receiverGetAmount,
            String commissionRate,
            String tireName,
            String transaction_type,
            String remitter_id,
            String beneficiary_id,
            String purpose,
            String currency_id,
            String currency_code,
            CustomCallback customCallback) {
        try {
            String apiName = "agent_cashPickUp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmount);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("note", remark);
            jsonObject.addProperty("purpose", purpose);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("commission_amount", feeAmount);
            jsonObject.addProperty("receiver_gets_amt", receiverGetAmount);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("tier_name", tireName);
            jsonObject.addProperty("transaction_type", transaction_type);
            jsonObject.addProperty("remitter_id", remitter_id);
            jsonObject.addProperty("beneficiary_id", beneficiary_id);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }

    }

    public void sendViaYeelAgentSide(Boolean retry,
                                     Boolean progress,
                                     String senderUserType,
                                     String senderAccountNumber,
                                     String receiverAccountNumber,
                                     String totalAmount,
                                     String remark,
                                     String senderId,
                                     String senderName,
                                     String receiverId,
                                     String receiverName,
                                     String feeAmount,
                                     String receiverGetAmount,
                                     String commissionRate,
                                     String tireName,
                                     String transaction_type,
                                     String remitter_id,
                                     String purpose,
                                     String currency_id,
                                     String currency_code,
                                     CustomCallback customCallback) {
        try {
            String apiName = "sendViaYeel";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmount);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("note", remark);
            jsonObject.addProperty("purpose", purpose);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("commission_amount", feeAmount);
            jsonObject.addProperty("receiver_gets_amt", receiverGetAmount);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("tier_name", tireName);
            jsonObject.addProperty("transaction_type", transaction_type);
            jsonObject.addProperty("remitter_id", remitter_id);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getAddFundHistoryList(Boolean retry, Boolean progress, String userID, String account_no, CustomCallback customCallback) {
        try {
            String apiName = "addFundReqList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("user_id", userID);
            jsonObject.addProperty("account_no", account_no);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //Get Full Agent List
    public void getFullAgentList(Boolean retry, Boolean progress, String userID, CustomCallback customCallback) {
        try {
            String apiName = "getAgentList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("latitude", "");
            jsonObject.addProperty("longitude", "");
            jsonObject.addProperty("page", "");
            jsonObject.addProperty("keyWord", "");

            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void addFundAPIOne(Boolean retry,
                              Boolean progress,
                              String userID,
                              String bankName,
                              String bankCode,
                              String amount,
                              String refNo,
                              String userType,
                              String accountNumber,
                              String agentName,
                              String currencyCode,
                              String currencyId,
                              CustomCallback customCallback) {
        try {
            String apiName = "addtoit";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("user_id", userID);
            jsonObject.addProperty("bankName", bankName);
            jsonObject.addProperty("bankCode", bankCode);
            jsonObject.addProperty("account_no", accountNumber);
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("refNo", refNo);
            jsonObject.addProperty("user_type", userType);
            jsonObject.addProperty("agent_name", agentName);
            jsonObject.addProperty("currency_code", currencyCode);
            jsonObject.addProperty("currency_id", currencyId);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.APP_BASE_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public Retrofit createRetrofitObjectForUploadImage(String user_id,
                                                       String id_type,
                                                       String id_number,
                                                       String issuing_country,
                                                       String issuing_country_flag,
                                                       String expiryDate,
                                                       String previous_kyc_id,
                                                       String yeel_kyc_id,
                                                       String Preapproved,
                                                       String url,
                                                       String skipIndividualKYC) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("id_type", id_type);
        jsonObject.addProperty("id_number", id_number);
        jsonObject.addProperty("issuing_country", issuing_country);
        jsonObject.addProperty("issuing_country_flag", issuing_country_flag);
        jsonObject.addProperty("expiryDate", expiryDate);
        jsonObject.addProperty("previous_kyc_id", previous_kyc_id);
        jsonObject.addProperty("yeel_kyc_id", yeel_kyc_id);
        jsonObject.addProperty("Preapproved", Preapproved);
        jsonObject.addProperty("skipIndividualKYC ", skipIndividualKYC);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);

    }

    public Retrofit createRetrofitObjectForUploadProfileImage(String user_id, String url) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }

    public Retrofit createRetrofitObjectForUploadBeneficiaryID(String user_id, String beneficiaryId, String url) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("non_yeel_user_id", beneficiaryId);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }

    //createRetrofitObjectForUploadImage
    public Retrofit createRetrofitObjectForUploadBankReceipt(String user_id, String url, String reqId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("req_id", reqId);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }


    public Retrofit createRetrofitObjectForUploadCashPickupReceiverDoc(String user_id, String non_yeel_user_id, String ydb_ref_id, String url) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("non_yeel_user_id", non_yeel_user_id);
        jsonObject.addProperty("ydb_ref_id", ydb_ref_id);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }

    public Retrofit createRetrofitObjectForUploadCashOutReceiverDoc(String user_id, String ydb_ref_id, String url) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        jsonObject.addProperty("non_yeel_user_id", "");
        jsonObject.addProperty("ydb_ref_id", ydb_ref_id);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }

    public Retrofit createRetrofitObjectForSyncContact(String user_id, String url) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("iat", System.currentTimeMillis());
        jsonObject.addProperty("user_id", user_id);
        String jwtToken = createJWTToken(jsonObject);
        return createRetrofitObjectWithHeader(url, jwtToken);
    }

    public void sendOTPForLogin(String type, Boolean retry, String devicetoken, String mobileNumber, String countryCode, String otp_id, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "loginOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("country_code", countryCode);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("otp_id", otp_id);
            jsonObject.addProperty("devicetoken", devicetoken);
            jsonObject.addProperty("iat", System.currentTimeMillis());

            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TOKEN_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    public void checkValidMobile(Boolean retry, String deviceToken, String mobileNumber, String countryCode, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "loginOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("country_code", countryCode);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("deviceToken", deviceToken);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TOKEN_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void otpVerificationFromLogin(Boolean retry, String mobileNumber, String countyCode, String otpId, String otp, String fcmToken, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "loginWithSecureOtp";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("otp", otp);
            jsonObject.addProperty("otp_id", otpId);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("country_code", countyCode);
            jsonObject.addProperty("grant_type", "client_credentials");
            jsonObject.addProperty("client_id", countyCode + mobileNumber);
            jsonObject.addProperty("devicetoken", fcmToken);
            jsonObject.addProperty("device_id", getDeviceId());
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TOKEN_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //ID Type Lists
    public void getIDTypes(Boolean retry, String type, String county_name, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getIdType";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("user_type", type);
            jsonObject.addProperty("country_name", county_name);
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.ANONYMOUS_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //Logout API Call
    public void logOutAPI(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "logout";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //Login API
    public void loginWithPINAPI(Boolean retry, String mobileNumber, String countyCode, String pin, String fcmToken, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "token";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("grant_type", "client_credentials");
            jsonObject.addProperty("client_id", countyCode + mobileNumber);
            jsonObject.addProperty("client_secret", pin);
            jsonObject.addProperty("devicetoken", fcmToken);
            jsonObject.addProperty("device_id", getDeviceId());
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(1, retry, jsonObject, apiName, progress, SthiramValues.TOKEN_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getUserDetailsUsingUserId(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getUserbyId";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getUserDetailsUsingQrCode(Boolean retry, String qrCode, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getUserByQrCode";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("qr_code", qrCode);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getUserDetailsUsingMobile(Boolean retry, String userId, String countryCode, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getUserByMobile";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile", userId);
            jsonObject.addProperty("country_code", countryCode);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getUserBasicDetails(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "userBasicDetails";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void getKYCDetails(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getUserKycDetails";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void pinVerification(Boolean retry, String userId, String PIN, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getReady";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("user_info", PIN);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //getUser Basic Details
    public void makeY2YPayment(Boolean retry,
                               String type,
                               String senderUserType,
                               String senderId,
                               String senderName,
                               String senderAccountNumber,
                               String receiverAccountNumber,
                               String receiverName,
                               String receiverId,
                               String remark,
                               String amountToSend,
                               String currency_id,
                               String currency_code,
                               Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "";
            String transactionType = "";
            if (type.equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                apiName = "transferToMerchant";
                transactionType = "Yeel Account Merchant";
            } else {
                apiName = "transferFund";
                transactionType = "Yeel Account";
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", amountToSend);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("transaction_type", transactionType);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(1, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    public void transferToMerchant(Boolean retry,
                                   String senderUserType,
                                   String senderId,
                                   String senderName,
                                   String senderAccountNumber,
                                   String receiverAccountNumber,
                                   String receiverName,
                                   String receiverId,
                                   String remark,
                                   String totalAmountToPay,
                                   String currency_id,
                                   String currency_code,
                                   String receiverGetsAmount,
                                   String commissionAmount,
                                   String commissionRate,
                                   String tierName,
                                   Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "merchantScanNpay";
            String transactionType = "Yeel Account Merchant";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("sender_user_type", senderUserType);
            jsonObject.addProperty("sender_account_no", senderAccountNumber);
            jsonObject.addProperty("receiver_account_no", receiverAccountNumber);
            jsonObject.addProperty("amount", totalAmountToPay);
            jsonObject.addProperty("transaction_description", remark);
            jsonObject.addProperty("sender_id", senderId);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("transaction_type", transactionType);
            jsonObject.addProperty("currency_id", currency_id);
            jsonObject.addProperty("currency_code", currency_code);
            jsonObject.addProperty("receiver_gets_amt", receiverGetsAmount);
            jsonObject.addProperty("commission_amount", commissionAmount);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("tier_name", tierName);
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(1, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }


    //getUser recent transactions
    public void recentTransactions(Boolean retry, String accountNo, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "recentTransactions";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    public void quickLinkTransList(Boolean retry, String accountNo, String userId, String transactionType, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "quickLinkTransList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("transaction_type", transactionType);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e.toString());
        }

    }

    //getUser recent transactions
    public void getTransactionsByType(Boolean retry, String accountNo, String limit, String userId, String type, String offset, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getTransactionsByType";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("offset", offset);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("limit", limit);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }

    }

    //getUser recent transactions
    public void getAgentCashPickupList(Boolean retry, String account_no, String limit, String userId, String offset, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "listCashPickupNCashOuts";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", account_no);
            jsonObject.addProperty("offset", offset);
            jsonObject.addProperty("limit", limit);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception ignored) {

        }

    }


    //getUser recent transactions
    public void getAgentCommissionList(Boolean retry, String accountNumber, String limit, String userId, String offset, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "agentCommissionList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("offset", offset);
            jsonObject.addProperty("limit", limit);
            jsonObject.addProperty("account_no", accountNumber);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }


    }


    //getUser recent transactions
    public void getTransactionDetails(Boolean retry, String userId, String ydbRefId, String notificationId, Boolean progress, CustomCallback customCallback) {
        try {
            // String apiName="getTransactionDetail";
            String apiName = "notificationUpdateNtransDetail";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("transaction_id", ydbRefId);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("notificationId", notificationId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception ignored) {

        }


    }

    //get Account Summary
    public void getAccountSummary(Boolean retry, String userId, String accountNo, String offset, String limit, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getAccountSummary";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("offset", offset);
            jsonObject.addProperty("limit", limit);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }


    }


    //get Account Summary
    public void getAllTransactionList(Boolean retry, String accountNo, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "fullTransactions";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }


    }


    //get notification list
    public void getNotificationList(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "userNotifications";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }


    }


    //get notification list
    public void readAllNotification(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "readAll";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception ignored) {

        }


    }


    //getUser Basic Details
    public void getReport(Boolean retry, String userId, String accountNo, String selectedMonth, String selectedYear, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getMonthlyReportPdf";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNo);
            jsonObject.addProperty("month", selectedMonth);
            jsonObject.addProperty("year", selectedYear);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }

    }


    //get agent dashboard
    public void getAgentDashBoard(Boolean retry, String userId, String accountNumber, String numberOfDays, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "agentDashboard";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("account_no", accountNumber);
            jsonObject.addProperty("no_of_days", numberOfDays);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.AGENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }

    }

    //get beneficiary details
    public void addBeneficiary(Boolean retry, String userId, String firstName, String middleName, String lastName, String mobileNumber, String country_code, String emailAddress, String country, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "addNonYeelSenderNreceiver";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("firstname", firstName);
            jsonObject.addProperty("middlename", middleName);
            jsonObject.addProperty("lastname", lastName);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("country_code", country_code);
            jsonObject.addProperty("email", emailAddress);
            jsonObject.addProperty("country", country);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }

    }

    /**
     * Update Beneficiary details
     */


    public void updateBeneficiary(Boolean retry, String userId, String firstName, String middleName, String lastName, String mobileNumber, String country_code, String emailAddress, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "updateBenficiary";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("beneficiaryId", userId);
            jsonObject.addProperty("firstname", firstName);
            jsonObject.addProperty("middlename", middleName);
            jsonObject.addProperty("lastname", lastName);
            jsonObject.addProperty("mobile", mobileNumber);
            jsonObject.addProperty("country_code", country_code);
            jsonObject.addProperty("email", emailAddress);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }

    }


    /**
     * Get already added beneficiary details to show on autocomplete textview
     */
    public void getBeneficiariesList(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "nonYeeluserList";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.USER_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }

    }


    /**
     * Get already added beneficiary details to show on autocomplete textview
     */
    public void getPurposeList(Boolean retry, String userId, Boolean progress, CustomCallback customCallback) {
        try {
            String apiName = "getPurpose";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", userId);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.TRANSACTION_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {

        }

    }


    // bill payment confirmation - University Fees
    public void billPaymentConfirmationAPI(Boolean retry, Boolean progress,
                                           String customerMobile,
                                           String currencyCode,
                                           String receiverName,
                                           String loggedinUserCountryCode,
                                           String loggedinUserMobile,
                                           String receiverId,
                                           String transactionDescription,
                                           String providerCustomerName,
                                           String commissionRate,
                                           String currencyId,
                                           String commissionAmount,
                                           String senderAccountNo,
                                           String tierName,
                                           String purpose,
                                           String amount,
                                           String providerId,
                                           String customerCountryCode,
                                           String userId,
                                           String receiverAccountNo,
                                           String meterNo,
                                           String senderName,
                                           String isExternal,
                                           String receiverGetsAmt,
                                           String senderUserType,
                                           CustomCallback customCallback) {
        try {
            String apiName = "updateVendorNpayBill";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("customer_mobile", customerMobile);
            jsonObject.addProperty("iat", System.currentTimeMillis());
            jsonObject.addProperty("currency_code", currencyCode);
            jsonObject.addProperty("receiver_name", receiverName);
            jsonObject.addProperty("loggedin_user_country_code", loggedinUserCountryCode);
            jsonObject.addProperty("loggedin_user_mobile", loggedinUserMobile);
            jsonObject.addProperty("receiver_id", receiverId);
            jsonObject.addProperty("transaction_description", transactionDescription);
            jsonObject.addProperty("provider_customer_name", providerCustomerName);
            jsonObject.addProperty("commission_rate", commissionRate);
            jsonObject.addProperty("currency_id", currencyId);
            jsonObject.addProperty("commission_amount", commissionAmount);
            jsonObject.addProperty("sender_account_no", senderAccountNo);
            jsonObject.addProperty("tier_name", tierName);
            jsonObject.addProperty("purpose", purpose);
            jsonObject.addProperty("amount", amount);
            jsonObject.addProperty("provider_id", providerId);
            jsonObject.addProperty("customer_country_code", customerCountryCode);
            jsonObject.addProperty("user_id", userId);
            jsonObject.addProperty("receiver_account_no", receiverAccountNo);
            jsonObject.addProperty("meter_no", meterNo);
            jsonObject.addProperty("sender_name", senderName);
            jsonObject.addProperty("is_external", isExternal);
            jsonObject.addProperty("receiver_gets_amt", receiverGetsAmt);
            jsonObject.addProperty("payment_id", "AN" + UUID.randomUUID().toString());
            jsonObject.addProperty("sender_user_type", senderUserType);

            callAPI(0, retry, jsonObject, apiName, progress, SthiramValues.BILL_PAYMENT_URL, new CustomCallback() {
                @Override
                public void success(CommonResponse response) {
                    customCallback.success(response);
                }

                @Override
                public void retry() {
                    customCallback.retry();
                }
            });
        } catch (Exception e) {
            logAValue("Error", e + "");
        }

    }


    private void callAPI(int loaderType, Boolean retry, JsonObject jsonObject, String apiName, Boolean progress, String URL, CustomCallback customCallback) {
        //loaderType =0 for default loader
        //loaderType=1 for custom loader
        if (progress) {
            if (loaderType == 0) {
                if (!dialogProgress.isShowing()) {
                    dialogProgress.show();
                }
            } else {
                if (!customDialogProgress.isShowing()) {
                    customDialogProgress.show();
                }
            }
        }
        String jwtToken = createJWTToken(jsonObject);
        Retrofit retrofit = createRetrofitObjectWithHeader(URL, jwtToken);
        APIInterface apiInterface = retrofit.create(APIInterface.class);
        apiInterface.apiCall(apiName).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NotNull Call<CommonResponse> call, @NotNull retrofit2.Response<CommonResponse> response) {
                try {
                    if (response.code() == 200) {
                        if (loaderType == 0) {
                            dialogProgress.dismiss();
                        } else {
                            customDialogProgress.dismiss();
                        }
                        customCallback.success(response.body());
                    } else if (response.code() == 502) {
                        //Error from clond front
                        //Toast.makeText(context, "502 Error", Toast.LENGTH_SHORT).show();
                        if (retry) {
                            if (loaderType == 0) {
                                dialogProgress.dismiss();
                            } else {
                                customDialogProgress.dismiss();
                            }
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        } else {
                            customCallback.retry();
                        }

                    } else if (response.code() == 401) {
                        //multiple session error
                        if (loaderType == 0) {
                            dialogProgress.dismiss();
                        } else {
                            customDialogProgress.dismiss();
                        }
                        if (!multipleLoginDialog.isShowing()) {
                            multipleLoginDialog.show();
                        }
                    } else if (response.code() == 403) {
                        //session expired error
                        if (!sessionExpiredDialog.isShowing()) {
                            sessionExpiredDialog.show();
                        }
                    } else if (response.code() == 503) {
                        //server maintenance error
                        if (loaderType == 0) {
                            dialogProgress.dismiss();
                        } else {
                            customDialogProgress.dismiss();
                        }
                        if (!serverMaintenanceDialog.isShowing()) {
                            serverMaintenanceDialog.show();
                        }
                    } else {
                        if (loaderType == 0) {
                            dialogProgress.dismiss();
                        } else {
                            customDialogProgress.dismiss();
                        }
                        if (!somethingWentWrongDialog.isShowing()) {
                            somethingWentWrongDialog.show();
                        }
                        //other errors
                    }
                } catch (Exception e) {
                    logAValue("Error", e + "");
                }
            }

            @Override
            public void onFailure(@NotNull Call<CommonResponse> call, @NotNull Throwable t) {
                try {
                    if (loaderType == 0) {
                        dialogProgress.dismiss();
                    } else {
                        customDialogProgress.dismiss();
                    }
                    if (t instanceof UnknownHostException || t instanceof SocketTimeoutException
                            || t instanceof NetworkErrorException) {
                        if (!checkInternetConnectionDialog.isShowing()) {
                            checkInternetConnectionDialog.show();
                        }
                    } else {
                        if (!somethingWentWrongDialog.isShowing()) {
                            somethingWentWrongDialog.show();
                        }
                    }
                } catch (Exception e) {
                    logAValue("Error", e + "");
                }

            }
        });
    }


    public String createJWTToken(JsonObject jsonObject) {
        logAValue("Request1", jsonObject.toString());

        String json = dataOluppikkal(jsonObject.toString(), getValueFromByteArray(ByteArrays.PUBLIC_KEY_ONE_BYTE_ARRAY_ENCRYPTED));
        String thakkol = TextCodec.BASE64.encode(getValueFromByteArray(ByteArrays.JWT_TOKEN_BYTE_ARRAY_ENCRYPTED));
        Header header = Jwts.header();
        header.setType(SthiramValues.SETUP_TYPE);
        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .claim("thuvalthulika", json)
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(thakkol))
                .compact();
    }

    //get value from a encrypted byte array
    private String getValueFromByteArray(byte[] byteArray) {
        //get encrypted string from byte array
        String encryptedValue = new String(byteArray);
        //get jwt key from encrypted string
        String key = new String(ByteArraysTwo.FRONT_END_PRIVATE_KEY);
        String value = dataEdukkal(encryptedValue, key);
        //String value = dataEdukkal(encryptedValue, Constants.FRONT_END_PRIVATE_KEY);
        return value;
    }

    //encrypt a string using public key (AES and RSA)
    public String dataOluppikkal(String clearText, String publicKey) {
        String dataToServer = "";
        try {
            //AES encryption
            String initVector = SthiramValues.VI;
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            SecretKey secKey = generator.generateKey();
            String paddingSeven = new String(ByteArraysThree.PADDING_SEVEN);
            Cipher aesCipher = Cipher.getInstance(paddingSeven);
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey, iv);
            byte[] AESEncrypted = aesCipher.doFinal(clearText.getBytes());

            //Generate Public Key
            KeyFactory publicKeyFac = KeyFactory.getInstance("RSA");
            KeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key puKey = publicKeyFac.generatePublic(publicKeySpec);

            //RSA Encryption


            String paddingOne = new String(ByteArraysThree.PADDING_ONE);
            Cipher cipher = Cipher.getInstance(paddingOne);
            cipher.init(Cipher.PUBLIC_KEY, puKey);
            byte[] RSAEncrypted = cipher.doFinal(secKey.getEncoded());


            /*String aesValue = new String(Base64.encode(AESEncrypted, Base64.DEFAULT)).replaceAll("(\\r|\\n)", "");
            String rsaValue = new String(Base64.encode(RSAEncrypted, Base64.DEFAULT)).replaceAll("(\\r|\\n)", "");*/


            String aesValue = new String(Base64.encode(AESEncrypted, Base64.DEFAULT)).replaceAll("([\\r\\n])", "");
            String rsaValue = new String(Base64.encode(RSAEncrypted, Base64.DEFAULT)).replaceAll("([\\r\\n])", "");

            dataToServer = aesValue + "._" + rsaValue;
        } catch (Exception ignored) {
        }
        return dataToServer;
    }

    //encrypt a string using public key (RSA)
    public String dataOluppikkalTwo(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);
            String paddingOne = new String(ByteArraysThree.PADDING_ONE);
            final Cipher cipher = Cipher.getInstance(paddingOne);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {

        }

        return encryptedBase64.replaceAll("([\\r\\n])", "");
    }


    public Retrofit createRetrofitObjectWithHeader(String URL, final String Authorization) {
        logAValue("User Id", getUserId() + " ");
        logAValue("Access Token", getAccessToken() + " ");
        String newAuthorize = dataOluppikkal(Authorization, getValueFromByteArray(ByteArrays.PUBLIC_KEY_TWO_BYTE_ARRAY_ENCRYPTED));
        String newUserId = dataOluppikkalTwo(getUserId(), getValueFromByteArray(ByteArrays.PUBLIC_KEY_TWO_BYTE_ARRAY_ENCRYPTED));
        String newToken = dataOluppikkalTwo(getAccessToken(), getValueFromByteArray(ByteArrays.PUBLIC_KEY_TWO_BYTE_ARRAY_ENCRYPTED));
        logAValue("-", "---------------- HEADERS ---------------------");
        logAValue("pradhanakavaadam", newAuthorize);
        logAValue("upakaranam", "android");
        logAValue("upakaranaadayalam", getDeviceId());
        logAValue("sankethikapathippu", getCurrentVersion());
        logAValue("thakkolpadam", newToken);
        logAValue("samayamsoochika", getTimeStamp());
        logAValue("upayogaarthi", newUserId);
        logAValue("-", "---------------- HEADERS ---------------------");
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder newRequest = request.newBuilder()
                            .header("pradhanakavaadam", newAuthorize)
                            .header("upakaranam", "android")
                            .header("upakaranaadayalam", getDeviceId())
                            .header("sankethikapathippu", getCurrentVersion())
                            .header("thakkolpadam", newToken)
                            .header("samayamsoochika", getTimeStamp())
                            //.header("user_req", "YDB010101")
                            .header("user_req", "")
                            .header("upayogaarthi", newUserId);
                    return chain.proceed(newRequest.build());
                }).build();


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());
        return builder.build();
    }


    //access token
    public String getAccessToken() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_ACCESS_TOKEN", "");
    }


    //user id
    public String getUserId() {
        return sharedPrefUtil.getEncryptedStringPreference("KEY_USER_ID", "");
    }


    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //get app version
    public String getCurrentVersion() {
        int currentVersion;
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersion = pInfo.versionCode;
        } catch (Exception e) {
            currentVersion = 0;
        }

        return currentVersion + "";
    }

    //get app version
    public String getTimeStamp() {
        long tsLong = System.currentTimeMillis() / 1000;
        return Long.toString(tsLong);
    }

    //get Json from encrypted string
    public String getJsonFromEncryptedString(String value) {
        String jsonString = dataEdukkal(value, getValueFromByteArray(ByteArrays.PRIVATE_KEY_ONE_BYTE_ARRAY_ENCRYPTED));
        String jsonNew = jsonString.replaceAll("'\'", "");
        logAValue("Response", jsonNew);
        return jsonNew;
    }

    //decrypt AES and RAS encrypted data
    public String dataEdukkal(String value, String privateKey) {
        String[] valueList = value.split("._");
        String aesValue = valueList[0];
        String rsaValue = valueList[1];
        String jsonString = "";
        try {
            KeyFactory privateKeyFac = KeyFactory.getInstance("RSA");
            KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key prKey = privateKeyFac.generatePrivate(privateKeySpec);

            String initVector = SthiramValues.VI;
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

            String paddingOne = new String(ByteArraysThree.PADDING_ONE);
            Cipher cipher = Cipher.getInstance(paddingOne);
            cipher.init(Cipher.PRIVATE_KEY, prKey);
            byte[] decryptedKey = cipher.doFinal(Base64.decode(rsaValue, Base64.DEFAULT));


            SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
            String paddingSeven = new String(ByteArraysThree.PADDING_SEVEN);
            Cipher aesCipher2 = Cipher.getInstance(paddingSeven);
            aesCipher2.init(Cipher.DECRYPT_MODE, originalKey, iv);
            byte[] bytePlainText = aesCipher2.doFinal(Base64.decode(aesValue, Base64.DEFAULT));
            jsonString = new String(bytePlainText);
        } catch (InvalidKeySpecException e) {
            try {
                KeyFactory privateKeyFac = KeyFactory.getInstance("RSA", "BC");
                KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
                Key prKey = privateKeyFac.generatePrivate(privateKeySpec);

                String initVector = SthiramValues.VI;
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
                String paddingOne = new String(ByteArraysThree.PADDING_ONE);
                Cipher cipher = Cipher.getInstance(paddingOne);
                cipher.init(Cipher.PRIVATE_KEY, prKey);
                byte[] decryptedKey = cipher.doFinal(Base64.decode(rsaValue, Base64.DEFAULT));


                SecretKey originalKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
                String paddingSeven = new String(ByteArraysThree.PADDING_SEVEN);
                Cipher aesCipher2 = Cipher.getInstance(paddingSeven);
                aesCipher2.init(Cipher.DECRYPT_MODE, originalKey, iv);
                byte[] bytePlainText = aesCipher2.doFinal(Base64.decode(aesValue, Base64.DEFAULT));
                jsonString = new String(bytePlainText);
            } catch (Exception e1) {
                logAValue("Error", e.toString());
            }

        } catch (Exception e) {
            logAValue("Error", e.toString());

        }
        return jsonString;
    }

    public void logAValue(String label, String values) {
        Log.e(label, values);
    }


    //get Json from encrypted string
    public String getCountyCodeFormatFromCountyList(String value, String countryCode) {
        String countyCodeFormat = "";
        assert value != null;
        if (!value.equals("")) {
            List<CountryListData> countryList = null;
            String jsonString = getJsonFromEncryptedString(value);
            Gson gson = new Gson();
            CountryListResponse apiResponse = gson.fromJson(jsonString, CountryListResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                countryList = apiResponse.getCountryList();
            }

            for (int i = 0; i < Objects.requireNonNull(countryList).size(); i++) {
                if (countryList.get(i).getCountryMobileNumberCode().equals(countryCode)) {
                    countyCodeFormat = countryList.get(i).getFormat();
                    break;
                }
            }
        } else {
            countyCodeFormat = SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT;
        }

        return countyCodeFormat;
    }

    //get Json from encrypted string
    public String getFlagFromCountyList(String value, String countryCode) {
        String countyFlag = "";
        assert value != null;
        if (!value.equals("")) {
            List<CountryListData> countryList = null;
            String jsonString = getJsonFromEncryptedString(value);
            Gson gson = new Gson();
            CountryListResponse apiResponse = gson.fromJson(jsonString, CountryListResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                countryList = apiResponse.getCountryList();
            }

            for (int i = 0; i < Objects.requireNonNull(countryList).size(); i++) {
                if (countryList.get(i).getCountryMobileNumberCode().equals(countryCode)) {
                    countyFlag = countryList.get(i).getFlag();
                    break;
                }
            }
        } else {
            countyFlag = SthiramValues.DEFAULT_COUNTY_FLAG;
        }

        return countyFlag;
    }


}
