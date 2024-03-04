package com.yeel.drc.activity.main.settings.businessaccount;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.provincesdistrictselection.DistrictSelectionActivity;
import com.yeel.drc.activity.signup.provincesdistrictselection.ProvincesSelectionActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.provinces.ProvincesResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class EditBusinessAddressActivity extends BaseActivity {
    Context context;

    TextInputLayout tilAddressLineOne;
    TextInputEditText edtAddressLineOne;
    TextInputLayout tilAddressLineTwo;
    TextInputEditText edtAddressLineTwo;
    TextInputEditText edtProvinces;
    RelativeLayout rlProvinces;
    TextInputEditText edtDistrict;
    RelativeLayout rlDistrict;
    TextInputLayout tilLocality;
    TextInputEditText edtLocality;
    TextView tvContinue;


    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    boolean validAddressLineOne =true;
    boolean validAddressLineTwo =true;
    boolean validProvinces =true;
    boolean validDistrict =true;
    boolean validLocality =true;

    String addressLineOne;
    String addressLineTwo;
    String provinces;
    String district;
    String locality;

    boolean updateProfileRetry=false;
    boolean provincesNDistrictsRetry=false;


    ProvincesResponse provincesResponse;
    final static int SELECT_PROVINCES= 101;
    final static int SELECT_DISTRICT= 102;
    int provincesPosition=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_address);
        setToolBar();
        context= EditBusinessAddressActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlProvinces.setOnClickListener(view -> {
            if(provincesResponse!=null){
                Intent in=new Intent(context, ProvincesSelectionActivity.class);
                in.putExtra("data",provincesResponse);
                startActivityForResult(in, SELECT_PROVINCES);
            }else {
                if(!errorDialog.isShowing()){
                    errorDialog.show(getString(R.string.region_list_not_avilable));
                }
            }
        });
        rlDistrict.setOnClickListener(view -> {
            if(provincesPosition!=1000){
                Intent in=new Intent(context, DistrictSelectionActivity.class);
                in.putExtra("data",provincesResponse.getProvinces().get(provincesPosition));
                startActivityForResult(in, SELECT_DISTRICT);
            }else {
                if(!errorDialog.isShowing()){
                    errorDialog.show(getString(R.string.state_list_not_avilable));
                }
            }
        });
        tvContinue.setOnClickListener(view -> {
            updateProfile();
        });

        edtAddressLineOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addressLineOne=edtAddressLineOne.getText().toString().trim();
                if(commonFunctions.validateAddressLine1(addressLineOne)){
                    validAddressLineOne=true;
                    tilAddressLineOne.setError(null);
                }else{
                    validAddressLineOne=false;
                    if(tilAddressLineOne.getError()==null||tilAddressLineOne.getError().equals("")){
                        tilAddressLineOne.setError("Enter valid address line 1");
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
                addressLineTwo=edtAddressLineTwo.getText().toString().trim();
                if(commonFunctions.validateAddressLine2(addressLineTwo)){
                    validAddressLineTwo=true;
                    tilAddressLineTwo.setError(null);
                }else{
                    validAddressLineTwo=false;
                    if(tilAddressLineTwo.getError()==null||tilAddressLineTwo.getError().equals("")){
                        tilAddressLineTwo.setError("Enter valid address line 2");
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
                locality=edtLocality.getText().toString().trim();
                if(commonFunctions.validateLocality(locality)){
                    validLocality=true;
                    tilLocality.setError(null);
                }else{
                    validLocality=false;
                    if(tilLocality.getError()==null||tilLocality.getError().equals("")){
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

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        tilAddressLineOne = findViewById(R.id.til_address_line_one);
        edtAddressLineOne = findViewById(R.id.edt_address_line_one);
        tilAddressLineTwo = findViewById(R.id.til_address_line_two);
        edtAddressLineTwo = findViewById(R.id.edt_address_line_two);
        edtProvinces = findViewById(R.id.edt_provinces);
        edtProvinces.setEnabled(false);
        rlProvinces = findViewById(R.id.rl_provinces);
        edtDistrict = findViewById(R.id.edt_district);
        edtDistrict.setEnabled(false);
        rlDistrict = findViewById(R.id.rl_district);
        tilLocality = findViewById(R.id.til_locality);
        edtLocality = findViewById(R.id.edt_locality);
        tvContinue=findViewById(R.id.tv_continue);
        rlProvinces = findViewById(R.id.rl_provinces);
        rlDistrict = findViewById(R.id.rl_district);
        setValues();
        getProvincesNDistricts();
    }

    private void setValues() {
        if(commonFunctions.getDistrict()!=null&&!commonFunctions.getDistrict().equals("")) {
            validAddressLineOne = true;
            validAddressLineTwo = true;
            validProvinces = true;
            validDistrict = true;
            validLocality = true;
            edtAddressLineOne.setText(commonFunctions.getAddressLineOne());
            edtAddressLineTwo.setText(commonFunctions.getAddressLineTwo());
            edtProvinces.setText(commonFunctions.getProvince());
            edtDistrict.setText(commonFunctions.getDistrict());
            edtLocality.setText(commonFunctions.getLocality());
            addressLineOne = commonFunctions.getAddressLineOne();
            addressLineTwo = commonFunctions.getAddressLineTwo();
            provinces = commonFunctions.getProvince();
            district = commonFunctions.getDistrict();
            locality = commonFunctions.getLocality();
        }else {
            validAddressLineOne = false;
            validAddressLineTwo = true;
            validProvinces = false;
            validDistrict = false;
            validLocality = false;
            addressLineOne = "";
            addressLineTwo = "";
            provinces = "";
            district = "";
            locality = "";
        }
        checkAllValid();
    }


    private void getProvincesNDistricts() {
        apiCall.getProvincesNDistricts(provincesNDistrictsRetry,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ProvincesResponse apiResponse=gson.fromJson(jsonString, ProvincesResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        provincesResponse=apiResponse;
                    }else{
                        if(!errorDialog.isShowing()){
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                provincesNDistrictsRetry=true;
                getProvincesNDistricts();

            }
        });
    }

    private void checkAllValid() {
        if(validAddressLineOne&&validAddressLineTwo&&validProvinces&&validDistrict&&validLocality){
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.edit_business_address);
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

    private void updateProfile() {
        apiCall.updateProfile(updateProfileRetry,commonFunctions.getUserId(),"",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                locality,
                district,
                provinces,
                "",
                "",
                "",
                "",
                addressLineOne,
                addressLineTwo,
                "",
                true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try{
                            String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse=gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                commonFunctions.setLocality(locality);
                                commonFunctions.setDistrict(district);
                                commonFunctions.setProvince(provinces);
                                commonFunctions.setAddressLineOne(addressLineOne);
                                commonFunctions.setAddressLineTwo(addressLineTwo);
                                Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent in = getIntent();
                                setResult(Activity.RESULT_OK, in);
                                finish();
                            }else{
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
                        updateProfileRetry=true;
                        updateProfile();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PROVINCES) {
                provinces= data.getStringExtra("provinces");
                provincesPosition=data.getIntExtra("position",0);

                district="";
                edtDistrict.setText(district);
                validDistrict=false;

                edtProvinces.setText(provinces);
                validProvinces=true;
                checkAllValid();
            }else if(requestCode==SELECT_DISTRICT){
                district=data.getStringExtra("district");
                edtDistrict.setText(district);
                validDistrict=true;
                checkAllValid();
            }
        }

    }
}