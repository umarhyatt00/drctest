package com.yeel.drc.activity.main.mobilepay;

import static com.yeel.drc.utils.SthiramValues.KENYA_COUNTY_FLAG;
import static com.yeel.drc.utils.SthiramValues.KENYA_COUNTY_MOBILE_CODE;
import static com.yeel.drc.utils.SthiramValues.KENYA_COUNTY_MOBILE_CODE_WITHOUT_PLUS;
import static com.yeel.drc.utils.SthiramValues.KENYA_MOBILE_NUMBER_FORMAT;
import static com.yeel.drc.utils.SthiramValues.KENYA_MOBILE_NUMBER_LENGTH;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.mpesadetailsfrommobile.VerifyMobileResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.mpesa.MobilePayData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;

public class MobilePayMobileEnterActivity extends BaseActivity {
    MobilePayData mobilePayData;
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    EditText edtMobile;
    TextView tvContinue;
    ImageView ivFlag;
    TextView tvCountryCode;
    String mobileNumber;
    boolean paymentRequestValidateRetry = false;
    TextView tvHeading;

    String countyCode="";
    String countyCodeWithOutPlus="";
    String mobileNumberLength="";
    TextWatcher watcher;
    boolean validMobile=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_mobile_enter);
        context = MobilePayMobileEnterActivity.this;
        mobilePayData = (MobilePayData) getIntent().getSerializableExtra("data");
        setToolBar();
        initView();
        setItemListeners();
    }


    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            paymentRequestValidate();
        });
        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = edtMobile.getText().toString().trim();
                mobileNumber = number.replaceAll("\\D", "");
                if (commonFunctions.validateMobileNumberDynamic(mobileNumber,KENYA_MOBILE_NUMBER_LENGTH)) {
                    validMobile=true;
                } else {
                   validMobile=false;
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        edtMobile = findViewById(R.id.edt_mobile);
        tvContinue = findViewById(R.id.tv_continue);
        edtMobile.addTextChangedListener(new TextFormatter(edtMobile, KENYA_MOBILE_NUMBER_FORMAT));
        edtMobile.requestFocus();
        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag);
        tvHeading=findViewById(R.id.tv_heading);

        checkAllValid();
        if(mobilePayData.getMobilePayCode().equals(SthiramValues.MPESA_KENYA_CODE)){
            tvHeading.setText(R.string.enter_your_beneficiary_s_m_pesa_mobile_number);
            setMobileNumberDetails(KENYA_MOBILE_NUMBER_FORMAT,
                    KENYA_COUNTY_MOBILE_CODE,
                    KENYA_COUNTY_MOBILE_CODE_WITHOUT_PLUS,
                    KENYA_MOBILE_NUMBER_LENGTH,
                    KENYA_COUNTY_FLAG);
        }else if(mobilePayData.getMobilePayCode().equals(SthiramValues.AIRTAL_UGANDA_CODE)){
            tvHeading.setText(R.string.airetel_uganda_heading);
            setMobileNumberDetails(SthiramValues.UGANDA_MOBILE_NUMBER_FORMAT,
                    SthiramValues.UGANDA_COUNTY_MOBILE_CODE,
                    SthiramValues.UGANDA_COUNTY_MOBILE_CODE_WITHOUT_PLUS,
                    SthiramValues.UGANDA_MOBILE_NUMBER_LENGTH,
                    SthiramValues.UGANDA_COUNTY_FLAG);
        }


    }

    private void setMobileNumberDetails(String format,String code,String codeWithoutPlus,String length,String flag) {
        countyCodeWithOutPlus=codeWithoutPlus;
        setMobileNumberFormat(format);
        countyCode =code;
        tvCountryCode.setText(countyCode);
        mobileNumberLength=length;
        setFlag(flag);
    }

    private void setMobileNumberFormat(String format) {
        if(watcher!=null){
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

    private void checkAllValid() {
        if (validMobile) {
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void paymentRequestValidate() {
        apiCall.paymentRequestValidate(
                mobilePayData.getMobilePayCode(),
                paymentRequestValidateRetry,
                true,
                mobilePayData.getSendAmount(),
                mobileNumber,
                commonFunctions.getUserId(),
                commonFunctions.getFullName(),
                countyCodeWithOutPlus,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                          /*  if(mobilePayData.getMobilePayCode().equals(Constants.MPESA_KENYA_CODE)){*/
                                VerifyMobileResponse apiResponse = gson.fromJson(jsonString, VerifyMobileResponse.class);
                                if(apiResponse.getStatus().equals(SthiramValues.SUCCESS)){
                                    mobilePayData.setYeeltid(apiResponse.getYeeltid());
                                    mobilePayData.setReceiverDetails(apiResponse.getData());
                                    callIntent();
                                }else{
                                    errorDialog.show(apiResponse.getMessage());
                                }
                       /*     }else if(mobilePayData.getMobilePayCode().equals(Constants.AIRTAL_UGANDA_CODE)){
                                VerifyMobileResponse apiResponse = gson.fromJson(jsonString, VerifyMobileResponse.class);
                                if(apiResponse.getStatus().equals(Constants.SUCCESS)){
                                    mobilePayData.setYeeltid(apiResponse.getYeeltid());
                                    VerifyMobileData data=new VerifyMobileData();
                                    data.setCustomerName("Jojo");
                                    data.setConversationID("123");
                                    data.setMmTransactionID("123456");
                                    data.setPartnerTransactionID("12345678");
                                    mobilePayData.setReceiverDetails(data);
                                    callIntent();
                                }else{
                                    errorDialog.show(apiResponse.getMessage());
                                }
                            }*/

                        }catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        paymentRequestValidateRetry = true;
                        paymentRequestValidate();
                    }
                }

        );
    }

    private void callIntent() {
        mobilePayData.setMobileNumber(mobileNumber);
        mobilePayData.setMobileCountyCode(countyCode);
        mobilePayData.setReceiverCountryCodeWithOutPlus(countyCodeWithOutPlus);
        Intent intent = new Intent(getApplicationContext(), MobilePayConfirmationActivity.class);
        intent.putExtra("data", mobilePayData);
        startActivity(intent);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }
    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
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
}