package com.yeel.drc.activity.main.settings;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CommonEmailOTPVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.dialogboxes.SuccessDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class EditEmailActivity extends BaseActivity {
    Context context;
    TextInputEditText edtEmail;
    TextView tvContinue;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    String email;
    TextInputLayout tilEmail;
    final static int OTP_VERIFICATION= 101;
    boolean updateProfileRetry=false;
    boolean sendEmailOtpRetry=false;
    SuccessDialog successDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        setToolBar();
        context= EditEmailActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            if(email.equals(commonFunctions.getEmail())){
                errorDialog.show("No changes found");
            }else{
                sendEmailOtp();
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email=edtEmail.getText().toString().trim();
                if(commonFunctions.isEmailValid(email)){
                    if(email.equals(commonFunctions.getEmail())){
                        tilEmail.setError(null);
                        tvContinue.setEnabled(false);
                        tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                    }else{
                        tvContinue.setEnabled(true);
                        tilEmail.setError(null);
                        tvContinue.setBackgroundResource(R.drawable.bg_button_one);
                    }
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

    private void initView() {
        successDialog=new SuccessDialog(context, SthiramValues.goback);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        edtEmail = findViewById(R.id.edt_email);
        email=commonFunctions.getEmail();
        tvContinue = findViewById(R.id.tv_continue);
        tilEmail=findViewById(R.id.til_email);
        LinearLayout llMain=findViewById(R.id.ll_main);
        setValue();
    }

    private void setValue() {
        if(commonFunctions.getEmail()!=null&&!commonFunctions.getEmail().equals("")){
            edtEmail.setText(commonFunctions.getEmail());
        }
        tvContinue.setEnabled(false);
        tvContinue.setBackgroundResource(R.drawable.bg_button_three);

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.edit_email);
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
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


    private void updateProfile() {
        apiCall.updateProfile(updateProfileRetry,commonFunctions.getUserId(),"",
                "",
                "",
                "",
                "",
                email,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try{
                            String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse=gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                commonFunctions.setEmail(email);
                                successDialog.show(apiResponse.getMessage());
                            }else{
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
                        updateProfileRetry=true;
                        updateProfile();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OTP_VERIFICATION) {
                updateProfile();
            }
        }

    }

}