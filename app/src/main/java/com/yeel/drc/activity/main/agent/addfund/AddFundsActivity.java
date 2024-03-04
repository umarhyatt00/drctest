package com.yeel.drc.activity.main.agent.addfund;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;
import static com.yeel.drc.utils.SthiramValues.SELECTED_CURRENCY_ID;
import static com.yeel.drc.utils.SthiramValues.SELECTED_CURRENCY_SYMBOL;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.addfundsone.AddFundOneResponse;
import com.yeel.drc.api.banklist.BankListResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class AddFundsActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    EditText edtAddFundAmount;
    EditText edtBankName;
    RelativeLayout rlBank;
    EditText edtRefNumber;
    TextView tvCurrencySymbol;
    TextInputLayout tilRefNumber;
    TextView tvContinue;
    LinearLayout llMain;

    String amount;
    String amountToAdd;
    String refNumber ="";
    String bankName="";
    String bankCode="";

    final static int SELECT_BANK= 101;
    final static int PIN_VERIFICATION= 102;
    boolean bankListRetry =false;
    boolean addFundsRetry =false;
    BankListResponse bankListResponse;

    boolean validAmount =false;
    boolean validBank =false;
    boolean validReffNumber =true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);
        context=AddFundsActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        edtAddFundAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amount = edtAddFundAmount.getText().toString().trim();
                if (!amount.equals("")) {
                    validAmount = true;
                } else {
                    validAmount = false;
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtRefNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                refNumber = edtRefNumber.getText().toString().trim();
                if (commonFunctions.validateReffNumber(refNumber)) {
                    tilRefNumber.setError(null);
                    validReffNumber = true;
                } else {
                    if (tilRefNumber.getError() == null || tilRefNumber.getError().equals("")) {
                        tilRefNumber.setError(getString(R.string.reff_number_validation_message));
                    }
                    validReffNumber = false;
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        rlBank.setOnClickListener(view -> {
            Intent in = new Intent(context, BankSelectionActivity.class);
            in.putExtra("data", bankListResponse);
            startActivityForResult(in, SELECT_BANK);
        });
        tvContinue.setOnClickListener(view -> {
            String message=commonFunctions.checkAddFundPossible(amount,amount, SthiramValues.TRANSACTION_TYPE_ADD_FUND);
            if(message.equals(SthiramValues.SUCCESS)){
                double doubleDebit = Double.parseDouble(amount);
                amountToAdd = doubleDebit+"";
                addFund();
            }
        });
    }

    private void checkAllValid() {
        if(validAmount&&validBank&&validReffNumber){
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        }else{
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void initView() {

        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall=new APICall(context, SthiramValues.home);
        tvCurrencySymbol=findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SELECTED_CURRENCY_SYMBOL);

        edtAddFundAmount=findViewById(R.id.edt_add_funds_amount);
        edtBankName=findViewById(R.id.edt_bank);
        rlBank=findViewById(R.id.rl_bank);
        edtRefNumber =findViewById(R.id.edt_reff_number);
        tilRefNumber =findViewById(R.id.til_reff_number);
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(false);
        llMain=findViewById(R.id.ll_main);
        checkAllValid();
        getBankList();
    }

    private void getBankList() {
        apiCall.getBankList(bankListRetry,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BankListResponse apiResponse=gson.fromJson(jsonString, BankListResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        bankListResponse =apiResponse;
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
                bankListRetry =true;
                getBankList();
            }
        });
    }

    private void addFund() {
        apiCall.addFundAPIOne(addFundsRetry,true,
                commonFunctions.getUserId(),
                bankName,
                bankCode,
                amountToAdd,
                refNumber,
                ACCOUNT_TYPE_AGENT,
                commonFunctions.getUserAccountNumber(),
                commonFunctions.getFullName(),
                SELECTED_CURRENCY_SYMBOL,
                SELECTED_CURRENCY_ID,
                new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AddFundOneResponse apiResponse=gson.fromJson(jsonString, AddFundOneResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                       /* Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_LONG).show();*/
                        Intent in = new Intent(context, UploadBankRecieptActivity.class);
                        in.putExtra("requestId",apiResponse.getReq_id());
                        startActivity(in);
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
                addFundsRetry =true;
                getBankList();
            }
        });
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        RelativeLayout rlLabel=findViewById(R.id.rl_label_menu);
        rlLabel.setVisibility(View.VISIBLE);
        rlLabel.setOnClickListener(view -> {
           Intent in=new Intent(context, AddFundsHistoryActivity.class);
           startActivity(in);
        });
        tvTitle.setText("");
    }


    @Override
    public void onBackPressed() {
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_BANK) {
                if(data!=null){
                    bankName = data.getStringExtra("bank_name");
                    bankCode=data.getStringExtra("bank_code");
                    edtBankName.setText(bankName);
                    validBank=true;
                }else{
                    validBank=false;
                }
                checkAllValid();

            }else if(requestCode == PIN_VERIFICATION){

            }
        }
    }

}