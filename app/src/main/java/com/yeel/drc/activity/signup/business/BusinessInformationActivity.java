package com.yeel.drc.activity.signup.business;

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
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.EmailAddressActivity;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class BusinessInformationActivity extends BaseActivity {
    SignUpData signUpData;
    Context context;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    TextView tvContinue;
    TextInputLayout tilBusinessName;
    TextInputEditText edtBusinessName;
    TextInputLayout tilAuthorizedPersonOne;
    TextInputEditText edtAuthorizedPersonOne;
    TextInputLayout tilAuthorizedPersonTwo;
    TextInputEditText edtAuthorizedPersonTwo;
    TextInputLayout tilTaxId;
    TextInputEditText edtTaxId;
    String businessName;
    String authorizedPersonOne;
    String authorizedPersonTwo;
    String companyRegNumber;

    boolean validBusinessName =false;
    boolean validAuthorizedPersonOne =false;
    boolean validAuthorizedPersonTwo=true;
    boolean validTaxId =false;
    ErrorDialog errorDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_information);
        context=BusinessInformationActivity.this;
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
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
        tilBusinessName=findViewById(R.id.til_business_name);
        edtBusinessName=findViewById(R.id.edt_business_name);
        tilAuthorizedPersonOne=findViewById(R.id.til_authorized_person_one);
        edtAuthorizedPersonOne=findViewById(R.id.edt_authorized_person_one);
        tilAuthorizedPersonTwo=findViewById(R.id.til_authorized_person_two);
        edtAuthorizedPersonTwo=findViewById(R.id.edt_authorized_person_two);
        tilTaxId=findViewById(R.id.til_tax_id);
        edtTaxId=findViewById(R.id.edt_tax_id);

    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            if(!authorizedPersonOne.equals(authorizedPersonTwo)){
                commonFunctions.setPage("business_info");
                signUpData.setBusinessName(businessName);
                signUpData.setAuthorizedPersonOne(authorizedPersonOne);
                signUpData.setAuthorizedPersonTwo(authorizedPersonTwo);
                signUpData.setCompanyRegistrationNumber(companyRegNumber);
                registerFunctions.saveRegisterDetails(signUpData);

                Intent in=new Intent(context, EmailAddressActivity.class);
                in.putExtra("signUpData",signUpData);
                startActivity(in);
            }else{
                errorDialog.show(getString(R.string.autherized_person_validation));
            }

        });

        edtBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                businessName = edtBusinessName.getText().toString().trim();
                if (commonFunctions.validateBusinessName(businessName)) {
                    validBusinessName=true;
                    tilBusinessName.setError(null);
                }else{
                    validBusinessName=false;
                    if(tilBusinessName.getError()==null||tilBusinessName.getError().equals("")){
                        tilBusinessName.setError(getString(R.string.enter_valid_business_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtAuthorizedPersonOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                authorizedPersonOne = edtAuthorizedPersonOne.getText().toString().trim();
                if (commonFunctions.validateAuthorizedPersonOne(authorizedPersonOne)) {
                    validAuthorizedPersonOne=true;
                    tilAuthorizedPersonOne.setError(null);
                }else{
                    validAuthorizedPersonOne=false;
                    if(tilAuthorizedPersonOne.getError()==null||tilAuthorizedPersonOne.getError().equals("")){
                        tilAuthorizedPersonOne.setError(getString(R.string.enter_valid_authorized_person_1));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtAuthorizedPersonTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                authorizedPersonTwo = edtAuthorizedPersonTwo.getText().toString().trim();
                if (commonFunctions.validateAuthorizedPersonTwo(authorizedPersonTwo)) {
                    validAuthorizedPersonTwo=true;
                    tilAuthorizedPersonTwo.setError(null);
                }else{
                    validAuthorizedPersonTwo=false;
                    if(tilAuthorizedPersonTwo.getError()==null||tilAuthorizedPersonTwo.getError().equals("")){
                        tilAuthorizedPersonTwo.setError(getString(R.string.enter_valid_authorized_person_2));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtTaxId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                companyRegNumber = edtTaxId.getText().toString().trim();
                if (commonFunctions.validateTaxId(companyRegNumber)) {
                    validTaxId=true;
                    tilTaxId.setError(null);
                }else{
                    validTaxId=false;
                    if(tilTaxId.getError()==null||tilTaxId.getError().equals("")){
                        tilTaxId.setError(getString(R.string.valid_company_reg_number));
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
        if(validBusinessName&&validAuthorizedPersonOne&&validAuthorizedPersonTwo&&validTaxId){
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
        switch (id) {
            case android.R.id.home:
                if(isTaskRoot()){
                    clearSignUpProgressDialog.show();
                }else {
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
        }else {
            finish();
        }
    }
}