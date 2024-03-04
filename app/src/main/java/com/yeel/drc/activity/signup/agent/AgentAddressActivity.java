package com.yeel.drc.activity.signup.agent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.business.BusinessKYCUploadActivity;
import com.yeel.drc.activity.signup.personal.PersonalKYCUploadActivity;
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
public class AgentAddressActivity extends BaseActivity {
    SignUpData signUpData;
    Context context;
    RegisterFunctions registerFunctions;
    TextInputLayout tilAddressLineOne;
    TextInputEditText edtAddressLineOne;
    TextInputEditText edtProvinces;
    RelativeLayout rlProvinces;
    TextInputEditText edtDistrict;
    RelativeLayout rlDistrict;
    TextInputLayout tilLocality;
    TextInputEditText edtLocality;
    TextView tvContinue;
    boolean validAddressLineOne = false;
    boolean validProvinces = false;
    boolean validDistrict = false;
    boolean validLocality = false;
    String addressLineOne;
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
        setContentView(R.layout.activity_agent_address);
        setToolBar();
        context = AgentAddressActivity.this;
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
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setEmailAddress(registerFunctions.getEmailAddress());
            signUpData.setTaxId(registerFunctions.gettaxId());
            if (registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                signUpData.setFirstName(registerFunctions.getFirstName());
                signUpData.setMiddleName(registerFunctions.getMiddleName());
                signUpData.setLastName(registerFunctions.getLastName());
                signUpData.setDob(registerFunctions.getDOB());
            }else if (registerFunctions.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)) {
                signUpData.setBusinessName(registerFunctions.getBusinessName());
                signUpData.setAuthorizedPersonOne(registerFunctions.getAuthorizedPersonOne());
                signUpData.setCompanyRegistrationNumber(registerFunctions.getCompanyRegistrationNumber());
            }

        }
        initView();
        setItemListeners();
    }

    private void initView() {
        tilAddressLineOne = findViewById(R.id.til_address_line_one);
        edtAddressLineOne = findViewById(R.id.edt_address_line_one);
        edtProvinces = findViewById(R.id.edt_provinces);
        edtProvinces.setEnabled(false);
        rlProvinces = findViewById(R.id.rl_provinces);
        edtDistrict = findViewById(R.id.edt_district);
        edtDistrict.setEnabled(false);
        rlDistrict = findViewById(R.id.rl_district);
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
        rlProvinces.setOnClickListener(view -> {
            if (provincesResponse != null) {
                Intent in = new Intent(context, ProvincesSelectionActivity.class);
                in.putExtra("data", provincesResponse);
                startActivityForResult(in, SELECT_PROVINCES);
            } else {
                if (!errorDialog.isShowing()) {
                    errorDialog.show(getString(R.string.state_list_not_available));
                }
            }
        });
        rlDistrict.setOnClickListener(view -> {
            if (provincesPosition != 1000) {
                Intent in = new Intent(context, DistrictSelectionActivity.class);
                in.putExtra("data", provincesResponse.getProvinces().get(provincesPosition));
                startActivityForResult(in, SELECT_DISTRICT);
            } else {
                if (!errorDialog.isShowing()) {
                    errorDialog.show(getString(R.string.region_list_not_avilable));
                }
            }
        });
        tvContinue.setOnClickListener(view -> {
            callSignUpAPI(addressLineOne, provinces, district, locality);
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
                        tilAddressLineOne.setError("Enter valid address line 1");
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
                        tilLocality.setError("Enter valid locality");
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void callSignUpAPI(String addressLineOne, String provinces, String district, String locality) {
        if (signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
            signUpData.setBusinessName("");
            signUpData.setAuthorizedPersonOne("");
            signUpData.setAuthorizedPersonTwo("");
            signUpData.setCompanyRegistrationNumber("");
        } else {
            signUpData.setFirstName("");
            signUpData.setMiddleName("");
            signUpData.setLastName("");
            signUpData.setDob("");
            signUpData.setAuthorizedPersonTwo("");
        }

        apiCall.userSignUp(userSignUpRetry,
                signUpData.getCurrencyId(),
                signUpData.getFirstName(),
                signUpData.getMiddleName(),
                signUpData.getLastName(),
                signUpData.getMobileNumber(),
                signUpData.getEmailAddress(),
                signUpData.getDob(),
                SthiramValues.ACCOUNT_TYPE_AGENT+"",
                locality, district, provinces,
                signUpData.getBusinessName(),
                signUpData.getAuthorizedPersonOne(),
                signUpData.getAuthorizedPersonTwo(),
                signUpData.getTaxId(),
                addressLineOne,
                "",
                signUpData.getAccountType()+"",
                signUpData.getCompanyRegistrationNumber(),
                signUpData.getCountryCode(),
                true,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            SignUpResponseData apiResponse = gson.fromJson(jsonString, SignUpResponseData.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                signUpData.setUserId(apiResponse.getUser_id());
                                registerFunctions.saveRegisterDetails(signUpData);
                                if (signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                                    commonFunctions.setPage("agent_individual_address");
                                    Intent in = new Intent(context, PersonalKYCUploadActivity.class);
                                    in.putExtra("signUpData", signUpData);
                                    startActivity(in);
                                } else {
                                    commonFunctions.setPage("agent_business_address");
                                    Intent in = new Intent(context, BusinessKYCUploadActivity.class);
                                    in.putExtra("signUpData", signUpData);
                                    startActivity(in);
                                }
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
                        callSignUpAPI(addressLineOne, provinces, district, locality);
                    }
                });
    }

    private void checkAllValid() {
        if (validAddressLineOne && validProvinces && validDistrict && validLocality) {
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