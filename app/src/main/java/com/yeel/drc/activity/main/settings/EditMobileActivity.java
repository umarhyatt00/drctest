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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CommonMobileOTPVerificationActivity;
import com.yeel.drc.activity.common.CountrySelectionActivity;
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
import com.yeel.drc.utils.TextFormatter;

public class EditMobileActivity extends BaseActivity {
     Context context;
     EditText edtMobile;
     TextView tvContinue;

    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    Boolean sendOtp =false;


    String mobileNumber;
    String mobileNumberLength;
    String countyCode;
    ImageView ivFlag;
    TextView tvCountryCode;
    LinearLayout llMobileCountyCode;
    String flag;
    final static int SELECT_A_COUNTRY_CODE= 102;


    final static int OTP_VERIFICATION= 101;
    boolean updateProfileRetry=false;
    TextWatcher watcher;
    SuccessDialog successDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_mobile);
        setToolBar();
        context= EditMobileActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, CountrySelectionActivity.class);
                in.putExtra("type","onboarding");
                startActivityForResult(in, SELECT_A_COUNTRY_CODE);
            }
        });
        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number=edtMobile.getText().toString().trim();
                mobileNumber= number.replaceAll("\\D","");
                if(commonFunctions.validateMobileNumberDynamic(mobileNumber,mobileNumberLength)){
                    String oldMobileNumber=commonFunctions.getCountryCode()+commonFunctions.getMobile();
                    String newMobileNumber=countyCode+mobileNumber;
                    if(oldMobileNumber.equals(newMobileNumber)){
                        tvContinue.setEnabled(false);
                        tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                    }else{
                        tvContinue.setEnabled(true);
                        tvContinue.setBackgroundResource(R.drawable.bg_button_one);
                    }
                }else{
                    tvContinue.setEnabled(false);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvContinue.setOnClickListener(view -> {
            String oldMobileNumber=commonFunctions.getCountryCode()+commonFunctions.getMobile();
            String newMobileNumber=countyCode+mobileNumber;
            if(oldMobileNumber.equals(newMobileNumber)){
                errorDialog.show("No changes found");
            }else{
                sendOtpAPICall();
            }
        });
    }

    private void initView() {
        successDialog=new SuccessDialog(context, SthiramValues.home);
        tvContinue=findViewById(R.id.tv_continue);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        edtMobile=findViewById(R.id.edt_mobile);

        //county code
        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag);
        llMobileCountyCode=findViewById(R.id.ll_mobile_county_code);
        flag= SthiramValues.DEFAULT_COUNTY_FLAG;
        setFlag(SthiramValues.DEFAULT_COUNTY_FLAG);
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);


        setValues();
    }

    private void setValues() {
        countyCode=commonFunctions.getCountryCode();
        mobileNumberLength=commonFunctions.getMobile().length()+"";
        tvCountryCode.setText(countyCode);
        setFlag(commonFunctions.getCountyFlag());
        edtMobile.setText(commonFunctions.getMobile());
        mobileNumber=commonFunctions.getMobile();
        tvContinue.setEnabled(false);
        tvContinue.setBackgroundResource(R.drawable.bg_button_three);
    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }


    private void sendOtpAPICall() {
        apiCall.sendOTP("SMS",sendOtp,commonFunctions.getFCMToken(),mobileNumber,countyCode,"",true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse=gson.fromJson(jsonString, SendOTPResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        Intent in=new Intent(context, CommonMobileOTPVerificationActivity.class);
                        in.putExtra("mobileNumber",mobileNumber);
                        in.putExtra("otpId",apiResponse.getOtp_id());
                        in.putExtra("country_code",countyCode);
                        startActivityForResult(in,OTP_VERIFICATION);
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
                sendOtp=true;
                sendOtpAPICall();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OTP_VERIFICATION) {
                updateProfile();
            }else if(requestCode==SELECT_A_COUNTRY_CODE){
                countyCode = data.getStringExtra("country_code");
                mobileNumberLength = data.getStringExtra("mobile_number_length");
                setMobileNumberFormat(data.getStringExtra("format"));
                flag= data.getStringExtra("flag");
                tvCountryCode.setText(countyCode);
                setFlag(flag);
                edtMobile.setText("");
            }
        }

    }

    private void updateProfile() {
        apiCall.updateProfile(updateProfileRetry,commonFunctions.getUserId(),"",
                "",
                "",
                mobileNumber,
                countyCode,
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
                "",
                true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try{
                            String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse=gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                commonFunctions.setCountryFlag(flag);
                                commonFunctions.setCountryCode(countyCode);
                                commonFunctions.setMobile(mobileNumber);
                                successDialog.show(getString(R.string.mobile_number_chnaged));
                               /* Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent in = getIntent();
                                setResult(Activity.RESULT_OK, in);
                                finish();*/
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

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.edt_mobile);
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

    private void setMobileNumberFormat(String format) {
        if(watcher!=null){
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

}