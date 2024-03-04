package com.yeel.drc.activity.main.settings.businessaccount;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeel.drc.R;
import com.yeel.drc.activity.main.settings.EditEmailActivity;
import com.yeel.drc.activity.main.settings.EditMobileActivity;
import com.yeel.drc.activity.main.settings.agent.EditAgentAddressActivity;
import com.yeel.drc.activity.main.settings.agent.EditAgentInfoActivity;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class ViewBusinessDetailsActivity extends BaseActivity {
    Context context;
    LinearLayout llBusinessName,llNames,llMobile,llEmail,llAddress;
    TextView tvBusinessName;
    TextView tvPartnersName;
    TextView tvMobileNumber;
    TextView tvEmail;
    TextView tvAddress;

    final static int UPDATE_ADDRESS= 101;
    final static int UPDATE_EMAIL= 102;
    final static int UPDATE_MOBILE= 103;
    final static int UPDATE_AUTHORIZED_PERSONS = 104;
    final static int UPDATE_TAX_ID= 105;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_buinsess_details);
        setToolBar();
        context= ViewBusinessDetailsActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llNames.setOnClickListener(view -> {
            if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                    Intent in=new Intent(context, EditAgentInfoActivity.class);
                    startActivityForResult(in,UPDATE_AUTHORIZED_PERSONS);
                }
            }else{
                Intent in=new Intent(context,EditBusinessInfoActivity.class);
                startActivityForResult(in,UPDATE_AUTHORIZED_PERSONS);
            }
        });
        llMobile.setOnClickListener(view -> {
            Intent in=new Intent(context, EditMobileActivity.class);
            startActivityForResult(in,UPDATE_MOBILE);
        });
        llEmail.setOnClickListener(view -> {
            Intent in=new Intent(context, EditEmailActivity.class);
            startActivityForResult(in,UPDATE_EMAIL);
        });
        llAddress.setOnClickListener(view -> {
            if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                    Intent in=new Intent(context, EditAgentAddressActivity.class);
                    startActivityForResult(in,UPDATE_ADDRESS);
                }
            }else{
                Intent in=new Intent(context, EditBusinessAddressActivity.class);
                startActivityForResult(in,UPDATE_ADDRESS);
            }

        });

    }

    private void initView() {
        llBusinessName=findViewById(R.id.ll_business_name);
         llNames=findViewById(R.id.ll_names);
         llMobile=findViewById(R.id.ll_mobile);
         llEmail=findViewById(R.id.ll_email);
         llAddress=findViewById(R.id.ll_address);
         tvBusinessName=findViewById(R.id.tv_business_name);
         tvPartnersName=findViewById(R.id.tv_partners_name);
         tvMobileNumber=findViewById(R.id.tv_mobile_number);
         tvEmail=findViewById(R.id.tv_email);
         tvAddress=findViewById(R.id.tv_address);
         setValues();
    }

    private void setValues() {
        tvBusinessName.setText(commonFunctions.getBusinessName());

        tvMobileNumber.setText(commonFunctions.formatAMobileNumber(commonFunctions.getMobile(),commonFunctions.getCountryCode(),commonFunctions.getMobileNumberFormat()));

        if(commonFunctions.getEmail()!=null&&!commonFunctions.getEmail().equals("")){
            tvEmail.setText(commonFunctions.getEmail());
        }else{
            tvEmail.setHint("Add your email");
        }
        String fullAddress = "";
        if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
            if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)){
                fullAddress=commonFunctions.getAddressLineOne();
                tvPartnersName.setText(commonFunctions.getAuthorizedPersonOne());
            }
        }else{
            if (commonFunctions.getAddressLineTwo() != null && !commonFunctions.getAddressLineTwo().equals("")) {
                fullAddress=commonFunctions.getAddressLineOne() + ", " + commonFunctions.getAddressLineTwo();
            } else {
                fullAddress=commonFunctions.getAddressLineOne();
            }
            tvPartnersName.setText(commonFunctions.getAuthorizedPersonOne() + ", " + commonFunctions.getAuthorizedPersonTwo());
        }
        if(commonFunctions.getDistrict()!=null&&!commonFunctions.getDistrict().equals("")){
            tvAddress.setText(fullAddress+"\n"+commonFunctions.getLocality()+", "+commonFunctions.getProvince()+", "+commonFunctions.getDistrict());
        }else{
            tvAddress.setHint(R.string.add_your_address);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UPDATE_ADDRESS) {
                setValues();
            }else if(requestCode == UPDATE_EMAIL){
                setValues();
            }else if(requestCode==UPDATE_MOBILE){
                setValues();
            }else if(requestCode==UPDATE_AUTHORIZED_PERSONS){
                setValues();
            }else if(requestCode==UPDATE_TAX_ID){
                setValues();
            }
        }

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.edit_business_info);
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