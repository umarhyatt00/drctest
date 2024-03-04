package com.yeel.drc.activity.signup;

import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.agent.AgentTypeSelectionActivity;
import com.yeel.drc.activity.signup.business.BusinessInformationActivity;
import com.yeel.drc.activity.signup.personal.PersonalInformationActivity;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class SelectAccountTypeActivity extends BaseActivity {
    Context context;
    LinearLayout llIndividual;
    LinearLayout llBusiness;
    LinearLayout llAgent;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account_type);
        context=SelectAccountTypeActivity.this;
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        signUpData=(SignUpData) getIntent().getSerializableExtra("signUpData");
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
        }
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llIndividual.setOnClickListener(view -> {
            llIndividual.setEnabled(false);
            registerFunctions.clearSignUpProgress();
            commonFunctions.setPage("account_type_i");
            signUpData.setAccountType(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, PersonalInformationActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
        llBusiness.setOnClickListener(view -> {
            llBusiness.setEnabled(false);
            registerFunctions.clearSignUpProgress();
            commonFunctions.setPage("account_type_b");
            signUpData.setAccountType(SthiramValues.ACCOUNT_TYPE_BUSINESS);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, BusinessInformationActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
        llAgent.setOnClickListener(view -> {
            llAgent.setEnabled(false);
            registerFunctions.clearSignUpProgress();
            Intent in=new Intent(context, AgentTypeSelectionActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        llIndividual.setEnabled(true);
        llBusiness.setEnabled(true);
        llAgent.setEnabled(true);
    }

    private void initView() {
       llIndividual=findViewById(R.id.ll_individual);
       llBusiness=findViewById(R.id.ll_business);
       llAgent=findViewById(R.id.ll_agent);
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
            if (isTaskRoot()) {
                clearSignUpProgressDialog.show();
            } else {
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