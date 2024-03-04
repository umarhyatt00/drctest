package com.yeel.drc.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.activity.main.settings.GetHelpActivity;
import com.yeel.drc.activity.signup.MobileNumberEnterActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;

public class ThudakkamActivity extends BaseActivity {
    Context context;
    TextView tvRegister;
    EditText edtMobile;
    TextView tvContinue;
    APICall apiCall;
    Boolean sendOtpRetry = false;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    private long mLastClickTime = 0;
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
        setContentView(R.layout.activity_login);
        context = ThudakkamActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions.setUserId("");
        commonFunctions.setAccessToken("");
        sendOtpRetry = false;
        commonFunctions.setPage("");
        tvRegister = findViewById(R.id.tv_register);
        edtMobile = findViewById(R.id.edt_mobile);
        edtMobile.requestFocus();
        tvContinue = findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag);
        llMobileCountyCode=findViewById(R.id.ll_mobile_county_code);
        countyCode = SthiramValues.DEFAULT_COUNTY_MOBILE_CODE;
        mobileNumberLength= SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH;
        setFlag(SthiramValues.DEFAULT_COUNTY_FLAG);
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);
    }

    private void setMobileNumberFormat(String format) {
        Log.e("Format",format+"    "+mobileNumberLength);
        if(watcher!=null){
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }


    private void setItemListeners() {
        llMobileCountyCode.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type","onboarding");
            countrySelectionListener.launch(in);
        });
        tvRegister.setOnClickListener(view -> {
            Intent in = new Intent(context, MobileNumberEnterActivity.class);
            startActivity(in);
            finish();
        });
        tvContinue.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            sendOtpAPICall();
        });
        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = edtMobile.getText().toString().trim();
                mobileNumber = number.replaceAll("\\D", "");
                if (commonFunctions.validateMobileNumberDynamic(mobileNumber,mobileNumberLength)) {
                    tvContinue.setEnabled(true);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_one);
                } else {
                    tvContinue.setEnabled(false);
                    tvContinue.setBackgroundResource(R.drawable.bg_button_three);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void sendOtpAPICall() {
        apiCall.sendOTPForLogin("SMS",sendOtpRetry,commonFunctions.getFCMToken(), mobileNumber, countyCode,"", true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        Intent in = new Intent(context, LoginOtpVerificationActivity.class);
                        in.putExtra("mobileNumber", mobileNumber);
                        in.putExtra("otpId", apiResponse.getOtp_id());
                        in.putExtra("country_code",countyCode);
                        startActivity(in);
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                sendOtpRetry = true;
                sendOtpAPICall();
            }
        });
    }

    private void checkValidMobile() {
        apiCall.checkValidMobile(sendOtpRetry,commonFunctions.getFCMToken(), mobileNumber, countyCode, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        Intent intent = new Intent(context, ReturningUserActivity.class);
                        intent.putExtra("fromLoginWithPIN", true);
                        intent.putExtra("mobileNumber",mobileNumber);
                        intent.putExtra("countryCode",countyCode);
                        startActivity(intent);
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                sendOtpRetry = true;
                sendOtpAPICall();
            }
        });
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_info_question, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.id_question) {
            Intent in = new Intent(context, GetHelpActivity.class);
            startActivity(in);
            return true;
        }
        return false;
    }

    ActivityResultLauncher<Intent> countrySelectionListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
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