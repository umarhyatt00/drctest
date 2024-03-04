package com.yeel.drc.activity.main.settings.sequrity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.yeel.drc.activity.common.CreatePINActivity;
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

public class ResetPINActivity extends BaseActivity {
    Context context;
    EditText edtMobile;
    TextView tvContinue;
    SuccessDialog successDialog;
    APICall apiCall;
    Boolean sendOtpRetry =false;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean updatePinRetry=false;
    String mobileNumber;
    String mobileNumberLength;
    String countyCode;
    ImageView ivFlag;
    TextView tvCountryCode;
    LinearLayout llMobileCountyCode;
    TextWatcher watcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_mobile_number_for_reset_p_i_n);
        setToolBar();
        context= ResetPINActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llMobileCountyCode.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type","onboarding");
            countyCodeSelectionListener.launch(in);
        });
        tvContinue.setOnClickListener(view -> {
            String userMobileNumber=commonFunctions.getCountryCode()+commonFunctions.getMobile();
            String enteredMobileNumber=countyCode+mobileNumber;
            if(enteredMobileNumber.equalsIgnoreCase(userMobileNumber)){
                sendOtpAPICall();
            }else {
                errorDialog.show("Please enter the registered mobile number");
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
                    tvContinue.setEnabled(true);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_one);
                }else{
                    tvContinue.setEnabled(false);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        successDialog=new SuccessDialog(context,"Other");
        edtMobile = findViewById(R.id.edt_mobile);
        edtMobile.addTextChangedListener(new TextFormatter(edtMobile, getString(R.string.mobile_number_formating_code)));
        edtMobile.requestFocus();
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag);
        llMobileCountyCode=findViewById(R.id.ll_mobile_county_code);
        countyCode = SthiramValues.DEFAULT_COUNTY_MOBILE_CODE;
        mobileNumberLength= SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH;
        setFlag(SthiramValues.DEFAULT_COUNTY_FLAG);
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);
    }


    private void setFlag(String flag) {

        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.reset_pin);
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

    private void mUpdatePIN(String pin) {
        apiCall.updatePin(updatePinRetry, commonFunctions.getUserId(), pin, commonFunctions.getCountryCode() + commonFunctions.getMobile(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setPIN(pin);
                        successDialog.show(getString(R.string.pin_chnaged_suucessfully));
                    } else {
                        errorDialog.show(apiResponse.getMessage());

                    }
                } catch (Exception e) {
                    somethingWentWrongDialog.show();                }
            }

            @Override
            public void retry() {
                updatePinRetry=true;
                mUpdatePIN(pin);
            }
        });
    }


    private void sendOtpAPICall() {
        apiCall.sendOTPForLogin("SMS",sendOtpRetry,commonFunctions.getFCMToken(),mobileNumber,countyCode,"",true, new CustomCallback() {
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
                        in.putExtra("country_code",countyCode);
                        in.putExtra("otpId",apiResponse.getOtp_id());
                        otpVerificationListener.launch(in);
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
                sendOtpRetry=true;
                sendOtpAPICall();
            }
        });
    }

    private void setMobileNumberFormat(String format) {
        if(watcher!=null){
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

    ActivityResultLauncher<Intent> otpVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            openPINActivity();
        }
    });

    private void openPINActivity() {
        Intent in=new Intent(context, CreatePINActivity.class);
        in.putExtra("from","reset");
        createPinListener.launch(in);
    }

    ActivityResultLauncher<Intent> createPinListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String pin=result.getData().getStringExtra("pin");
            mUpdatePIN( pin);
        }
    });

    ActivityResultLauncher<Intent> countyCodeSelectionListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            countyCode = result.getData().getStringExtra("country_code");
            mobileNumberLength = result.getData().getStringExtra("mobile_number_length");
            String flag= result.getData().getStringExtra("flag");
            setMobileNumberFormat(result.getData().getStringExtra("format"));
            tvCountryCode.setText(countyCode);
            setFlag(flag);
            edtMobile.setText("");
        }
    });

}