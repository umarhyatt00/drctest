package com.yeel.drc.activity.main.settings.agent;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class EditAgentInfoActivity extends BaseActivity {
    Context context;
    TextInputEditText edtPersonOne;
    TextView tvContinue;
    TextInputLayout tilAuthorizedPersonOne;

    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    String authorizedPersonOne;
    boolean validAuthorizedPersonOne =true;
    boolean updateProfileRetry=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_agent_info);
        setToolBar();
        context= EditAgentInfoActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        edtPersonOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                authorizedPersonOne = edtPersonOne.getText().toString().trim();
                if (commonFunctions.validateAuthorizedPersonOne(authorizedPersonOne)) {
                    validAuthorizedPersonOne=true;
                    tilAuthorizedPersonOne.setError(null);
                }else{
                    validAuthorizedPersonOne=false;
                    if(tilAuthorizedPersonOne.getError()==null||tilAuthorizedPersonOne.getError().equals("")){
                        tilAuthorizedPersonOne.setError(getString(R.string.enter_valid_authorized_person_1));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvContinue.setOnClickListener(view -> {
            updateProfile();
        });
    }

    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        edtPersonOne=findViewById(R.id.edt_authorized_person_one);
        tvContinue=findViewById(R.id.tv_continue);
        tilAuthorizedPersonOne=findViewById(R.id.til_authorized_person_one);
        setValues();
        checkAllValid();
    }

    private void setValues() {
        authorizedPersonOne=commonFunctions.getAuthorizedPersonOne();
        edtPersonOne.setText(commonFunctions.getAuthorizedPersonOne());
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
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

    private void checkAllValid() {
        if(validAuthorizedPersonOne){
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        }else{
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
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
                authorizedPersonOne,
                "",
                "",
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
                                commonFunctions.setAuthorizedPersonOne(authorizedPersonOne);
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
}