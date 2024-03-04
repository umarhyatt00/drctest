package com.yeel.drc.activity.main.cashout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class CashOutActivity extends BaseActivity {

    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    private CashOutActivity context;
    private EditText edtCashOutAmount;
    private TextInputLayout tilAdditionalNote;
    private TextInputEditText edtAdditionalNote;
    private TextView tvBranchName;
    private TextView tvBranchCode;
    private TextView tvCashOutAmount;
    private TextView tvCashOutFees;
    private TextView tvTotalAmount;
    private TextView tvCashOutButton;
    private Dialog success;
    TextView tvCurrencySymbol;


    RelativeLayout rlMain;
    AgentData agentData;

    private double amount;
    private double calculatedFees;
    private double totalAmount;
    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";
    String mRemarks="";
    boolean getFeesRetry = false;
    boolean cashOutRetry = false;
    boolean readyToGo = false;
    String agentName="";
    CommonFunctions commonFunctions;

    boolean validAmount=false;
    boolean validRemark=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_out_main);
        context = CashOutActivity.this;
        agentData = (AgentData) getIntent().getSerializableExtra("data");
        initView();
        setToolBar();
        setItemListeners();
    }

    private void setItemListeners() {
        edtAdditionalNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRemarks = edtAdditionalNote.getText().toString().trim();
                if(commonFunctions.validateRemark(mRemarks)){
                    validRemark=true;
                    tilAdditionalNote.setError(null);
                    enableButton();
                }else{
                    validRemark=false;
                    if(tilAdditionalNote.getError()==null||tilAdditionalNote.getError().equals("")){
                        tilAdditionalNote.setError(getString(R.string.enter_valid_remark));
                    }
                    enableButton();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtCashOutAmount.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(500, 300) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        if(commonFunctions.validAmount(edtCashOutAmount.getText().toString().trim())){
                            amount = Double.parseDouble(edtCashOutAmount.getText().toString());
                            timer.cancel();
                            callFeesAPI(amount + "");
                        }else{
                            amount = 0.00;
                            calculatedFees = 0.00;
                            totalAmount = 0.00;
                            setAmountDetails(false);
                        }
                        validAmount=false;
                        enableButton();
                    }
                }.start();


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvCashOutButton.setOnClickListener(view -> {
            String message=commonFunctions.checkTransactionIsPossible(String.valueOf(totalAmount),String.valueOf(amount), SthiramValues.TRANSACTION_TYPE_CASH_OUT);
            if(message.equals(SthiramValues.SUCCESS)){
                commonFunctions.logAValue("Amount", amount + "");
                commonFunctions.logAValue("Fees", calculatedFees + "");
                commonFunctions.logAValue("Total Amount", totalAmount + "");
                Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                startActivityForResult(intent, 200);
            }
        });
    }

    private void mCalculateCommission() {
        //commonFunctions.hideKeyboard(context);
        if (!edtCashOutAmount.getText().toString().trim().equals("")) {
            try {
                amount = Double.parseDouble(edtCashOutAmount.getText().toString());
                if (amount >= 1) {
                    //fees
                    calculatedFees=commonFunctions.calculateCommission(feesType+"",amount+"",feesValue+"");
                    totalAmount = amount + calculatedFees;
                    setAmountDetails(true);
                } else {
                    amount = 0.00;
                    calculatedFees = 0.00;
                    totalAmount = 0.00;
                    setAmountDetails(false);
                }
            } catch (Exception e) {
                amount = 0.00;
                calculatedFees = 0.00;
                totalAmount = 0.00;
                setAmountDetails(false);
            }

        } else {
            amount = 0.00;
            calculatedFees = 0.00;
            totalAmount = 0.00;
            setAmountDetails(false);

        }

    }


    private void enableButton(){
        if(validAmount&&validRemark){
            tvCashOutButton.setBackgroundResource(R.drawable.bg_button_one);
            tvCashOutButton.setEnabled(true);
        }
        else {
            tvCashOutButton.setBackgroundResource(R.drawable.bg_button_three);
            tvCashOutButton.setEnabled(false);
        }
    }

    private void setAmountDetails(boolean value) {
        readyToGo = value;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            tvCashOutAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(amount + "").toString());
            tvCashOutFees.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(calculatedFees + "").toString());
            tvTotalAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(totalAmount + "").toString());

        } else {
            tvCashOutAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(amount + "").toString());
            tvCashOutFees.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(calculatedFees + "").toString());
            tvTotalAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount(totalAmount + "").toString());
        }
    }

    private void initView() {
        commonFunctions=new CommonFunctions(context);
        tvCurrencySymbol=findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);

        edtCashOutAmount = (EditText) findViewById(R.id.edtCashOutAmount);
        tilAdditionalNote = (TextInputLayout) findViewById(R.id.til_additionalNote);
        edtAdditionalNote = (TextInputEditText) findViewById(R.id.edtAdditionalNote);
        tvBranchName = (TextView) findViewById(R.id.CashOutBranchName);
        tvBranchCode = (TextView) findViewById(R.id.CashoutBranchCode);
        tvCashOutAmount = (TextView) findViewById(R.id.cash_out_amount);
        tvCashOutFees = (TextView) findViewById(R.id.tv_cashOut_fees);
        tvCashOutFees.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + commonFunctions.setAmount("0").toString());
        tvTotalAmount = (TextView) findViewById(R.id.cashOut_totalAmount);
        tvCashOutButton = (TextView) findViewById(R.id.mBtnCashout);
        rlMain = findViewById(R.id.rl_main);
        setValues();
        enableButton();
    }

    private void callFeesAPI(String amount) {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Yeel agent cashout",
                amount,
                commonFunctions.getUserId(),
                commonFunctions.getCurrencyID(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            FeeDetailsResponse apiResponse = gson.fromJson(jsonString, FeeDetailsResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                feesType = "PERC";
                                feesValue = apiResponse.getCommission().getPercentageRate();
                                feesTierName = apiResponse.getCommission().getTierName();
                                mCalculateCommission();
                                validAmount=true;
                                enableButton();
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getFeesRetry = true;
                        callFeesAPI(amount);
                    }
                }
        );
    }

    private void setValues() {
        amount = 0.00;
        calculatedFees = 0.00;
        totalAmount = 0.00;
        setAmountDetails(false);
        if (agentData != null) {
            if(agentData.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                agentName=commonFunctions.generateFullName(
                        agentData.getFirstname(),
                        agentData.getMiddlename(),
                        agentData.getLastname()
                );
            }else{
                agentName=agentData.getBusiness_name();
            }
            tvBranchName.setText(agentName);

            String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            tvBranchCode.setText(commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),mobileNumberFormat));
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cash_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_info_icon, menu);
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
        dialog.setContentView(R.layout.custom_alert_cashout_info);
        TextView description = dialog.findViewById(R.id.description);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.what_is_cash_out);
        description.setText(R.string.you_can_get_cash_by_visiting_any_of_the_yeel_delegates_agents_worldwide_just_scan_the_qr_code_or_enter_the_delegate_agent_code_and_cash_out);
        TextView dialogButton = dialog.findViewById(R.id.tv_ok);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                //paymentSuccessDialog();
                doCashOut();
            }
        }
    }

    private void doCashOut() {
        apiCall.doCashOut(cashOutRetry,
                true,
                commonFunctions.getUserType() + "",
                commonFunctions.getUserAccountNumber() + "",
                agentData.getAccount_no() + "",
                totalAmount + "",
                mRemarks + "",
                commonFunctions.getUserId() + "",
                commonFunctions.getFullName() + "",
                agentData.getId() + "",
                agentName + "",
                calculatedFees + "",
                amount + "",
                feesValue + "",
                feesTierName + "",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                        Gson gson = new Gson();
                        StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                        if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                            paymentSuccessDialog();
                        } else {
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void retry() {
                        cashOutRetry = true;
                        doCashOut();
                    }
                }

        );
    }

    private void paymentSuccessDialog() {
        success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.payment_success_dialoge);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        mButtonCancel.setText(R.string.ok);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL+" "+commonFunctions.setAmount(amount+""));

        tvUserName.setText(R.string.cash_out_amount);
        tvTitle.setText(R.string.cashout_success_messgae);
        mButtonCancel.setOnClickListener(v -> {
            if (!isFinishing() || !isDestroyed()) {
                success.dismiss();
            }
            commonFunctions.callHomeIntent();
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        if (!isFinishing() || !isDestroyed()) {
            success.show();
        }
    }
}