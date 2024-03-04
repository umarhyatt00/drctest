package com.yeel.drc.activity.main.settings.individual;

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
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class ViewIndividualDetails extends BaseActivity {
    Context context;
    private TextView tvUserNAme;
    private TextView tvUserEmail;
    private TextView tvUserMobile;
    TextView tvAddress;
    LinearLayout llMobile,llEmail,llAddress;
    final static int UPDATE_ADDRESS= 101;
    final static int UPDATE_EMAIL= 102;
    final static int UPDATE_MOBILE= 103;
    LinearLayout llMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_details);
        setToolBar();
        context= ViewIndividualDetails.this;
        initView();
        setItemListeners();
        setValues();
    }

    private void setValues() {
        tvUserNAme.setText(commonFunctions.getFullName());
        tvUserMobile.setText(commonFunctions.formatAMobileNumber(commonFunctions.getMobile(),commonFunctions.getCountryCode(),commonFunctions.getMobileNumberFormat()));
        if(commonFunctions.getEmail()!=null&&!commonFunctions.getEmail().equals("")){
            tvUserEmail.setText(commonFunctions.getEmail());
        }else{
            tvUserEmail.setHint(R.string.add_your_email);
        }

        if(commonFunctions.getDistrict()!=null&&!commonFunctions.getDistrict().equals("")){
            if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                    tvAddress.setText(commonFunctions.getAddressLineOne()+"\n"+commonFunctions.getLocality()+", "+commonFunctions.getProvince()+", "+commonFunctions.getDistrict());
                }
            }else {
                tvAddress.setText(commonFunctions.getLocality()+", "+commonFunctions.getProvince()+", "+commonFunctions.getDistrict());

            }
        }
    }

    private void setItemListeners() {
        llMobile.setOnClickListener(view -> {
            Intent in=new Intent(context, EditMobileActivity.class);
            startActivityForResult(in, UPDATE_MOBILE);
        });
        llEmail.setOnClickListener(view -> {
            Intent in=new Intent(context, EditEmailActivity.class);
            startActivityForResult(in, UPDATE_EMAIL);
        });
        llAddress.setOnClickListener(view -> {
            if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                    Intent in=new Intent(context, EditAgentAddressActivity.class);
                    startActivityForResult(in, UPDATE_ADDRESS);
                }
            }else{
                Intent in=new Intent(context, EditIndividualAddressActivity.class);
                startActivityForResult(in, UPDATE_ADDRESS);
            }

        });

    }

    private void initView() {
        tvUserNAme = findViewById(R.id.tvUserNAme);
        tvUserMobile =  findViewById(R.id.tvUserMobile);
        tvUserEmail =  findViewById(R.id.tvUserEmail);
        tvAddress=findViewById(R.id.tv_address);

        llMobile=findViewById(R.id.ll_mobile);
        llEmail=findViewById(R.id.ll_email);
        llAddress=findViewById(R.id.ll_address);
        llMain=findViewById(R.id.ll_main);

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
        tvTitle.setText(R.string.edt_personal_info);
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