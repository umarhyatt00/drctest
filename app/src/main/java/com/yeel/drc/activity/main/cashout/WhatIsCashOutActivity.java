package com.yeel.drc.activity.main.cashout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.homepayandsend.ScanAndPayActivity;
import com.yeel.drc.activity.main.agent.addfund.AgentLocatorActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.PermissionUtils;

import java.util.List;
import java.util.Objects;

public class WhatIsCashOutActivity extends BaseActivity {
    Context context;
    RelativeLayout rlAgentLocator;
    private TextView mBtnScanQr;
    TextView tvShowBalance;
    public static final int SCAN_INTENT_ID = 101;
    TextView tvCurrencySymbol;

    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    boolean getUserDetailsUsingQrCodeRetry = false;
    PermissionUtils permissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_out);
        setToolBar();
        context= WhatIsCashOutActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlAgentLocator.setOnClickListener(view -> {
            Intent in=new Intent(context, AgentLocatorActivity.class);
            startActivity(in);
        });
        mBtnScanQr.setOnClickListener(view -> {
            if(permissionUtils.checkCameraPermissionForScan(requestPermissionLauncherCamera)){
                Intent in=new Intent(context, ScanAndPayActivity.class);
                in.putExtra("from","cashOut");
                startActivityForResult(in,SCAN_INTENT_ID);
            }

        });

    }

    //camera permission
    private ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent in=new Intent(context, ScanAndPayActivity.class);
                    in.putExtra("from","cashOut");
                    startActivityForResult(in,SCAN_INTENT_ID);
                }
            });

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);
        tvCurrencySymbol=findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        rlAgentLocator=findViewById(R.id.rl_agentLocator);
        mBtnScanQr = (TextView) findViewById(R.id.tv_scan_and_cashOut);
        tvShowBalance=findViewById(R.id.tv_show_bal_cashOut);
        tvShowBalance.setText(commonFunctions.setAmount(commonFunctions.getUserAccountBalance()));
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cash_out);
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
                mDialogAgentInfo();
                return true;

        }
        return false;
    }

    private void mDialogAgentInfo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_cashout_info);
        TextView description = dialog.findViewById(R.id.description);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.what_is_cash_out);
        description.setText(R.string.you_can_get_cash_by_visiting_any_of_the_yeel_delegates_agents_worldwide_just_scan_the_qr_code_or_enter_the_delegate_agent_code_and_cash_out);
        TextView dialogButton = dialog.findViewById(R.id.tv_ok);
        dialogButton.setOnClickListener(v -> {
            if(!isFinishing()||!isDestroyed()){
                dialog.dismiss();
            }

        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        if(!isFinishing()){
            dialog.show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCAN_INTENT_ID:
                assert data != null;
                String qrCode=data.getStringExtra("receiver_qr_code");
                if(qrCode!=null&&!qrCode.equals("")){
                    getUserDetailsAPI(qrCode);
                }
        }
    }

    private void getUserDetailsAPI(String qrCode) {
        apiCall.getUserDetailsUsingQrCode(getUserDetailsUsingQrCodeRetry,qrCode,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        UserDetailsData userDetails=apiResponse.getUserDetails();
                        if(userDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){

                            List<CurrencyListData> currencyList=apiResponse.getCurrencyList();
                            boolean isAccountNumberAvailable = false;
                            String receiverAccountNumber="";

                            if(currencyList!=null){
                                //check currency available or not
                                for(int i=0;i<currencyList.size();i++){
                                    if(commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())){
                                        receiverAccountNumber=currencyList.get(i).getAccount_number();
                                        isAccountNumberAvailable=true;
                                        break;
                                    }
                                }

                                if(isAccountNumberAvailable){
                                    AgentData agentData=new AgentData();
                                    if(userDetails.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                                        agentData.setFirstname(userDetails.getFirstname());
                                        agentData.setMiddlename(userDetails.getMiddlename());
                                        agentData.setMobile(userDetails.getMobile());
                                        agentData.setCountry_code(userDetails.getCountry_code());
                                        agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT);
                                        agentData.setLastname(userDetails.getLastname());
                                        agentData.setAccount_no(receiverAccountNumber);
                                        agentData.setId(userDetails.getId());
                                    }else{
                                        agentData.setBusiness_name(userDetails.getBusiness_name());
                                        agentData.setMobile(userDetails.getMobile());
                                        agentData.setCountry_code(userDetails.getCountry_code());
                                        agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT);
                                        agentData.setAccount_no(receiverAccountNumber);
                                        agentData.setId(userDetails.getId());
                                    }
                                    Intent intent= new Intent(context,CashOutAmountEnteringActivity.class);
                                    intent.putExtra("data", agentData);
                                    startActivity(intent);
                                }else{
                                    String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                                    errorDialog.show(message);
                                }

                            }else{
                                String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                            }


                        }else{
                            errorDialog.show("Invalid Agent");
                        }
                    } else {
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
                getUserDetailsUsingQrCodeRetry=true;
                getUserDetailsAPI(qrCode);
            }
        });
    }
}