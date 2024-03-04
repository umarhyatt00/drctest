package com.yeel.drc.activity.main.homepayandsend;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.Objects;

public class SendViaYeelConfirmationActivity extends BaseActivity {
    UserDetailsData userDetailsData;
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    private Dialog success;

    TextView tvCashOutAmount;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvPay;
    TextView tvAgentName;
    ImageView ivAgent;
    TextView tvAgentMobile;
    ScrollView llScrollView;
    ProgressBar progressBar;
    TextView tvFirstLetter;

    double amount;
    double calculatedFees;
    double totalAmount;
    boolean getFeesRetry = false;
    boolean paymentRetry = false;

    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_via_yeel_confirmation);
        userDetailsData = (UserDetailsData) getIntent().getSerializableExtra("data");
        context= SendViaYeelConfirmationActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvPay.setOnClickListener(view -> {
            String message=commonFunctions.checkTransactionIsPossible(totalAmount+"",amount+"", SthiramValues.TRANSACTION_TYPE_CASH_OUT);
            if(message.equals(SthiramValues.SUCCESS)){
                Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                startActivityForResult(intent, 200);
            }
        });
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall = new APICall(context, SthiramValues.home);

        tvCashOutAmount =findViewById(R.id.tv_bill_amount);
        tvFees=findViewById(R.id.tv_fees);
        tvTotalToPay=findViewById(R.id.totalToPay);
        tvPay=findViewById(R.id.tv_button);
        tvAgentName =findViewById(R.id.tv_provider_name);
        ivAgent =findViewById(R.id.iv_agent);
        tvAgentMobile =findViewById(R.id.tv_provider_mobile);
        llScrollView=findViewById(R.id.ll_scroll_view);
        llScrollView.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        tvFirstLetter=findViewById(R.id.tv_first_Letter);

        callFeesAPI(userDetailsData.getAmount());
    }

    private void callFeesAPI(String amount) {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Yeel Account-Merchant Payment",
                amount,
                commonFunctions.getUserId(),
                commonFunctions.getCurrencyID(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        /*try {*/
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            FeeDetailsResponse apiResponse = gson.fromJson(jsonString, FeeDetailsResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                feesType = "PERC";
                                feesValue = apiResponse.getCommission().getPercentageRate();
                                feesTierName = apiResponse.getCommission().getTierName();
                                mCalculateCommission();
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                       /* } catch (Exception e) {
                            Log.e("Error",e+"");
                            somethingWentWrongDialog.show();
                        }*/
                    }

                    @Override
                    public void retry() {
                        getFeesRetry = true;
                        callFeesAPI(amount);
                    }
                }
        );
    }

    private void mCalculateCommission() {
    /*    try {*/
            amount = Double.parseDouble(userDetailsData.getAmount());
            if (amount >= 1) {
                //fees
                calculatedFees=commonFunctions.calculateCommission(feesType+"",amount+"",feesValue+"");
                totalAmount = amount + calculatedFees;
                setValues();
            } else {
                amount = 0.00;
                calculatedFees = 0.00;
                totalAmount = 0.00;
                setValues();
            }
       /* } catch (Exception e) {
            Log.e("Error",e+"");
            amount = 0.00;
            calculatedFees = 0.00;
            totalAmount = 0.00;
            setValues();
        }*/
    }


    private void setValues() {
        progressBar.setVisibility(View.GONE);
        llScrollView.setVisibility(View.VISIBLE);
        tvCashOutAmount.setText(commonFunctions.setAmount(amount+"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvFees.setText(commonFunctions.setAmount(calculatedFees +"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvTotalToPay.setText(commonFunctions.setAmount(totalAmount +"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvAgentName.setText(userDetailsData.getReceiverName());
        String firstLetter = String.valueOf(userDetailsData.getReceiverName().charAt(0));
        if (userDetailsData.getProfileImage() != null && !userDetailsData.getProfileImage().equals("")) {
            tvFirstLetter.setVisibility(View.GONE);
            Glide.with(this)
                    .load(userDetailsData.getProfileImage())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(ivAgent);
        } else {
            tvFirstLetter.setVisibility(View.VISIBLE);
            tvFirstLetter.setText(firstLetter);
        }

        //set mobile number
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
        String number=commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),mobileNumberFormat);
        tvAgentMobile.setText(number);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                doPayment();
            }
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.verify_details);
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

    private void doPayment() {
        String senderUserType = commonFunctions.getUserType();
        String senderAccountNumber = commonFunctions.getUserAccountNumber();
        String senderId = commonFunctions.getUserId();
        String senderName = commonFunctions.getFullName();
        String receiverAccountNumber = userDetailsData.getAccount_no();
        String receiverId = userDetailsData.getId();
        apiCall.transferToMerchant(
                paymentRetry,
                senderUserType + "",
                senderId + "",
                senderName + "",
                senderAccountNumber + "",
                receiverAccountNumber + "",
                userDetailsData.getReceiverName() + "",
                receiverId + "",
                userDetailsData.getRemark() + "",
                totalAmount+"",
                SthiramValues.SELECTED_CURRENCY_ID + "",
                SthiramValues.SELECTED_CURRENCY_SYMBOL + "",
                amount+"",
                calculatedFees+"",
                feesValue+"",
                feesTierName+"",
                true,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                paymentSuccessDialog(userDetailsData.getReceiverName() );
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
                        paymentRetry = true;
                        doPayment();
                    }
                });
    }

    private void paymentSuccessDialog(String receiverName) {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.payment_success_dialoge);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(commonFunctions.setAmount(userDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvUserName.setText(receiverName);
        tvTitle.setText(R.string.payment_sent_successfully);
        mButtonCancel.setOnClickListener(v -> {
            success.dismiss();
            commonFunctions.callHomeIntent();
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

}