package com.yeel.drc.activity.main.agent.cashpickup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.activity.main.cashpickup.CashPickupReceiptActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CashOutResponse;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.cashpickup.CashPickupData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class AgentCashPickupConfirmationActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean individualCashPickupAPIRetry=false;
    CashPickupData cashPickupData;
    ScrollView scrollView;
    ProgressBar progressBar;


    TextView tvFirstLetter;
    ImageView ivUser;
    TextView tvReceiverName;
    TextView tvMobileNumber;
    TextView tvSendAmount;
    TextView tvDeliveryMethod;
    TextView tvPurpose;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvReceiverGetsAmount;
    TextView tvContinue;
    TextView tvSenderName;


    boolean getFeesRetry = false;
    boolean paymentRetry = false;
    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_cash_pickup_confirmation);
        cashPickupData=(CashPickupData) getIntent().getSerializableExtra("data");
        context= AgentCashPickupConfirmationActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }
    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            String message=commonFunctions.checkTransactionIsPossible(cashPickupData.getTotalAmount()+"",cashPickupData.getAmount(), SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP );
            if(message.equals(SthiramValues.SUCCESS)){
                Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                startActivityForResult(intent, 200);
            }
        });
    }

    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.home);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall=new APICall(context, SthiramValues.home);

        tvFirstLetter=findViewById(R.id.tv_first_Letter);
        ivUser=findViewById(R.id.iv_user);
        tvReceiverName=findViewById(R.id.tv_receiver_name);
        tvSendAmount=findViewById(R.id.tv_send_amount);
        tvDeliveryMethod=findViewById(R.id.tv_delivery_method);
        tvPurpose=findViewById(R.id.tv_purpose);
        tvFees=findViewById(R.id.tv_fees);
        tvTotalToPay=findViewById(R.id.tv_total_to_pay);
        tvReceiverGetsAmount=findViewById(R.id.tv_receiver_gets_amount);
        tvMobileNumber=findViewById(R.id.edt_mobile_number);
        tvContinue=findViewById(R.id.tv_continue);
        scrollView=findViewById(R.id.ll_scroll_view);
        scrollView.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        tvSenderName=findViewById(R.id.tv_sender_name);

        tvContinue.setBackgroundResource(R.drawable.bg_button_one);
        tvContinue.setEnabled(true);
        callFeesAPI();

    }

    private void callFeesAPI() {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Agent Cash Pickup",
                cashPickupData.getAmount()+"",
                commonFunctions.getUserId()+"",
                commonFunctions.getCurrencyID(),
                new CustomCallback(){
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
        try {
            double calculatedFees=0.00;
            double totalAmount=0.00;
            double amount= Double.parseDouble(cashPickupData.getAmount());
            calculatedFees=commonFunctions.calculateCommission(feesType+"",amount+"",feesValue+"");
            totalAmount = amount + calculatedFees;
            cashPickupData.setFeesAmount(calculatedFees+"");
            cashPickupData.setTotalAmount(totalAmount+"");
            cashPickupData.setFeesTierName(feesTierName);
            cashPickupData.setFeesValue(feesValue);
            setDetails();
        } catch (Exception e) {
            // setAmountToZero();
        }
    }

    private void setDetails() {
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        BeneficiaryCommonData cashPickupReceiverData=cashPickupData.getCashPickupReceiverData();
        String fullName=commonFunctions.generateFullName(cashPickupReceiverData.getFirstName(),cashPickupReceiverData.getMiddleName(),cashPickupReceiverData.getLastName());
        tvReceiverName.setText(fullName);
        //set first letter
        String firstLetter= String.valueOf(fullName.charAt(0));
        tvFirstLetter.setText(firstLetter);
        //set mobile number

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),cashPickupReceiverData.getMobileCountryCode());
        tvMobileNumber.setText(commonFunctions.formatAMobileNumber(cashPickupReceiverData.getMobileNumber(),cashPickupReceiverData.getMobileCountryCode(),mobileNumberFormat));

        tvPurpose.setText(cashPickupData.getPurpose());
        tvDeliveryMethod.setText("Cash Pickup");

        BeneficiaryCommonData cashPickupSenderData=cashPickupData.getCashPickupSenderData();
        String senderFullName=commonFunctions.generateFullName(cashPickupSenderData.getFirstName(),cashPickupSenderData.getMiddleName(),cashPickupSenderData.getLastName());
        tvSenderName.setText(senderFullName);

        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
        tvSendAmount.setText(commonFunctions.setAmount(cashPickupData.getAmount())+" "+sign);
        tvFees.setText(commonFunctions.setAmount(cashPickupData.getFeesAmount()) + " " + sign);
        tvTotalToPay.setText(commonFunctions.setAmount(cashPickupData.getTotalAmount()) + " " + sign);
        tvReceiverGetsAmount.setText(commonFunctions.setAmount(cashPickupData.getAmount())+" "+sign);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
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
                //call API
                callCashPickupAPI();
               /* if(commonFunctions.getUserType().equals(Constants.ACCOUNT_TYPE_AGENT)){
                    if(commonFunctions.getAgentType().equals(Constants.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){

                    }else{

                    }
                }*/
            }
        }
    }

    private void callCashPickupAPI() {
        String agentName;
        if (cashPickupData.getCashPickupAgentData().getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
            agentName=commonFunctions.generateFullName(
                    cashPickupData.getCashPickupAgentData().getFirstname(),
                    cashPickupData.getCashPickupAgentData().getMiddlename(),
                    cashPickupData.getCashPickupAgentData().getLastname()
            );
        } else {
            agentName = cashPickupData.getCashPickupAgentData().getBusiness_name();
        }

        apiCall.doCasPickupFromAgentSide(
                individualCashPickupAPIRetry,
                true,
                commonFunctions.getUserType() + "",
                commonFunctions.getUserAccountNumber() + "",
                cashPickupData.getCashPickupAgentData().getAccount_no() + "",
                cashPickupData.getTotalAmount() + "",
                cashPickupData.getAdditionalNotes() + "",
                commonFunctions.getUserId() + "",
                commonFunctions.getFullName() + "",
                cashPickupData.getCashPickupAgentData().getId() + "",
                agentName + "",
                cashPickupData.getFeesAmount() + "",
                cashPickupData.getAmount() + "",
                cashPickupData.getFeesValue() + "",
                cashPickupData.getFeesTierName() + "",
                SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP+"",
                cashPickupData.getCashPickupSenderData().getBeneficiaryId()+"",
                cashPickupData.getCashPickupReceiverData().getBeneficiaryId()+"",
                cashPickupData.getPurpose()+"",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                        Gson gson = new Gson();
                        CashOutResponse apiResponse = gson.fromJson(jsonString, CashOutResponse.class);
                        if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                            cashPickupData.setReferenceNumber(apiResponse.getYdb_ref_id());
                            Intent in = new Intent(context, CashPickupReceiptActivity.class);
                            in.putExtra("data", cashPickupData);
                            startActivity(in);
                        } else {
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void retry() {
                        individualCashPickupAPIRetry = true;
                        callCashPickupAPI();
                    }
                }

        );
    }
}