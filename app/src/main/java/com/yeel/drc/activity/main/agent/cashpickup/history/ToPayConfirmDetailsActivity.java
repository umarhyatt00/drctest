package com.yeel.drc.activity.main.agent.cashpickup.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.timeout.BaseActivity;

import java.util.Objects;

public class ToPayConfirmDetailsActivity extends BaseActivity {

    Context context;
    CommonFunctions commonFunctions;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TransactionDetailsData transactionDetailsData;

    TextView tvContinue;
    TextView txtRequestDate, txtRequestAmount, txtRequestRefNumber, txtRequestPickupDetails, txtRequestStatus;
    TextView textSenderName, textSenderMobile, textSenderLocation,textSenderLocationLabel, textSenderEmail,textSenderEmailLabel;
    TextView textReceiverName, textReceiverMobile, textReceiverLocation,textReceiverLocationLabel, textReceiverEmail,textReceiverEmailLabel;
    TextView textCashOutName, textCashOutMobile, textCashOutLocation, textCashOutLocationLabel, textCashOutEmail, textCashOutEmailLabel;
    Group grpCashOut, grpNormalLayout;
    ConstraintLayout mainLayout;
    TransactionsData transactionsData;
    ProgressBar progressBar;

    boolean getTransactionDetailsRetry = false;
    String ydbRefId;
    String notificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_pay_details);
        context = ToPayConfirmDetailsActivity.this;


        transactionsData = (TransactionsData) getIntent().getSerializableExtra("data");
        ydbRefId = transactionsData.getYdb_ref_id();
        notificationId = "";


        setToolBar();
        initView();
        setItemListeners();

    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            String transactionType=transactionDetailsData.getTransaction_type();
            if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)||transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)) {
                Intent in = new Intent(context, CashPickupReceiverDocActivity.class);
                in.putExtra("data", transactionDetailsData);
                startActivity(in);
            } else {
                Intent in = new Intent(context, CashOutReceiverDocActivity.class);
                in.putExtra("data", transactionDetailsData);
                startActivity(in);
            }

        });
    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);

        tvContinue = findViewById(R.id.tv_continue);
        mainLayout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressBar);

        txtRequestDate = findViewById(R.id.txt_request_date);
        txtRequestAmount = findViewById(R.id.text_request_amount);
        txtRequestRefNumber = findViewById(R.id.text_request_ref_number);
        txtRequestPickupDetails = findViewById(R.id.text_request_pickup_details);
        txtRequestStatus = findViewById(R.id.text_request_pickup_status);

        textSenderName = findViewById(R.id.text_sender_name);
        textSenderMobile = findViewById(R.id.text_sender_mobile);
        textSenderLocation = findViewById(R.id.text_sender_location);
        textSenderLocationLabel = findViewById(R.id.text_sender_location_label);
        textSenderEmail = findViewById(R.id.text_sender_email);
        textSenderEmailLabel = findViewById(R.id.text_sender_email_label);

        textReceiverName = findViewById(R.id.text_receiver_name);
        textReceiverMobile = findViewById(R.id.text_receiver_mobile);
        textReceiverLocation = findViewById(R.id.text_receiver_location);
        textReceiverLocationLabel = findViewById(R.id.text_receiver_location_label);
        textReceiverEmail = findViewById(R.id.text_receiver_email);
        textReceiverEmailLabel = findViewById(R.id.text_receiver_email_label);

        textCashOutName = findViewById(R.id.text_cash_out_name);
        textCashOutMobile = findViewById(R.id.text_cash_out_mobile);
        textCashOutLocation = findViewById(R.id.text_cash_out_location);
        textCashOutLocationLabel = findViewById(R.id.text_cash_out_location_label);
        textCashOutEmail = findViewById(R.id.text_cash_out_email);
        textCashOutEmailLabel = findViewById(R.id.text_cash_out_email_label);

        grpCashOut = findViewById(R.id.group_cash_out);
        grpNormalLayout = findViewById(R.id.group_normal);


        getTransactionDetails();
    }

    private void getTransactionDetails() {
        viewLoader();
        apiCall.getTransactionDetails(getTransactionDetailsRetry, commonFunctions.getUserId(), ydbRefId, notificationId, false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    TransactionDetailsResponse apiResponse = gson.fromJson(jsonString, TransactionDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        transactionDetailsData = apiResponse.getTransactionDetailsData();
                        setValues();
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
                getTransactionDetailsRetry = true;
                getTransactionDetails();
            }
        });
    }

    private void setValues() {
        hideLoader();
        //first section
        String message = getString(R.string.requested_on);
        txtRequestDate.setText(message + " " + commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "date2"));
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        txtRequestAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + sign);
        txtRequestRefNumber.setText(transactionDetailsData.getYdb_ref_id());

        String transactionType=transactionsData.getTransaction_type();
        //agent data
        AgentData agentData=null;
        if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){
            agentData=transactionDetailsData.getReceiver_agent_detail();
        }else {
           agentData = transactionDetailsData.getAgentDetails();
        }
        if (agentData != null) {
            String agentFullAddress=commonFunctions.getAgentNameWithFullAddress(agentData);
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),agentMobileNumberFormat);
            txtRequestPickupDetails.setText(agentFullAddress+", "+agentMobileNumber);
        } else {
            txtRequestPickupDetails.setText("Agent Details not available");
        }


        if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
            //cash out
            grpCashOut.setVisibility(View.VISIBLE);
            grpNormalLayout.setVisibility(View.GONE);

            UserDetailsData userDetailsData = transactionDetailsData.getSender_detail();
            textCashOutName.setText(transactionDetailsData.getSender_name());

            String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
            textCashOutMobile.setText(commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),mobileNumberFormat));


            if (userDetailsData.getEmail() != null) {
                if (userDetailsData.getEmail().isEmpty()) {
                    textCashOutEmail.setVisibility(View.GONE);
                    textCashOutEmailLabel.setVisibility(View.GONE);
                } else {
                    textCashOutEmail.setText(userDetailsData.getEmail());
                    textCashOutEmail.setVisibility(View.VISIBLE);
                    textCashOutEmailLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textCashOutEmail.setVisibility(View.GONE);
                textCashOutEmailLabel.setVisibility(View.GONE);
            }

            //show location for cash out
            if(userDetailsData.getDistrict()!=null&&!userDetailsData.getDistrict().equals("")){
                String fullAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);
                textCashOutLocation.setText(fullAddress);
                textCashOutLocation.setVisibility(View.VISIBLE);
                textCashOutLocationLabel.setVisibility(View.VISIBLE);
            }else{
                textCashOutLocation.setVisibility(View.GONE);
                textCashOutLocationLabel.setVisibility(View.GONE);
            }


        }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){

            grpCashOut.setVisibility(View.GONE);
            grpNormalLayout.setVisibility(View.VISIBLE);

            //remitter details----------------Receiver Details
            BeneficiaryData remitterData = transactionDetailsData.getRemitterDetails();
            //remitter full name
            String  remitterFullName=commonFunctions.generateFullName(remitterData.getFirstname(),remitterData.getMiddleName(),remitterData.getLastname());
            textSenderName.setText(remitterFullName);
            //remitter mobile number
            String remitterMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),remitterData.getCountry_code());
            textSenderMobile.setText(commonFunctions.formatAMobileNumber(remitterData.getMobile(),remitterData.getCountry_code(),remitterMobileNumberFormat));
            //remitter email ID
            String remitterMailID=remitterData.getEmail();
            if (remitterMailID != null&&!remitterMailID.equals("")&&!remitterMailID.equals("null")) {
                textSenderEmail.setText(remitterData.getEmail());
                textSenderEmail.setVisibility(View.VISIBLE);
                textSenderEmailLabel.setVisibility(View.VISIBLE);
            }else {
                textSenderEmail.setVisibility(View.GONE);
                textSenderEmailLabel.setVisibility(View.GONE);
            }

            //remitter country
            if (remitterData.getCountry() != null) {
                if (remitterData.getCountry().isEmpty()) {
                    textSenderLocation.setVisibility(View.GONE);
                    textSenderLocationLabel.setVisibility(View.GONE);
                } else {
                    textSenderLocation.setText(remitterData.getCountry());
                    textSenderLocation.setVisibility(View.VISIBLE);
                    textSenderLocationLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textSenderLocation.setVisibility(View.GONE);
                textSenderLocationLabel.setVisibility(View.GONE);
            }



            //receiver details
            BeneficiaryData beneficiaryData = transactionDetailsData.getBeneficiaryDetails();
            String fullName = commonFunctions.generateFullName(beneficiaryData.getFirstname(), beneficiaryData.getMiddleName(), beneficiaryData.getLastname());
            textReceiverName.setText(fullName);

            String receiverMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),beneficiaryData.getCountry_code());
            textReceiverMobile.setText(commonFunctions.formatAMobileNumber(beneficiaryData.getMobile(),beneficiaryData.getCountry_code(),receiverMobileNumberFormat));


            if (beneficiaryData.getEmail() != null) {
                if (beneficiaryData.getEmail().isEmpty()) {
                    textReceiverEmail.setVisibility(View.GONE);
                    textReceiverEmailLabel.setVisibility(View.GONE);
                } else {
                    textReceiverEmail.setText(beneficiaryData.getEmail());
                    textReceiverEmail.setVisibility(View.VISIBLE);
                    textReceiverEmailLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textReceiverEmail.setVisibility(View.GONE);
                textReceiverEmailLabel.setVisibility(View.GONE);
            }

            if (beneficiaryData.getCountry() != null) {
                if (beneficiaryData.getCountry().isEmpty()) {
                    textReceiverLocation.setVisibility(View.GONE);
                    textReceiverLocationLabel.setVisibility(View.GONE);
                } else {
                    textReceiverLocation.setText(beneficiaryData.getCountry());
                    textReceiverLocation.setVisibility(View.VISIBLE);
                    textReceiverLocationLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textReceiverLocation.setVisibility(View.GONE);
                textReceiverLocationLabel.setVisibility(View.GONE);
            }


        } else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)){
            grpCashOut.setVisibility(View.GONE);
            grpNormalLayout.setVisibility(View.VISIBLE);

            //sender details
            UserDetailsData userDetailsData = transactionDetailsData.getSender_detail();
            textSenderName.setText(transactionDetailsData.getSender_name());

            String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
            textSenderMobile.setText(commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),senderMobileNumberFormat));


            if (userDetailsData.getEmail() != null) {
                if (userDetailsData.getEmail().isEmpty()) {
                    textSenderEmail.setVisibility(View.GONE);
                    textSenderEmailLabel.setVisibility(View.GONE);
                } else {
                    textSenderEmail.setText(userDetailsData.getEmail());
                    textSenderEmail.setVisibility(View.VISIBLE);
                    textSenderEmailLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textSenderEmail.setVisibility(View.GONE);
                textSenderEmailLabel.setVisibility(View.GONE);
            }

            if (userDetailsData.getDistrict() != null) {
                if (userDetailsData.getDistrict().isEmpty()) {
                    textSenderLocation.setVisibility(View.GONE);
                    textSenderLocationLabel.setVisibility(View.GONE);
                } else {
                    String fullAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);
                    textSenderLocation.setText(fullAddress);
                    textSenderLocation.setVisibility(View.VISIBLE);
                    textSenderLocationLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textSenderLocation.setVisibility(View.GONE);
                textSenderLocationLabel.setVisibility(View.GONE);
            }

            //receiver details
            BeneficiaryData beneficiaryData = transactionDetailsData.getBeneficiaryDetails();
            String fullName = commonFunctions.generateFullName(beneficiaryData.getFirstname(), beneficiaryData.getMiddleName(), beneficiaryData.getLastname());
            textReceiverName.setText(fullName);

            String receiverMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),beneficiaryData.getCountry_code());
            textReceiverMobile.setText(commonFunctions.formatAMobileNumber(beneficiaryData.getMobile(),beneficiaryData.getCountry_code(),receiverMobileNumberFormat));


            if (beneficiaryData.getEmail() != null) {
                if (beneficiaryData.getEmail().isEmpty()) {
                    textReceiverEmail.setVisibility(View.GONE);
                    textReceiverEmailLabel.setVisibility(View.GONE);
                } else {
                    textReceiverEmail.setText(beneficiaryData.getEmail());
                    textReceiverEmail.setVisibility(View.VISIBLE);
                    textReceiverEmailLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textReceiverEmail.setVisibility(View.GONE);
                textReceiverEmailLabel.setVisibility(View.GONE);
            }

            if (beneficiaryData.getCountry() != null) {
                if (beneficiaryData.getCountry().isEmpty()) {
                    textReceiverLocation.setVisibility(View.GONE);
                    textReceiverLocationLabel.setVisibility(View.GONE);
                } else {
                    textReceiverLocation.setText(beneficiaryData.getCountry());
                    textReceiverLocation.setVisibility(View.VISIBLE);
                    textReceiverLocationLabel.setVisibility(View.VISIBLE);
                }
            }else {
                textReceiverLocation.setVisibility(View.GONE);
                textReceiverLocationLabel.setVisibility(View.GONE);
            }
        }


    }

    private void viewLoader() {
        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        mainLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.confirm_details);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}