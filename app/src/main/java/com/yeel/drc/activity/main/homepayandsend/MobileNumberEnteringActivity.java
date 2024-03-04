package com.yeel.drc.activity.main.homepayandsend;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity;
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
import com.yeel.drc.utils.TextFormatter;

import java.util.List;

public class MobileNumberEnteringActivity extends BaseActivity {
    Context context;
    EditText mEdtMobile;
    ImageView mImgBrowse;
    ImageView mImgContinue;
    boolean validMobileNumber = false;
    String from;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    UserDetailsData userDetails;
    boolean getUserDetailsUsingMobileRetry = false;
    TextView tvError;
    PermissionUtils permissionUtils;

    String mobileNumber;
    String mobileNumberLength;
    String countyCode;
    TextView tvCountryCode;
    final static int SELECT_A_COUNTRY_CODE= 102;
    TextWatcher watcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_entering);
        setToolBar();
        from = getIntent().getStringExtra("from");
        context = MobileNumberEnteringActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvCountryCode.setOnClickListener(view -> {
            if (from.equals("pay")) {
                Intent in = new Intent(context, CountrySelectionActivity.class);
                in.putExtra("type","onboarding");
                startActivityForResult(in, SELECT_A_COUNTRY_CODE);
            }
        });
        mEdtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = mEdtMobile.getText().toString().trim();
                mobileNumber = number.replaceAll("\\D", "");
                    /*if(charSequence.toString().contains(" ")) {*/

                        if (commonFunctions.validateMobileNumberDynamic(mobileNumber,mobileNumberLength)) {
                            if (!mobileNumber.equals(commonFunctions.getMobile())) {
                                tvError.setVisibility(View.INVISIBLE);
                                getUserDetailsAPI(mobileNumber);
                            } else {
                                Toast.makeText(context, getString(R.string.canot_transfer), Toast.LENGTH_SHORT).show();
                                setError();
                            }
                        } else {
                            setError();
                        }
                    /*}*/

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mImgContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validMobileNumber) {
                    if (from.equals("pay")) {
                        Intent in = new Intent(context, AmountEnteringActivity.class);
                        in.putExtra("data", userDetails);
                        startActivity(in);
                    } else if (from.equals("cashOut")) {
                        if(userDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                            AgentData agentData=new AgentData();
                            if(userDetails.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                                agentData.setFirstname(userDetails.getFirstname());
                                agentData.setMiddlename(userDetails.getMiddlename());
                                agentData.setMobile(userDetails.getMobile());
                                agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT);
                                agentData.setLastname(userDetails.getLastname());
                                agentData.setAccount_no(userDetails.getAccount_no());
                                agentData.setId(userDetails.getId());
                                agentData.setProfile_image(userDetails.getProfileImage());
                            }else{
                                agentData.setBusiness_name(userDetails.getBusiness_name());
                                agentData.setMobile(userDetails.getMobile());
                                agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT);
                                agentData.setAccount_no(userDetails.getAccount_no());
                                agentData.setId(userDetails.getId());
                                agentData.setProfile_image(userDetails.getProfileImage());
                            }
                            Intent intent= new Intent(context, CashOutAmountEnteringActivity.class);
                            intent.putExtra("data", agentData);
                            startActivity(intent);
                        }else{
                            errorDialog.show("Invalid Agent");
                        }
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), InviteUserActivity.class);
                    intent.putExtra("mobile", mobileNumber);
                    intent.putExtra("country_code", countyCode);
                    startActivity(intent);
                }

            }
        });
        mImgBrowse.setOnClickListener(view -> {
            if(permissionUtils.checkContactPermission(requestPermissionLauncherContact)) {
                Intent intent = new Intent(getApplicationContext(), MyContactListActivity.class);
                intent.putExtra("from", from);
                startActivity(intent);
            }
        });
    }

    private void setError() {
        mImgContinue.setImageResource(R.drawable.ic_img_failed_btn);
        mImgContinue.setEnabled(false);
        validMobileNumber = false;
    }

    private void enableNextButton() {
        mImgContinue.setImageResource(R.drawable.ic_img_active_btn);
        mImgContinue.setEnabled(true);
    }

    private void getUserDetailsAPI(String mobileNumber) {
        apiCall.getUserDetailsUsingMobile(getUserDetailsUsingMobileRetry, mobileNumber, countyCode,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    enableNextButton();
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        List<CurrencyListData> currencyList=apiResponse.getCurrencyList();
                        String receiverAccountNumber=commonFunctions.getReceiverAccountNumberFormCurrencyList(currencyList);
                        if(receiverAccountNumber!=null&&!receiverAccountNumber.equals("")){
                            userDetails = apiResponse.getUserDetails();
                            userDetails.setAccount_no(receiverAccountNumber);
                            validMobileNumber = true;
                            if (from.equals("pay")) {
                                Intent in = new Intent(context, AmountEnteringActivity.class);
                                in.putExtra("data", userDetails);
                                startActivity(in);
                            } else if (from.equals("cashOut")) {
                                if(userDetails.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                                    AgentData agentData=new AgentData();
                                    if(userDetails.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                                        agentData.setFirstname(userDetails.getFirstname());
                                        agentData.setMiddlename(userDetails.getMiddlename());
                                        agentData.setMobile(userDetails.getMobile());
                                        agentData.setCountry_code(userDetails.getCountry_code());
                                        agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT);
                                        agentData.setLastname(userDetails.getLastname());
                                        agentData.setAccount_no(userDetails.getAccount_no());
                                        agentData.setId(userDetails.getId());
                                        agentData.setProfile_image(userDetails.getProfileImage());
                                    }else{
                                        agentData.setBusiness_name(userDetails.getBusiness_name());
                                        agentData.setMobile(userDetails.getMobile());
                                        agentData.setCountry_code(userDetails.getCountry_code());
                                        agentData.setAgent_type(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT);
                                        agentData.setAccount_no(userDetails.getAccount_no());
                                        agentData.setId(userDetails.getId());
                                        agentData.setProfile_image(userDetails.getProfileImage());
                                    }
                                    Intent intent= new Intent(context,CashOutAmountEnteringActivity.class);
                                    intent.putExtra("data", agentData);
                                    startActivity(intent);
                                }else{
                                    validMobileNumber = false;
                                    errorDialog.show("Invalid Agent");
                                }
                            }
                        }else{
                            validMobileNumber = false;
                            String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                            errorDialog.show(message);
                        }
                    } else {
                        validMobileNumber = false;
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                    validMobileNumber = false;
                }
            }

            @Override
            public void retry() {
                getUserDetailsUsingMobileRetry = true;
                getUserDetailsAPI(mobileNumber);
            }
        });
    }


    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.home);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        mEdtMobile = findViewById(R.id.maskedEditText);
        mEdtMobile.requestFocus();
        mImgBrowse = findViewById(R.id.imgBrowse);
        mImgContinue = findViewById(R.id.imgContinue);
        mImgContinue.setEnabled(false);
        tvError = findViewById(R.id.tv_error);
        tvError.setVisibility(View.INVISIBLE);

        if (from.equals("cashOut")) {
            mImgBrowse.setVisibility(View.INVISIBLE);
            mEdtMobile.setHint(R.string.enter_agents_mobile_number);
        } else {
            mImgBrowse.setVisibility(View.VISIBLE);
            mEdtMobile.setHint(getString(R.string.enter_mobile_number));
        }

        tvCountryCode=findViewById(R.id.tv_county_code);
        countyCode = SthiramValues.DEFAULT_COUNTY_MOBILE_CODE;
        mobileNumberLength= SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH;
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
          if(requestCode==SELECT_A_COUNTRY_CODE){
                countyCode = data.getStringExtra("country_code");
                mobileNumberLength = data.getStringExtra("mobile_number_length");
                setMobileNumberFormat(data.getStringExtra("format"));
                tvCountryCode.setText(countyCode);
                mEdtMobile.setText("");
            }
        }

    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

        }
        return false;
    }

    //contact permission
    private ActivityResultLauncher<String> requestPermissionLauncherContact =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(getApplicationContext(), MyContactListActivity.class);
                    intent.putExtra("from", from);
                    startActivity(intent);
                }
            });

    private void setMobileNumberFormat(String format) {
        if(watcher!=null){
            mEdtMobile.removeTextChangedListener(watcher);
        }
        watcher=new TextFormatter(mEdtMobile, format);
        mEdtMobile.addTextChangedListener(watcher);
    }
}