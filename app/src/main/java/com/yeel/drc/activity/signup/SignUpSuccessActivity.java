package com.yeel.drc.activity.signup;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.personal.PersonalKYCUploadActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.login.LoginWithPINResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class SignUpSuccessActivity extends BaseActivity {
    TextView tvContinue, textDescription;
    Context context;
    RegisterFunctions registerFunctions;
    SignUpData signUpData;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean loginWithPINAPIRetry = false;
    APICall apiCall;
    MultipleLoginDialog multipleLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_success);
        context = SignUpSuccessActivity.this;
        registerFunctions = new RegisterFunctions(context);
        signUpData = (SignUpData) getIntent().getSerializableExtra("signUpData");
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
                    if (signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
                        kycVerificationDialog();
                    } else {
                        callLoginAPI();
                    }
                }
        );
    }

    private void callLoginAPI() {
        apiCall.loginWithPINAPI(loginWithPINAPIRetry, signUpData.getMobileNumber(), signUpData.getCountryCode(),signUpData.getPin(), commonFunctions.getFCMToken(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    LoginWithPINResponse apiResponse = gson.fromJson(jsonString, LoginWithPINResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.clearLocalStorage();
                        commonFunctions.setUserDetailsAfterLogin(apiResponse.getData().getAccess_token(), apiResponse.getData().getUser_details());
                        commonFunctions.callHomeIntent();
                    } else {
                        if(apiResponse.getError_type().equals(SthiramValues.ERROR_TYPE_MULTIPLE_LOGIN)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            if (!errorDialog.isShowing()) {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                loginWithPINAPIRetry = true;
                callLoginAPI();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void kycVerificationDialog() {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_verification);
        TextView btnNow = success.findViewById(R.id.text_now);
        TextView btnLater = success.findViewById(R.id.text_later);
        TextView limitAmount = success.findViewById(R.id.text_limit_amount);

        String currencySymbol;
        if(signUpData.getCurrencyId().equals("1")){
            currencySymbol="SSP";
        }else {
            currencySymbol="USD";
        }
        limitAmount.setText(commonFunctions.setAmount(signUpData.getPreApprovedLimit())+" "+currencySymbol);
        btnLater.setOnClickListener(v -> {
            success.dismiss();
            callLoginAPI();
        });
        btnNow.setOnClickListener(v -> {
            success.dismiss();
            Intent in = new Intent(context, PersonalKYCUploadActivity.class);
            in.putExtra("signUpData", signUpData);
            startActivity(in);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    private void initView() {
        multipleLoginDialog=new MultipleLoginDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        commonFunctions.setPage("");
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        tvContinue = findViewById(R.id.tv_continue);
        textDescription = findViewById(R.id.text_description);

        if (signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            textDescription.setText(getString(R.string.you_are_ready_to_use_yeel));

        } else {
            textDescription.setText(getString(R.string.you_ve_finished_creating_a_yeel_sustenta_account_it_will_be_reviewed_within_24_hours));

        }
        tvContinue.setText(getString(R.string.ok_let_s_go));
    }
}