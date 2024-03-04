package com.yeel.drc.activity.main.billpayments.internal;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.billpayments.BillConfirmationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.billpayments.BillPaymentsData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;
import java.util.Objects;

public class EducationInternalBillPaymentsActivity extends BaseActivity {
    BillPaymentsData billPaymentData;
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    TextView tvAmountSymbol;
    ImageView ivNext;
    TextView tvProviderName;
    ImageView ivProviderImage;
    TextInputLayout tilStudentName;
    EditText edtStudentName;
    TextInputLayout tilStudentId;
    EditText edtStudentId;
    EditText edtMobileNumber;
    EditText edtRemark;
    TextInputLayout tilRemark;
    EditText edtAmount;
    TextView tvBalance;
    TextView tvCountryCode;
    ImageView ivFlag;

    String studentName;
    String studentId;
    String remark;
    String amountToPay;

    boolean validStudentName=false;
    boolean validStudentId=false;
    boolean validMobileNumber=false;
    boolean validRemark=true;

    String studentMobileNumber;
    String countyCode="";
    String mobileNumberLength="";
    TextWatcher watcher;
    CommonFunctions commonFunctions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_internal_bill_payments);
        context=EducationInternalBillPaymentsActivity.this;
        commonFunctions=new CommonFunctions(context);
        billPaymentData =(BillPaymentsData) getIntent().getSerializableExtra("data");
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        ivNext.setOnClickListener(view -> {
            String value=edtAmount.getText().toString().trim();
            String message = commonFunctions.checkTransactionIsPossible(value,value, SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS);
            if (message.equals(SthiramValues.SUCCESS)) {
                amountToPay = edtAmount.getText().toString().trim();
                billPaymentData.setStudentName(studentName);
                billPaymentData.setStudentId(studentId);
                billPaymentData.setStudentMobileNumber(studentMobileNumber);
                billPaymentData.setStudentMobileCountyCode(countyCode);
                billPaymentData.setRemark(remark);
                billPaymentData.setAmount(amountToPay);
                Intent in = new Intent(context, BillConfirmationActivity.class);
                in.putExtra("data", billPaymentData);
                startActivity(in);
            }
        });
        edtMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = edtMobileNumber.getText().toString().trim();
                String value=number.replaceAll("\\D", "");
                if (commonFunctions.validateMobileNumber(value)) {
                    studentMobileNumber = value;
                    validMobileNumber=true;
                } else {
                    studentMobileNumber="";
                    validMobileNumber=false;
                }
                checkAllValid();
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtStudentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value=edtStudentName.getText().toString().trim();
                if (commonFunctions.validateAuthorizedPersonOne(value)) {
                    studentName = value;
                    validStudentName=true;
                    tilStudentName.setError(null);
                }else{
                    studentName="";
                    validStudentName=false;
                    if(tilStudentName.getError()==null||tilStudentName.getError().equals("")){
                        tilStudentName.setError(getString(R.string.enter_valid_student_name));
                        tilStudentName.setErrorEnabled(true);
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtStudentId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value=edtStudentId.getText().toString().trim();
                if (commonFunctions.validateStudentIDNumber(value)) {
                    studentId = value;
                    validStudentId=true;
                    tilStudentId.setError(null);
                }else{
                    studentId="";
                    validStudentId=false;
                    if(tilStudentId.getError()==null||tilStudentId.getError().equals("")){
                        tilStudentId.setError(getString(R.string.enter_valid_student_id));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value=edtRemark.getText().toString().trim();
                if (commonFunctions.validateRemark(value)) {
                    remark = value;
                    validRemark=true;
                    tilRemark.setError(null);
                }else{
                    remark="";
                    validRemark=false;
                    if(tilRemark.getError()==null||tilRemark.getError().equals("")){
                        tilRemark.setError(getString(R.string.enter_valid_remark));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkAllValid() {
        if(validStudentName&&validStudentId&&validMobileNumber&&validRemark){
            ivNext.setImageResource(R.drawable.ic_blue_next_arrow_with_white_arrow_without_shadow);
            ivNext.setEnabled(true);
        }else{
            ivNext.setImageResource(R.drawable.ic_blue_next_arrow_with_white_arrow_without_shadow_disabled);
            ivNext.setEnabled(false);

        }
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        tvAmountSymbol=findViewById(R.id.tv_amount_symbol);
        tvAmountSymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        ivNext=findViewById(R.id.iv_next);
        ivNext.setEnabled(false);
        tvProviderName=findViewById(R.id.tv_provider_name);
        ivProviderImage=findViewById(R.id.iv_provider_image);
        tilStudentName=findViewById(R.id.til_student_name);
        edtStudentName=findViewById(R.id.edt_student_name);
        tilStudentId=findViewById(R.id.til_student_id);
        edtStudentId=findViewById(R.id.edt_student_id);
        edtMobileNumber=findViewById(R.id.edt_mobile_number);
        edtMobileNumber.addTextChangedListener(new TextFormatter(edtMobileNumber, getString(R.string.mobile_number_formating_code)));
        edtRemark=findViewById(R.id.edt_remark);
        tilRemark=findViewById(R.id.til_remark);
        edtAmount=findViewById(R.id.edt_amount);
        tvBalance=findViewById(R.id.tvUserBalance);
        tvBalance.setText(commonFunctions.setAmount(commonFunctions.getUserAccountBalance())+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);

        tvCountryCode=findViewById(R.id.tv_county_code);
        ivFlag=findViewById(R.id.iv_flag_country_code);

        checkAllValid();
        setMobileNumberDetails(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT, SthiramValues.DEFAULT_COUNTY_MOBILE_CODE, SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH, SthiramValues.DEFAULT_COUNTY_FLAG);
        setProviderDetails();

        if(billPaymentData.getStudentName()!=null&&!billPaymentData.equals("")){
            setStudentDetails();
        }
    }

    private void setStudentDetails() {
        validStudentName=true;
        validStudentId=true;
        validMobileNumber=true;
        validRemark=true;

        studentName=billPaymentData.getStudentName();
        studentId=billPaymentData.getStudentId();
        countyCode=billPaymentData.getStudentMobileCountyCode();

        studentMobileNumber=billPaymentData.getStudentMobileNumber();
        edtStudentName.setText(billPaymentData.getStudentName());
        edtMobileNumber.setText(billPaymentData.getStudentMobileNumber());
        edtStudentId.setText(billPaymentData.getStudentId());

        checkAllValid();
    }

    private void setProviderDetails() {
        ProvidersListData providersListData=billPaymentData.getProvidersListData();
        tvProviderName.setText(billPaymentData.getProvidersListData().getProvider_name());
        Glide.with(ivProviderImage.getContext()).load(SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL+""+providersListData.getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProviderImage);
    }

    private void setMobileNumberDetails(String defaultMobileNumberFormat, String defaultCountyMobileCode, String defaultMobileNumberLength, String defaultCountyFlag) {
        setMobileNumberFormat(defaultMobileNumberFormat);
        countyCode =defaultCountyMobileCode;
        tvCountryCode.setText(countyCode);
        mobileNumberLength=defaultMobileNumberLength;
        setFlag(defaultCountyFlag);
    }

    private void setMobileNumberFormat(String format) {
        if(watcher!=null){
            edtMobileNumber.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(edtMobileNumber, format);
        edtMobileNumber.addTextChangedListener(watcher);
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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.enter_details);
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