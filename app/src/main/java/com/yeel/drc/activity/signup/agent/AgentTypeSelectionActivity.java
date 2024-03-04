package com.yeel.drc.activity.signup.agent;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class AgentTypeSelectionActivity extends BaseActivity {
    SignUpData signUpData;
    Context context;
    LinearLayout llIndividual;
    LinearLayout llBusiness;
    RegisterFunctions registerFunctions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agnet_type_selection);
        context= AgentTypeSelectionActivity.this;
        signUpData=(SignUpData) getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        setToolBar();
        initView();
        setItemListeners();
        mDialogueCashOut();
    }

    private void setItemListeners() {
        llIndividual.setOnClickListener(view -> {
            commonFunctions.setPage("account_type_i_a");
            signUpData.setAccountType(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, IndividualAgentInformation.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
        llBusiness.setOnClickListener(view -> {
            commonFunctions.setPage("account_type_b_a");
            signUpData.setAccountType(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, BusinessAgentInformationActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });
    }

    private void initView() {
        llIndividual=findViewById(R.id.ll_individual);
        llBusiness=findViewById(R.id.ll_business);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_info_icon_gray, menu);
        return true;
    }


    //back button click
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_info:
                mDialogueCashOut();
                return true;

        }
        return false;
    }


    private void mDialogueCashOut() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_agent_details);
        TextView dialogButton = dialog.findViewById(R.id.tv_ok);
        TextView tvAboutIndividualAgent = dialog.findViewById(R.id.tv_about_individual_agent);
        TextView tvAboutBusinessAgent = dialog.findViewById(R.id.tv_about_business_agent);

        String labelIndividualAgent=getString(R.string.individual_agent);
        String messageIndividualAgent=getString(R.string.individual_agent_message);
        String aboutIndividualAgent="<font size='5' color='#4E576D'><b>"+labelIndividualAgent+"- </b></font>"+messageIndividualAgent;


        String labelBusinessAgent=getString(R.string.business_agent);
        String messageBusinessAgent=getString(R.string.business_agent_message);
        String aboutBusinessAgent="<font size='5' color='#4E576D'><b>"+labelBusinessAgent+"- </b></font>"+messageBusinessAgent;


        tvAboutIndividualAgent.setText(Html.fromHtml(aboutIndividualAgent));
        tvAboutBusinessAgent.setText(Html.fromHtml(aboutBusinessAgent));


        dialogButton.setOnClickListener(v -> {
            if (!isFinishing() || !isDestroyed()) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            dialog.show();
        }


    }
}