package com.yeel.drc.activity.signup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CommonMobileOTPVerificationActivity;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.activity.common.LanguageSelectionActivity;
import com.yeel.drc.activity.login.ThudakkamActivity;
import com.yeel.drc.activity.login.WelcomeBackActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;
import com.yeel.drc.utils.SharedPrefUtil;
import com.yeel.drc.utils.TextFormatter;

public class MobileNumberEnterActivity extends BaseActivity {
    Context context;
    RegisterFunctions registerFunctions;
    TextView tvContinue;
    EditText edtMobile;
    LinearLayout llLogin;
    TextView tvLogin;
    LinearLayout llLanguage;
    ImageView ivLanLogo;
    TextView tvLanName;
    RelativeLayout rlMain;
    String mobileNumber;
    String mobileNumberLength;
    String countyCode;
    ImageView ivFlag;
    TextView tvCountryCode;
    LinearLayout llMobileCountyCode;
    SignUpData signUpData;
    APICall apiCall;
    Boolean sendOtpRetry = false;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    SharedPrefUtil sharedPrefUtil;
    private long mLastClickTime = 0;
    TextWatcher watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_enter);
        context = MobileNumberEnterActivity.this;
        sharedPrefUtil = new SharedPrefUtil(context);
        signUpData = new SignUpData();
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        initView();
        setItemListeners();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        sendOtpRetry = false;
        registerFunctions = new RegisterFunctions(context);
        tvContinue = findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        edtMobile = findViewById(R.id.maskedEditText);
        edtMobile.requestFocus();
        tvLogin = findViewById(R.id.tv_sign_in);
        llLogin = findViewById(R.id.ll_login);
        llLanguage = findViewById(R.id.ll_language);
        ivLanLogo = findViewById(R.id.iv_lan_logo);
        tvLanName = findViewById(R.id.tv_lan_name);
        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag);
        llMobileCountyCode=findViewById(R.id.ll_mobile_county_code);

        countyCode = SthiramValues.DEFAULT_COUNTY_MOBILE_CODE;
        mobileNumberLength= SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH;

        setFlag(SthiramValues.DEFAULT_COUNTY_FLAG);
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);


        if (sharedPrefUtil.getEncryptedStringPreference("LANGUAGE", "en").equals("en")) {
            tvLanName.setText("EN");
            ivLanLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_us_logo_png));
        }else {
            tvLanName.setText("FR");
            ivLanLogo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_country_logo));
        }
        rlMain=findViewById(R.id.rl_main);
    }

    private void setItemListeners() {
        llMobileCountyCode.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type","onboarding");
            countryCodeSelectionListener.launch(in);
        });
        tvLogin.setOnClickListener(view -> {
            Intent in = new Intent(context, ThudakkamActivity.class);
            startActivity(in);
            finish();
        });
        llLogin.setOnClickListener(view -> {
            Intent in = new Intent(context, ThudakkamActivity.class);
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
        llLanguage.setOnClickListener(view -> {
            Intent in = new Intent(context, LanguageSelectionActivity.class);
            languageChangeListener.launch(in);
        });
    }


    ActivityResultLauncher<Intent> languageChangeListener=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            startActivity(Intent.makeRestartActivityTask(getIntent().getComponent()));
        }
    });

    private void sendOtpAPICall() {
        apiCall.sendOTP("SMS",sendOtpRetry, commonFunctions.getFCMToken(),mobileNumber, countyCode,"", true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        Intent in = new Intent(context, CommonMobileOTPVerificationActivity.class);
                        in.putExtra("mobileNumber", mobileNumber);
                        in.putExtra("otpId", apiResponse.getOtp_id());
                        in.putExtra("country_code",countyCode);
                        otpVerificationListener.launch(in);
                    } else {
                        if (apiResponse.getError_type() != null && apiResponse.getError_type().equals("existing user")) {
                            Intent in = new Intent(MobileNumberEnterActivity.this, WelcomeBackActivity.class);
                            startActivity(in);
                        } else {
                            errorDialog.show(apiResponse.getMessage());
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
                sendOtpRetry = true;
                sendOtpAPICall();
            }
        });
    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
        tvCountryCode.setText(countyCode);
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
            registerFunctions.clearSignUpProgress();
            commonFunctions.setPage("mobile_enter");
            signUpData.setCountryCode(countyCode);
            signUpData.setMobileNumber(mobileNumber);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in = new Intent(MobileNumberEnterActivity.this, ChooseLinkOrNewActivity.class);
            in.putExtra("signUpData", signUpData);
            startActivity(in);
        }
    });

    ActivityResultLauncher<Intent> countryCodeSelectionListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            countyCode = result.getData().getStringExtra("country_code");
            mobileNumberLength = result.getData().getStringExtra("mobile_number_length");
            String flag= result.getData().getStringExtra("flag");
            setMobileNumberFormat(result.getData().getStringExtra("format"));
            tvCountryCode.setText(countyCode);
            edtMobile.setText("");
            setFlag(flag);
        }
    });

}