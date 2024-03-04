package com.yeel.drc.activity.signup.agent;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yeel.drc.R;
import com.yeel.drc.activity.signup.EmailAddressActivity;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;
import com.yeel.drc.utils.TextFormatter;

public class IndividualAgentInformation extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    TextInputLayout tilFirstName;
    TextInputEditText edtFirstName;
    TextInputLayout tilMiddleName;
    TextInputEditText edtMiddleName;
    TextInputLayout tilLastName;
    TextInputEditText edtLastName;
    TextInputLayout tilDOB;
    TextInputEditText edtDOB;
    TextView tvContinue;
    String firstName;
    String middleName="";
    String lastName;
    String dateOfBirth;
    boolean validFirstName =false;
    boolean validMiddleName =true;
    boolean validLastName =false;
    boolean validDOB =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_agent_information);
        context= IndividualAgentInformation.this;
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
        }
        setToolBar();
        initView();
        setItemListeners();
    }
    private void initView() {
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        tilFirstName=findViewById(R.id.til_first_name);
        edtFirstName=findViewById(R.id.edt_first_name);
        tilMiddleName=findViewById(R.id.til_middle_name);
        edtMiddleName=findViewById(R.id.edt_middle_name);
        tilLastName=findViewById(R.id.til_last_name);
        edtLastName=findViewById(R.id.edt_last_name);
        tilDOB=findViewById(R.id.til_dob);
        edtDOB=findViewById(R.id.edt_dob);
        edtDOB.addTextChangedListener(new TextFormatter(edtDOB,"XX/XX/XXXX"));
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            commonFunctions.setPage("individual_agent_info");
            signUpData.setFirstName(firstName);
            signUpData.setMiddleName(middleName);
            signUpData.setLastName(lastName);
            String formattedDOB=commonFunctions.dobFormat(dateOfBirth);
            signUpData.setDob(formattedDOB);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in=new Intent(context, EmailAddressActivity.class);
            in.putExtra("signUpData",signUpData);
            startActivity(in);
        });

        //validation first name
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firstName = edtFirstName.getText().toString().trim();
                if (commonFunctions.validateFistName(firstName)) {
                    validFirstName=true;
                    tilFirstName.setError(null);
                }else{
                    validFirstName=false;
                    if(tilFirstName.getError()==null||tilFirstName.getError().equals("")){
                        tilFirstName.setError(getString(R.string.enter_valid_first_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate middle name
        edtMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                middleName=edtMiddleName.getText().toString().trim();
                if(commonFunctions.validateMiddleName(middleName)){
                    validMiddleName=true;
                    tilMiddleName.setError(null);
                }else{
                    validMiddleName=false;
                    if(tilMiddleName.getError()==null||tilMiddleName.getError().equals("")){
                        tilMiddleName.setError(getString(R.string.enter_valid_middle_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate last name
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastName=edtLastName.getText().toString().trim();
                if(commonFunctions.validateLastName(lastName)){
                    validLastName=true;
                    tilLastName.setError(null);
                }else{
                    validLastName=false;
                    if(tilLastName.getError()==null||tilLastName.getError().equals("")){
                        tilLastName.setError(getString(R.string.enter_valid_last_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtDOB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dateOfBirth=edtDOB.getText().toString().trim();
                if (commonFunctions.validateDOB(dateOfBirth)) {
                    validDOB = true;
                    tilDOB.setError(null);
                } else {
                    if(tilDOB.getError()==null||tilDOB.getError().equals("")){
                        tilDOB.setError(getString(R.string.enter_valid_dobe));
                    }
                    validDOB = false;
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkAllValid() {
        if(validFirstName&&validMiddleName&&validLastName&&validDOB){
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if(isTaskRoot()){
                clearSignUpProgressDialog.show();
            }else{
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            clearSignUpProgressDialog.show();
        }else{
            finish();
        }
    }
}