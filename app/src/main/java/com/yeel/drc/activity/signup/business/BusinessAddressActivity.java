package com.yeel.drc.activity.signup.business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.provincesdistrictselection.DistrictSelectionActivity;
import com.yeel.drc.activity.signup.provincesdistrictselection.ProvincesSelectionActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.provinces.ProvincesResponse;
import com.yeel.drc.api.signup.SignUpResponseData;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

public class BusinessAddressActivity extends BaseActivity {
    SignUpData signUpData;
    Context context;
    RegisterFunctions registerFunctions;
    TextInputLayout tilAddressLineOne;
    TextInputEditText edtAddressLineOne;
    TextInputLayout tilAddressLineTwo;
    TextInputEditText edtAddressLineTwo;
    TextInputEditText edtProvinces;
    RelativeLayout rlRegion;
    TextInputEditText edtDistrict;
    RelativeLayout rlState;
    TextInputLayout tilLocality;
    TextInputEditText edtLocality;
    TextView tvContinue;
    boolean validAddressLineOne = false;
    boolean validAddressLineTwo = true;
    boolean validProvinces = false;
    boolean validDistrict = false;
    boolean validLocality = false;
    String addressLineOne;
    String addressLineTwo;
    String provinces;
    String district;
    String locality;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    boolean userSignUpRetry = false;
    boolean provincesNDistrictsRetry = false;
    ProvincesResponse provincesResponse;
    final static int SELECT_PROVINCES = 101;
    final static int SELECT_DISTRICT = 102;
    int provincesPosition = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_address);
        setToolBar();
        context = BusinessAddressActivity.this;
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        signUpData = (SignUpData) getIntent().getSerializableExtra("signUpData");
        registerFunctions = new RegisterFunctions(context);
        clearSignUpProgressDialog = new ClearSignUpProgressDialog(context, commonFunctions);
        if (signUpData == null) {
            signUpData = new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setBusinessName(registerFunctions.getBusinessName());
            signUpData.setAuthorizedPersonOne(registerFunctions.getAuthorizedPersonOne());
            signUpData.setAuthorizedPersonTwo(registerFunctions.getAuthorizedPersonTwo());
            signUpData.setCompanyRegistrationNumber(registerFunctions.getCompanyRegistrationNumber());
            signUpData.setTaxId(registerFunctions.gettaxId());
            signUpData.setEmailAddress(registerFunctions.getEmailAddress());

        }
        initView();
        setItemListeners();

    }

    private void initView() {
        tilAddressLineOne = findViewById(R.id.til_address_line_one);
        edtAddressLineOne = findViewById(R.id.edt_address_line_one);
        tilAddressLineTwo = findViewById(R.id.til_address_line_two);
        edtAddressLineTwo = findViewById(R.id.edt_address_line_two);
        edtProvinces = findViewById(R.id.edt_provinces);
        edtProvinces.setEnabled(false);
        rlRegion = findViewById(R.id.rl_provinces);
        edtDistrict = findViewById(R.id.edt_district);
        edtDistrict.setEnabled(false);
        rlState = findViewById(R.id.rl_district);
        tilLocality = findViewById(R.id.til_locality);
        edtLocality = findViewById(R.id.edt_locality);
        tvContinue = findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        getProvincesNDistricts();
    }

    private void getProvincesNDistricts() {
        apiCall.getProvincesNDistricts(provincesNDistrictsRetry, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ProvincesResponse apiResponse = gson.fromJson(jsonString, ProvincesResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        provincesResponse = apiResponse;
                    } else {
                        if (!errorDialog.isShowing()) {
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                provincesNDistrictsRetry = true;
                getProvincesNDistricts();
            }
        });
    }


    private void setItemListeners() {
        rlRegion.setOnClickListener(view -> {
            if (provincesResponse != null) {
                Intent in = new Intent(context, ProvincesSelectionActivity.class);
                in.putExtra("data", provincesResponse);
                startActivityForResult(in, SELECT_PROVINCES);
            } else {
                if (!errorDialog.isShowing()) {
                    errorDialog.show(getString(R.string.region_list_not_avilable));
                }
            }
        });
        rlState.setOnClickListener(view -> {
            if (provincesPosition != 1000) {
                Intent in = new Intent(context, DistrictSelectionActivity.class);
                in.putExtra("data", provincesResponse.getProvinces().get(provincesPosition));
                startActivityForResult(in, SELECT_DISTRICT);
            } else {
                if (!errorDialog.isShowing()) {
                    errorDialog.show(getString(R.string.state_list_not_available));
                }
            }
        });
        tvContinue.setOnClickListener(view -> {
            if(!addressLineOne.equals(addressLineTwo)){
                callSignUpAPI(addressLineOne, addressLineTwo, provinces, district, locality);
            }else{
                errorDialog.show(getString(R.string.addess_validation));
            }

        });

        edtAddressLineOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addressLineOne = edtAddressLineOne.getText().toString().trim();
                if (commonFunctions.validateAddressLine1(addressLineOne)) {
                    validAddressLineOne = true;
                    tilAddressLineOne.setError(null);
                } else {
                    validAddressLineOne = false;
                    if (tilAddressLineOne.getError() == null || tilAddressLineOne.getError().equals("")) {
                        tilAddressLineOne.setError(getString(R.string.enter_valid_address_line_1));
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtAddressLineTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addressLineTwo = edtAddressLineTwo.getText().toString().trim();
                if (commonFunctions.validateAddressLine2(addressLineTwo)) {
                    validAddressLineTwo = true;
                    tilAddressLineTwo.setError(null);
                } else {
                    validAddressLineTwo = false;
                    if (tilAddressLineTwo.getError() == null || tilAddressLineTwo.getError().equals("")) {
                        tilAddressLineTwo.setError(getString(R.string.enter_valid_address_line_2));
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtLocality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                locality = edtLocality.getText().toString().trim();
                if (commonFunctions.validateLocality(locality)) {
                    validLocality = true;
                    tilLocality.setError(null);
                } else {
                    validLocality = false;
                    if (tilLocality.getError() == null || tilLocality.getError().equals("")) {
                        tilLocality.setError(getString(R.string.enter_valid_county));
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void callSignUpAPI(String addressLineOne, String addressLineTwo, String provinces, String district, String locality) {
        apiCall.userSignUp(userSignUpRetry,
                signUpData.getCurrencyId(),
                "", "",
                "", signUpData.getMobileNumber(),
                signUpData.getEmailAddress(), "", signUpData.getAccountType(),
                locality+"", district+"", provinces+"", signUpData.getBusinessName(), signUpData.getAuthorizedPersonOne(),
                signUpData.getAuthorizedPersonTwo(), signUpData.getTaxId() + "", addressLineOne, addressLineTwo, "", signUpData.getCompanyRegistrationNumber(),signUpData.getCountryCode(), true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            SignUpResponseData apiResponse = gson.fromJson(jsonString, SignUpResponseData.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                commonFunctions.setPage("business_address");
                                signUpData.setUserId(apiResponse.getUser_id());
                                registerFunctions.saveRegisterDetails(signUpData);
                                Intent in = new Intent(context, BusinessKYCUploadActivity.class);
                                in.putExtra("signUpData", signUpData);
                                startActivity(in);
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    }

                    @Override
                    public void retry() {
                        userSignUpRetry = true;
                        callSignUpAPI(addressLineOne, addressLineTwo, provinces, district, locality);
                    }
                });
    }

    private void checkAllValid() {
        if (validAddressLineOne && validAddressLineTwo && validProvinces && validDistrict && validLocality) {
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
        RelativeLayout rlSkip = findViewById(R.id.rl_skip);
        rlSkip.setVisibility(View.VISIBLE);
        rlSkip.setOnClickListener(view -> {
            callSignUpAPI("", "", "", "", "");
        });
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
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
        if (isTaskRoot()) {
            clearSignUpProgressDialog.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PROVINCES) {
                provinces = data.getStringExtra("provinces");
                provincesPosition = data.getIntExtra("position", 0);
                district = "";
                edtDistrict.setText(district);
                validDistrict = false;
                edtProvinces.setText(provinces);
                validProvinces = true;
                checkAllValid();
            } else if (requestCode == SELECT_DISTRICT) {
                district = data.getStringExtra("district");
                edtDistrict.setText(district);
                validDistrict = true;
                checkAllValid();
            }
        }

    }
}