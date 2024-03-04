package com.yeel.drc.activity.signup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CommonEmailOTPVerificationActivity;
import com.yeel.drc.activity.signup.agent.AgentAddressActivity;
import com.yeel.drc.activity.signup.business.BusinessAddressActivity;
import com.yeel.drc.activity.signup.personal.ResidenceDetailsActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class EmailAddressActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    TextView tvContinue;
    TextInputLayout tilEmail;
    EditText edtEmail;
    String email;
    final static int OTP_VERIFICATION= 101;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    boolean sendEmailOtpRetry=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_address);
        context=EmailAddressActivity.this;
        setToolBar();
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setTaxId(registerFunctions.gettaxId());
            if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                signUpData.setFirstName(registerFunctions.getFirstName());
                signUpData.setMiddleName(registerFunctions.getMiddleName());
                signUpData.setLastName(registerFunctions.getLastName());
                signUpData.setDob(registerFunctions.getDOB());
            }else if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)){
                signUpData.setBusinessName(registerFunctions.getBusinessName());
                signUpData.setAuthorizedPersonOne(registerFunctions.getAuthorizedPersonOne());
                signUpData.setAuthorizedPersonTwo(registerFunctions.getAuthorizedPersonTwo());
                signUpData.setCompanyRegistrationNumber(registerFunctions.getCompanyRegistrationNumber());
            }else if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                signUpData.setBusinessName(registerFunctions.getBusinessName());
                signUpData.setAuthorizedPersonOne(registerFunctions.getAuthorizedPersonOne());
                signUpData.setCompanyRegistrationNumber(registerFunctions.getCompanyRegistrationNumber());
            }
        }
        initView();
        setItemListeners();
    }

    private void initView() {
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        tilEmail=findViewById(R.id.til_email);
        edtEmail=findViewById(R.id.edt_email);
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            sendEmailOtp();
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email=edtEmail.getText().toString().trim();
                if(commonFunctions.isEmailValid(email)){
                    tvContinue.setEnabled(true);
                    tilEmail.setError(null);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_one);
                }else{
                    tvContinue.setEnabled(false);
                    if(tilEmail.getError()==null||tilEmail.getError().equals("")){
                        tilEmail.setError(getString(R.string.enter_valid_email));
                    }
                    tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendEmailOtp() {
        apiCall.sendEmailOtp(sendEmailOtpRetry,email,"",true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        Intent in = new Intent(context, CommonEmailOTPVerificationActivity.class);
                        in.putExtra("emailAddress", email);
                        in.putExtra("otpId", apiResponse.getOtp_id());
                        startActivityForResult(in, OTP_VERIFICATION);
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }

                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                sendEmailOtpRetry=true;
                sendEmailOtp();
            }
        });
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        RelativeLayout rlSkip=findViewById(R.id.rl_skip);
        rlSkip.setVisibility(View.VISIBLE);
        rlSkip.setOnClickListener(view -> {
            signUpData.setEmailAddress("");
            registerFunctions.saveRegisterDetails(signUpData);
            if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)){
                commonFunctions.setPage("email_enter_b");
                Intent in=new Intent(context, BusinessAddressActivity.class);
                in.putExtra("signUpData",signUpData);
                startActivity(in);
            }else if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)||registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                commonFunctions.setPage("email_enter_a");
                Intent in=new Intent(context, AgentAddressActivity.class);
                in.putExtra("signUpData",signUpData);
                startActivity(in);
            }
        });
        tvTitle.setText("");
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(isTaskRoot()){
                    clearSignUpProgressDialog.show();
                }else{
                    finish();
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            clearSignUpProgressDialog.show();
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OTP_VERIFICATION) {
                signUpData.setEmailAddress(email);
                registerFunctions.saveRegisterDetails(signUpData);
                if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)){
                    commonFunctions.setPage("email_enter_i");
                    Intent in=new Intent(context, ResidenceDetailsActivity.class);
                    in.putExtra("signUpData",signUpData);
                    startActivity(in);
                }else if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)){
                    commonFunctions.setPage("email_enter_b");
                    Intent in=new Intent(context, BusinessAddressActivity.class);
                    in.putExtra("signUpData",signUpData);
                    startActivity(in);
                }else if(registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)||registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                    commonFunctions.setPage("email_enter_a");
                    Intent in=new Intent(context, AgentAddressActivity.class);
                    in.putExtra("signUpData",signUpData);
                    startActivity(in);
                }
            }
        }

    }

}