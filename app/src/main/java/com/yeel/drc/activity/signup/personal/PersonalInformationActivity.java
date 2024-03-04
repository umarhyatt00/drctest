package com.yeel.drc.activity.signup.personal;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.signup.SignUpResponseData;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;
import com.yeel.drc.utils.TextFormatter;

public class PersonalInformationActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    TextInputLayout tilFirstName;
    TextInputEditText edtFirstName;
    TextInputLayout tilMiddleName;
    TextInputEditText edtMiddleName;
    TextInputLayout tilLastName;
    TextInputEditText edtLastName;
    TextInputLayout tilDOB;
    TextInputEditText edtDOB;
    TextView tvContinue;
    String firstName;
    String middleName="";
    String lastName;
    boolean validFirstName =false;
    boolean validMiddleName =true;
    boolean validLastName =false;
    ErrorDialog errorDialog;
    APICall apiCall;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean userSignUpRetry=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        context=PersonalInformationActivity.this;
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
        }
        setToolBar();
        initView();
        setItemListeners();
    }

    private void initView() {
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        tilFirstName=findViewById(R.id.til_first_name);
        edtFirstName=findViewById(R.id.edt_first_name);
        tilMiddleName=findViewById(R.id.til_middle_name);
        edtMiddleName=findViewById(R.id.edt_middle_name);
        tilLastName=findViewById(R.id.til_last_name);
        edtLastName=findViewById(R.id.edt_last_name);
        tilDOB=findViewById(R.id.til_dob);
        edtDOB=findViewById(R.id.edt_dob);
        edtDOB.addTextChangedListener(new TextFormatter(edtDOB,"XX/XX/XXXX"));
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> callSignUpAPI());

        //validation first name
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firstName = edtFirstName.getText().toString().trim();
                if (commonFunctions.validateFistName(firstName)) {
                    validFirstName=true;
                    tilFirstName.setError(null);
                }else{
                    validFirstName=false;
                    if(tilFirstName.getError()==null||tilFirstName.getError().equals("")){
                        tilFirstName.setError(getString(R.string.enter_valid_first_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate middle name
        edtMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                middleName=edtMiddleName.getText().toString().trim();
                if(commonFunctions.validateMiddleName(middleName)){
                    validMiddleName=true;
                    tilMiddleName.setError(null);
                }else{
                    validMiddleName=false;
                    if(tilMiddleName.getError()==null||tilMiddleName.getError().equals("")){
                        tilMiddleName.setError(getString(R.string.enter_valid_middle_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate last name
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastName=edtLastName.getText().toString().trim();
                if(commonFunctions.validateLastName(lastName)){
                    validLastName=true;
                    tilLastName.setError(null);
                }else{
                    validLastName=false;
                    if(tilLastName.getError()==null||tilLastName.getError().equals("")){
                        tilLastName.setError(getString(R.string.enter_valid_last_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void callSignUpAPI() {
        apiCall.userSignUp(userSignUpRetry,
                signUpData.getCurrencyId(),
                firstName,
                middleName,
                lastName,
                signUpData.getMobileNumber(),
                "",
                "",
                signUpData.getAccountType(),
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
                signUpData.getCountryCode(),
                true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try{
                            String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            SignUpResponseData apiResponse=gson.fromJson(jsonString, SignUpResponseData.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){

                                commonFunctions.setPage("personal_info");
                                signUpData.setUserId(apiResponse.getUser_id());
                                signUpData.setFirstName(firstName);
                                signUpData.setMiddleName(middleName);
                                signUpData.setLastName(lastName);
                                if(signUpData.getCurrencyId().equals("1")){
                                    signUpData.setPreApprovedLimit(apiResponse.getPreapproved_limit_SSP());
                                }else{
                                    signUpData.setPreApprovedLimit(apiResponse.getPreapproved_limit_USD());
                                }
                                registerFunctions.saveRegisterDetails(signUpData);
                                Intent in = new Intent(context, UploadPersonalDocumentActivity.class);
                                in.putExtra("signUpData", signUpData);
                                startActivity(in);
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
                        userSignUpRetry=true;
                        callSignUpAPI();
                    }
                });
    }

    private void checkAllValid() {
        if(validFirstName&&validMiddleName&&validLastName){
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        }else{
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
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


}