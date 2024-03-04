package com.yeel.drc.activity.main.mobilepay;

import static com.yeel.drc.utils.SthiramValues.KENYA_COUNTY_MOBILE_CODE;
import static com.yeel.drc.utils.SthiramValues.KENYA_MOBILE_NUMBER_FORMAT;
import static com.yeel.drc.utils.SthiramValues.UGANDA_COUNTY_MOBILE_CODE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.mpesadetailsfrommobile.VerifyMobileData;
import com.yeel.drc.api.mpesafees.MpesaFeeResponse;
import com.yeel.drc.api.mpesapayment.MepsaPaymentResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.mpesa.MobilePayData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class MobilePayConfirmationActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    LinearLayout llNext;
    MobilePayData mobilePayData;

    TextView tvContinue;
    TextView tvFirstLetter;
    TextView tvMobileNumber;
    TextView tvReceiverName;
    TextView tvSendAmount;
    TextView tvPurpose;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvReceiverGetsAmount;
    TextView senderName;
    LinearLayout senderNameLayout;
    ScrollView scrollView;
    ProgressBar progressBar;
    TextView tvMobilePayName;


    boolean getFeesRetry = false;
    boolean getPaymentRetry = false;
    boolean validAmount = false;
    String feesType = "PERC";
    String feesValue = "";

    private double amount = 0.00;
    private double calculatedFees = 0.00;
    private double totalAmount = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_confirmation);
        mobilePayData = (MobilePayData) getIntent().getSerializableExtra("data");
        context = MobilePayConfirmationActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llNext.setOnClickListener(view -> {
            String type="";
            if(mobilePayData.getMobilePayCode().equals(SthiramValues.MPESA_KENYA_CODE)){
                type= SthiramValues.TRANSACTION_TYPE_MPESA;
            }else if(mobilePayData.getMobilePayCode().equals(SthiramValues.AIRTAL_UGANDA_CODE)){
                type= SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA;
            }
            String message=commonFunctions.checkTransactionIsPossible(totalAmount+"", mobilePayData.getSendAmount()+"",type);
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


        llNext = findViewById(R.id.ll_next);
        tvContinue = findViewById(R.id.tv_continue);
        tvFirstLetter = findViewById(R.id.tv_first_Letter);
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvSendAmount = findViewById(R.id.tv_send_amount);
        tvPurpose = findViewById(R.id.tv_purpose);
        tvFees = findViewById(R.id.tv_fees);
        tvTotalToPay = findViewById(R.id.tv_total_to_pay);
        tvReceiverGetsAmount = findViewById(R.id.tv_receiver_gets_amount);
        scrollView = findViewById(R.id.ll_scroll_view);
        scrollView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        tvMobilePayName=findViewById(R.id.tv_delivery_method);
        senderName=findViewById(R.id.tv_sender_name);
        senderNameLayout=findViewById(R.id.layoutSenderName);

        //callFeesAPI();
        feesType = mobilePayData.getFeesType();
        feesValue = mobilePayData.getFeesValue();
        mCalculateCommission();
    }

    private void callFeesAPI() {
        validAmount = false;
        setReadyToNext();
        apiCall.getMPesaFeesDetails(
                mobilePayData.getMobilePayCode(),
                getFeesRetry,
                false,
                mobilePayData.getSendAmount() + "",
                commonFunctions.getUserId()+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MpesaFeeResponse apiResponse = gson.fromJson(jsonString, MpesaFeeResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                feesType = apiResponse.getData().getType();
                                feesValue = apiResponse.getData().getFees();
                                mCalculateCommission();
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
                        callFeesAPI();
                    }
                }

        );
    }

    private void mCalculateCommission() {
        if (mobilePayData.getSendAmount() != null) {
            try {
                calculatedFees=commonFunctions.calculateCommission(feesType+"", mobilePayData.getSendAmount()+"",feesValue+"");
                amount=Double.parseDouble(mobilePayData.getSendAmount());
                totalAmount = amount + calculatedFees;
                setDetails();
            } catch (Exception e) {
                somethingWentWrongDialog.show();
            }
        } else {
            somethingWentWrongDialog.show();
        }
    }


    private void setDetails() {
        try {
            validAmount = true;
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            tvMobilePayName.setText(mobilePayData.getMobilePayName());
            VerifyMobileData receiverDetails = mobilePayData.getReceiverDetails();
            if(receiverDetails!=null){
                String formattedMobileNumber = "";
                if(mobilePayData.getMobilePayCode().equals(SthiramValues.MPESA_KENYA_CODE)){
                    formattedMobileNumber=commonFunctions.formatAMobileNumber(mobilePayData.getMobileNumber(),KENYA_COUNTY_MOBILE_CODE+"",KENYA_MOBILE_NUMBER_FORMAT+"");
                }else if(mobilePayData.getMobilePayCode().equals(SthiramValues.AIRTAL_UGANDA_CODE)){
                    formattedMobileNumber=commonFunctions.formatAMobileNumber(mobilePayData.getMobileNumber(),UGANDA_COUNTY_MOBILE_CODE+"", SthiramValues.UGANDA_MOBILE_NUMBER_FORMAT+"");
                }
                tvMobileNumber.setText(formattedMobileNumber);
                tvReceiverName.setText(receiverDetails.getCustomerName());
                //set first letter
                String firstLetter = String.valueOf(receiverDetails.getCustomerName().charAt(0));
                tvFirstLetter.setText(firstLetter);
                //set mobile number
            }
            String sign = getString(R.string.sign_usd);
            tvSendAmount.setText(commonFunctions.setAmount(mobilePayData.getSendAmount() + "") + " " + sign);
            tvFees.setText(commonFunctions.setAmount(calculatedFees + "") + " " + sign);
            tvTotalToPay.setText(commonFunctions.setAmount(totalAmount + "") + " " + sign);
            tvReceiverGetsAmount.setText(commonFunctions.setAmount(mobilePayData.getGetAmount()) + " "+ mobilePayData.getReceiverCurrency());
           if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
               senderNameLayout.setVisibility(View.VISIBLE);
               senderName.setText( commonFunctions.generateFullName(mobilePayData.getSenderDetails().getFirstName(),mobilePayData.getSenderDetails().getMiddleName(),mobilePayData.getSenderDetails().getLastName()));
           }else {
               senderNameLayout.setVisibility(View.GONE);
           }
            setReadyToNext();
        } catch (Exception e) {
            somethingWentWrongDialog.show();
        }
    }


    private void setReadyToNext() {
        if (validAmount) {
            llNext.setVisibility(View.VISIBLE);
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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.confirmation);
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
            if (requestCode == 200) {
                makePaymentAPI();
            }
        }
    }

    private void makePaymentAPI() {
        validAmount = false;
        setReadyToNext();
        String remitterId="";
        if(mobilePayData.getSenderDetails()!=null){
            remitterId=mobilePayData.getSenderDetails().getBeneficiaryId();
        }
        apiCall.mpesaPaymentRequestAPI(
                getPaymentRetry,
                true,
                mobilePayData.getMobileNumber() + "",
                commonFunctions.getUserId() + "",
                mobilePayData.getReceiverDetails().getCustomerName() + "",
                mobilePayData.getReceiverDetails().getConversationID() + "",
                mobilePayData.getReceiverDetails().getMmTransactionID() + "",
                mobilePayData.getGetAmount() + "",
                mobilePayData.getSendAmount() + "",
                mobilePayData.getConversionAmount() + "",
                mobilePayData.getYeeltid() + "",
                mobilePayData.getReceiverDetails().getPartnerTransactionID() + "",
                totalAmount + "",
                feesType + "",
                feesValue+"",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                calculatedFees+"",
                mobilePayData.getMobilePayCode()+"",
                mobilePayData.getMobileNumber()+"",
                mobilePayData.getMobileCountyCode()+"",
                mobilePayData.getReceiverCountryCodeWithOutPlus()+"",
                mobilePayData.getReceiverCurrency()+"",
                commonFunctions.getUserType()+"",
                remitterId+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MepsaPaymentResponse apiResponse = gson.fromJson(jsonString, MepsaPaymentResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                mobilePayData.setFeesAmount(calculatedFees + "");
                                mobilePayData.setTotalAmount(totalAmount + "");
                                mobilePayData.setReferenceNumber(apiResponse.getData().getPartnerTransactionId());
                                Intent in = new Intent(context, MobilePayPaymentReceiptActivity.class);
                                in.putExtra("data", mobilePayData);
                                startActivity(in);
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getPaymentRetry = true;
                        makePaymentAPI();
                    }
                }

        );
    }

}