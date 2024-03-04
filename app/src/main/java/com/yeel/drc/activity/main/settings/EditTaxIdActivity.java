package com.yeel.drc.activity.main.settings;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class EditTaxIdActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TextInputEditText edtOne, edtTwo,edtThree;

    TextView tvContinue;
    String taxId="";
    APICall apiCall;

    boolean validOne=false,validTwo=false,validThree=false;
    boolean updateProfileRetry=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tax_id);
        setToolBar();
        context= EditTaxIdActivity.this;
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            taxId=edtOne.getText().toString().trim()+ edtTwo.getText().toString().trim()+edtThree.getText().toString().trim();
            updateProfile();
        });
        edtOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String value = edtOne.getText().toString().trim();
                if (value.length() == 3) {
                    validOne = true;
                    edtTwo.requestFocus();
                    validateValues();
                } else {
                    validOne = false;
                    validateValues();
                }

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
                String value= edtTwo.getText().toString().trim();
                if(value.length()==3){
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
                String value=edtThree.getText().toString().trim();
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


    private void updateProfile() {
        apiCall.updateProfile(updateProfileRetry,commonFunctions.getUserId(),"",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                taxId,
                "",
                "",
                "",
                true, new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try{
                            String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse=gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                commonFunctions.setTaxId(taxId);
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

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.add_tax_id);
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