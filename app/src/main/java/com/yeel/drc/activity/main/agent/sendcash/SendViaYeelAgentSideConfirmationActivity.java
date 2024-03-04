package com.yeel.drc.activity.main.agent.sendcash;

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
import com.yeel.drc.api.CashOutResponse;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.sendviayeel.SendYeelToYeelModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

public class SendViaYeelAgentSideConfirmationActivity extends BaseActivity {
    Context context;
    CommonFunctions commonFunctions;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    SendYeelToYeelModel sendYeelToYeelData;
    TextView tvFirstLetter;
    ImageView ivUser;
    TextView tvReceiverName;
    TextView tvSenderName;
    TextView tvMobileNumber;
    TextView tvSendAmount;
    TextView tvDeliveryMethod;
    TextView tvPurpose;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvReceiverGetsAmount;
    TextView tvContinue;
    LinearLayout layoutPurpose;
    ScrollView scrollView;

    private static final int PIN_INTENT_ID = 200;

    boolean getFeesRetry = false;
    boolean paymentRetry = false;
    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        context = SendViaYeelAgentSideConfirmationActivity.this;

        sendYeelToYeelData = (SendYeelToYeelModel) getIntent().getSerializableExtra("data");

        setToolBar();
        initView();
        setItemListeners();

    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(v -> {
            String message=commonFunctions.checkTransactionIsPossible(sendYeelToYeelData.getTotalAmount()+"",sendYeelToYeelData.getAmount()+"", SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL);
            if(message.equals(SthiramValues.SUCCESS)){
                Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                startActivityForResult(intent, PIN_INTENT_ID);
            }

        });

    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall = new APICall(context, SthiramValues.home);
        commonFunctions = new CommonFunctions(context);

        tvFirstLetter = findViewById(R.id.tv_first_Letter);
        ivUser = findViewById(R.id.iv_user);
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvSenderName = findViewById(R.id.tv_sender_name);
        tvSendAmount = findViewById(R.id.tv_send_amount);
        tvDeliveryMethod = findViewById(R.id.tv_delivery_method);
        tvPurpose = findViewById(R.id.tv_purpose);
        tvFees = findViewById(R.id.tv_fees);
        tvTotalToPay = findViewById(R.id.tv_total_to_pay);
        tvReceiverGetsAmount = findViewById(R.id.tv_receiver_gets_amount);
        tvMobileNumber = findViewById(R.id.edt_mobile_number);
        tvContinue = findViewById(R.id.tv_continue);
        layoutPurpose = findViewById(R.id.layout_purpose);
        scrollView=findViewById(R.id.ll_scroll_view);
        scrollView.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        tvContinue.setBackgroundResource(R.drawable.bg_button_one);
        tvContinue.setEnabled(true);
        callFeesAPI();
    }

    private void callFeesAPI() {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Agent Yeel Account",
                sendYeelToYeelData.getAmount()+"",
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
            double amount= Double.parseDouble(sendYeelToYeelData.getAmount());
            calculatedFees=commonFunctions.calculateCommission(feesType+"",amount+"",feesValue+"");
            totalAmount = amount + calculatedFees;
            sendYeelToYeelData.setFeesAmount(calculatedFees+"");
            sendYeelToYeelData.setTotalAmount(totalAmount+"");
            sendYeelToYeelData.setFeesTierName(feesTierName);
            sendYeelToYeelData.setFeesValue(feesValue);
            setDetails();
        } catch (Exception e) {
            // setAmountToZero();
        }
    }


    private void setDetails() {
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        BeneficiaryCommonData senderData = sendYeelToYeelData.getSenderData();
        UserDetailsData receiverData = sendYeelToYeelData.getReceiverData();


        String fullNameReceiver = commonFunctions.generateFullNameFromUserDetails(receiverData);


        String fullNameSender = commonFunctions.generateFullName(senderData.getFirstName(), senderData.getMiddleName(), senderData.getLastName());
        tvReceiverName.setText(fullNameReceiver);
        tvSenderName.setText(fullNameSender);
        //set first letter
        String firstLetter = String.valueOf(fullNameReceiver.charAt(0));
        tvFirstLetter.setText(firstLetter);
        //set mobile number
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),receiverData.getCountry_code());
        String formattedMobileNumber=commonFunctions.formatAMobileNumber(receiverData.getMobile(),receiverData.getCountry_code(),mobileNumberFormat);
        tvMobileNumber.setText(formattedMobileNumber);

        //set amounts
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        tvSendAmount.setText(commonFunctions.setAmount(sendYeelToYeelData.getAmount()) + " " + sign);
        tvFees.setText(commonFunctions.setAmount(sendYeelToYeelData.getFeesAmount()) + " " + sign);
        tvTotalToPay.setText(commonFunctions.setAmount(sendYeelToYeelData.getTotalAmount()) + " " + sign);
        tvReceiverGetsAmount.setText(commonFunctions.setAmount(sendYeelToYeelData.getAmount()) + " " + sign);

     /*   if (sendYeelToYeelData.getPurpose()!=null&&!sendYeelToYeelData.getPurpose().equals("")) {
            layoutPurpose.setVisibility(View.VISIBLE);
            tvPurpose.setText(sendYeelToYeelData.getPurpose());
        } else {
            layoutPurpose.setVisibility(View.GONE);
        }*/

        layoutPurpose.setVisibility(View.GONE);

        tvDeliveryMethod.setText("Yeel");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PIN_INTENT_ID) {
                callPaymentAPI();
            }
        }
    }

    private void callPaymentAPI() {
        String receiverName=commonFunctions.generateFullNameFromUserDetails(sendYeelToYeelData.getReceiverData());
        apiCall.sendViaYeelAgentSide(paymentRetry,
                true,
                commonFunctions.getUserType() + "",
                commonFunctions.getUserAccountNumber() + "",
                sendYeelToYeelData.getReceiverData().getAccount_no() + "",
                sendYeelToYeelData.getTotalAmount() + "",
                sendYeelToYeelData.getPurpose() + "",
                commonFunctions.getUserId() + "",
                commonFunctions.getFullName() + "",
                sendYeelToYeelData.getReceiverData().getId() + "",
                receiverName + "",
                sendYeelToYeelData.getFeesAmount() + "",
                sendYeelToYeelData.getAmount() + "",
                sendYeelToYeelData.getFeesValue() + "",
                sendYeelToYeelData.getFeesTierName() + "",
                SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL+"",
                sendYeelToYeelData.getSenderData().getBeneficiaryId()+"",
                sendYeelToYeelData.getPurpose()+"",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                        Gson gson = new Gson();
                        CashOutResponse apiResponse = gson.fromJson(jsonString, CashOutResponse.class);
                        if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                            sendYeelToYeelData.setReferenceNumber(apiResponse.getYdb_ref_id());
                            paymentSuccessDialog();
                        } else {
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void retry() {
                        paymentRetry = true;
                        callPaymentAPI();
                    }
                }

        );
    }

    private void paymentSuccessDialog() {

        UserDetailsData receiverData = sendYeelToYeelData.getReceiverData();
        String fullNameReceiver = commonFunctions.generateFullNameFromUserDetails(receiverData);

        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.payment_success_dialoge);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(commonFunctions.setAmount(sendYeelToYeelData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        String willReceive = getString(R.string.will_receive);
        tvUserName.setText(fullNameReceiver + " " + willReceive);
        tvTitle.setText(R.string.payment_sent_successfully);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                success.dismiss();
                commonFunctions.callHomeIntent();
            }
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
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
}