package com.yeel.drc.activity.main.exchange;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.basicdetails.BasicDetailsResponse;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;
import java.util.Objects;

public class ExchangeConfirmationActivity extends BaseActivity {
    ExchangeData exchangeData;
    Context context;
    CommonFunctions pothuFunctions;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    private Dialog success;
    TextView tvSendAmount;
    TextView tvGetAmount;
    TextView tvConversionDate;
    TextView tvPayAmount;
    TextView tvHeading;
    TextView tvConfirm;
    boolean exchangeRetry = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_confirmation);
        exchangeData = (ExchangeData) getIntent().getSerializableExtra("data");
        context = ExchangeConfirmationActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
            pinVerificationListener.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> pinVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            doExchange();
        }
    });

    private void doExchange() {
        String encryptedBasicDetails = apiCall.getJsonFromEncryptedString(pothuFunctions.getBasicDetails());
        String receiverAccountNumber = "";
        if (encryptedBasicDetails != null && !encryptedBasicDetails.equals("")) {
            Gson gson = new Gson();
            BasicDetailsResponse apiResponse = gson.fromJson(encryptedBasicDetails, BasicDetailsResponse.class);
            List<CurrencyListData> currencyList = apiResponse.getCurrencyList();
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).getCurrency_code().equals("SSP")) {
                    receiverAccountNumber = currencyList.get(i).getAccount_number();
                }
            }
        }
        apiCall.doExchange(exchangeRetry, true,
                pothuFunctions.getUserType() + "",
                pothuFunctions.getUserAccountNumber() + "",
                receiverAccountNumber + "",
                exchangeData.getSendAmount() + "",
                "", pothuFunctions.getUserId() + "",
                pothuFunctions.getFullName() + "",
                pothuFunctions.getUserId() + "",
                pothuFunctions.getFullName() + "",
                "", exchangeData.getGetAmount() + "",
                exchangeData.getConversionAmount() + "",
                exchangeData.getSendAmount() + "",
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
                        exchangeRetry = true;
                        doExchange();
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
        mButtonCancel.setText(R.string.done);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(pothuFunctions.setAmount(exchangeData.getSendAmount()+ "") + " " + exchangeData.getSenderCurrency());
        tvUserName.setText(R.string.exchange_amount);
        tvTitle.setText(R.string.exchange_success_message);
        mButtonCancel.setOnClickListener(v -> {
            if (!isFinishing() || !isDestroyed()) {
                success.dismiss();
            }
            pothuFunctions.callHomeIntent();
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        if (!isFinishing() || !isDestroyed()) {
            success.show();
        }
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);
        pothuFunctions = new CommonFunctions(context);
        tvSendAmount = findViewById(R.id.tvSendAmount);
        tvGetAmount = findViewById(R.id.tvGetAmount);
        tvConversionDate = findViewById(R.id.tvConversionDate);
        tvPayAmount = findViewById(R.id.tvPayAmount);
        tvHeading = findViewById(R.id.tvHeading);
        tvConfirm = findViewById(R.id.tvConfirm);
        setValues();
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        tvHeading.setText(exchangeData.getSenderCurrency() + " to " + exchangeData.getReceiverCurrency());
        tvSendAmount.setText(pothuFunctions.setAmount(exchangeData.getSendAmount()) + " " + exchangeData.getSenderCurrency());
        tvGetAmount.setText(pothuFunctions.setAmount(exchangeData.getGetAmount()) + " " + exchangeData.getReceiverCurrency());
        tvConversionDate.setText("1 " + exchangeData.getSenderCurrency() + " = " + exchangeData.getConversionAmount() + " " + exchangeData.getReceiverCurrency());
        tvPayAmount.setText(pothuFunctions.setAmount(exchangeData.getSendAmount()) + " " + exchangeData.getSenderCurrency());

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.confirmation);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}