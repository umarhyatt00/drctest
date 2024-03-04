package com.yeel.drc.activity.signup;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.yeel.drc.R;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

import java.util.Objects;

public class TaxIdNumberActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    TextView tvContinue;
    String taxId;
    TextInputEditText edtOne, edtTwo,edtThree;
    boolean validOne=false,validTwo=false,validThree=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_id_number);
        setToolBar();
        context=TaxIdNumberActivity.this;
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
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

    private void validateValues() {
        if(validOne&&validTwo&&validThree){
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        }else{
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void initView() {
        edtOne=findViewById(R.id.edt_one);
        edtTwo =findViewById(R.id.edt_two);
        edtThree=findViewById(R.id.edt_three);
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        edtOne.requestFocus();
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            taxId= Objects.requireNonNull(edtOne.getText()).toString().trim()+ Objects.requireNonNull(edtTwo.getText()).toString().trim()+ Objects.requireNonNull(edtThree.getText()).toString().trim();
            commonFunctions.setPage("tax_id");
            signUpData.setTaxId(taxId);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, EmailAddressActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
        edtOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = Objects.requireNonNull(edtOne.getText()).toString().trim();
                if (value.length() == 4) {
                    validOne = true;
                    edtTwo.requestFocus();
                } else {
                    validOne = false;
                }
                validateValues();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value= Objects.requireNonNull(edtTwo.getText()).toString().trim();
                if(value.length()==4){
                    validTwo=true;
                    edtThree.requestFocus();
                    validateValues();
                }else if(value.length()==0){
                    validTwo=false;
                    validateValues();
                    edtOne.requestFocus();
                }else{
                    validTwo=false;
                    validateValues();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value= Objects.requireNonNull(edtThree.getText()).toString().trim();
                if(value.length()==3){
                    validThree=true;
                    validateValues();
                }else if(value.length()==0){
                    validThree=false;
                    validateValues();
                    edtTwo.requestFocus();
                }else{
                    validThree=false;
                    validateValues();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
            commonFunctions.setPage("tax_id");
            signUpData.setTaxId("");
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, EmailAddressActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);

        });
        tvTitle.setText("");
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id != android.R.id.home) {
            return false;
        }
        if (isTaskRoot()) {
            clearSignUpProgressDialog.show();
        } else {
            finish();
        }
        return true;
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