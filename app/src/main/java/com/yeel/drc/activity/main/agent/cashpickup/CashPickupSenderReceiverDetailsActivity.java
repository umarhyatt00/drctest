package com.yeel.drc.activity.main.agent.cashpickup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.yeel.drc.R;
import com.yeel.drc.activity.main.agent.common.AddBeneficiaryDetailsActivity;
import com.yeel.drc.activity.main.agent.common.UploadSenderDocumentsActivity;
import com.yeel.drc.activity.main.cashpickup.CashPickupDetailsActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.cashpickup.CashPickupData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

public class CashPickupSenderReceiverDetailsActivity extends BaseActivity {

    CommonFunctions commonFunctions;
    CashPickupData cashPickupData;

    TextView btnAddSender, btnAddReceiver, btnContinue, txtSenderEdit, txtReceiverEdit,tvSenderDocument;
    TextView txtReceiverName, txtReceiverEmail, txtReceiverMobile, txtSenderName, txtSenderEmail, txtSenderMobile, txtSenderLocation, txtReceiverLocation;
    Group grpSenderData, grpSenderAddedData, grpReceiverData, grpReceiverAddedData;
    Group grpReceiverEmail, grpSenderEmail;

    private static final int SENDER_DETAILS_INTENT_ID = 110;
    private static final int RECEIVER_DETAILS_INTENT_ID = 111;
    APICall apiCall;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_sender_receiver_details);
        context=CashPickupSenderReceiverDetailsActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setReceiverDetails(BeneficiaryCommonData customerData) {

        grpReceiverData.setVisibility(View.GONE);
        grpReceiverAddedData.setVisibility(View.VISIBLE);

        txtReceiverName.setText(commonFunctions.generateFullName(customerData.getFirstName(), customerData.getMiddleName(), customerData.getLastName()));

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),customerData.getMobileCountryCode());
        String formattedMobileNumber=commonFunctions.formatAMobileNumber(customerData.getMobileNumber(),customerData.getMobileCountryCode(),mobileNumberFormat);
        txtReceiverMobile.setText(formattedMobileNumber);


        txtReceiverEmail.setText(customerData.getEmailAddress());
        txtReceiverLocation.setText(customerData.getCountryName());

        if (!customerData.getEmailAddress().isEmpty()) {
            grpReceiverEmail.setVisibility(View.VISIBLE);
        } else {
            grpReceiverEmail.setVisibility(View.GONE);
        }
        cashPickupData.setCashPickupReceiverData(customerData);

        enableContinueBtn();

    }

    private void setSenderDetails(BeneficiaryCommonData customerData) {
        grpSenderData.setVisibility(View.GONE);
        grpSenderAddedData.setVisibility(View.VISIBLE);

        txtSenderName.setText(commonFunctions.generateFullName(customerData.getFirstName(), customerData.getMiddleName(), customerData.getLastName()));

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),customerData.getMobileCountryCode());
        String formattedMobileNumber=commonFunctions.formatAMobileNumber(customerData.getMobileNumber(),customerData.getMobileCountryCode(),mobileNumberFormat);
        txtSenderMobile.setText(formattedMobileNumber);


        txtSenderEmail.setText(customerData.getEmailAddress());
        txtSenderLocation.setText(customerData.getCountryName());

        if (!customerData.getEmailAddress().isEmpty()) {
            grpSenderEmail.setVisibility(View.VISIBLE);
        } else {
            grpSenderEmail.setVisibility(View.GONE);
        }

        cashPickupData.setCashPickupSenderData(customerData);

        enableContinueBtn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SENDER_DETAILS_INTENT_ID) {
                if (data.hasExtra("customer_data")) {
                    BeneficiaryCommonData customerData = (BeneficiaryCommonData) data.getSerializableExtra("customer_data");
                    if (cashPickupData.getCashPickupReceiverData() != null) {
                        if (cashPickupData.getCashPickupReceiverData().getBeneficiaryId().equals(customerData.getBeneficiaryId())) {
                            Toast.makeText(getBaseContext(), "Sender and Receiver can't be the same", Toast.LENGTH_SHORT).show();
                        } else {
                            setSenderDetails(customerData);
                        }
                    } else {
                        setSenderDetails(customerData);
                    }
                }

            } else if (requestCode == RECEIVER_DETAILS_INTENT_ID) {
                if (data.hasExtra("customer_data")) {
                    BeneficiaryCommonData customerData = (BeneficiaryCommonData) data.getSerializableExtra("customer_data");
                    if (cashPickupData.getCashPickupSenderData() != null) {
                        if (cashPickupData.getCashPickupSenderData().getBeneficiaryId().equals(customerData.getBeneficiaryId())) {
                            Toast.makeText(getBaseContext(), "Sender and Receiver can't be the same", Toast.LENGTH_SHORT).show();
                        } else {
                            setReceiverDetails(customerData);
                        }
                    } else {
                        setReceiverDetails(customerData);
                    }


                }
            }
        }
    }

    private void enableContinueBtn() {
        if (grpSenderAddedData.getVisibility() == View.VISIBLE && grpReceiverAddedData.getVisibility() == View.VISIBLE) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.bg_button_one);
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundResource(R.drawable.bg_button_three);
        }
    }

    private void setItemListeners() {

        btnAddSender.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddBeneficiaryDetailsActivity.class);
            in.putExtra("customer_type", "sender");
            startActivityForResult(in, SENDER_DETAILS_INTENT_ID);
        });

        txtSenderEdit.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddBeneficiaryDetailsActivity.class);
            in.putExtra("customer_type", "sender");
            in.putExtra("customer_data", cashPickupData.getCashPickupSenderData());
            startActivityForResult(in, SENDER_DETAILS_INTENT_ID);
        });
        btnAddReceiver.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddBeneficiaryDetailsActivity.class);
            in.putExtra("customer_type", "receiver");
            startActivityForResult(in, RECEIVER_DETAILS_INTENT_ID);
        });
        txtReceiverEdit.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddBeneficiaryDetailsActivity.class);
            in.putExtra("customer_type", "receiver");
            in.putExtra("customer_data", cashPickupData.getCashPickupReceiverData());
            startActivityForResult(in, RECEIVER_DETAILS_INTENT_ID);
        });

        btnContinue.setOnClickListener(v -> {
            cashPickupData.setCashPickupType(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP);
            Intent in = new Intent(getBaseContext(), CashPickupDetailsActivity.class);
            in.putExtra("data", cashPickupData);
            startActivity(in);
        });
        tvSenderDocument.setOnClickListener(view -> {
            Intent in=new Intent(context, UploadSenderDocumentsActivity.class);
            in.putExtra("beneficiaryID",cashPickupData.getCashPickupSenderData().getBeneficiaryId());
            in.putExtra("beneficiaryIDImage",cashPickupData.getCashPickupSenderData().getBeneficiaryIdImage());
            viewSenderIdActivityResultLauncher.launch(in);
        });
    }

    ActivityResultLauncher<Intent> viewSenderIdActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String beneficiaryIDImage=data.getStringExtra("beneficiaryIDImage");
                        cashPickupData.getCashPickupSenderData().setBeneficiaryIdImage(beneficiaryIDImage);
                    }
                }
            });


    private void initView() {
        commonFunctions = new CommonFunctions(getBaseContext());
        cashPickupData = new CashPickupData();
        apiCall=new APICall(context, SthiramValues.dismiss);

        btnAddSender = findViewById(R.id.btnAddSenderDetail);
        btnAddReceiver = findViewById(R.id.btnAddReceiverDetail);
        btnContinue = findViewById(R.id.tv_continue);
        txtSenderEdit = findViewById(R.id.text_sender_edit);
        txtReceiverEdit = findViewById(R.id.text_receiver_edit);

        grpSenderData = findViewById(R.id.grpSenderData);
        grpSenderAddedData = findViewById(R.id.grpAddedSenderData);
        grpReceiverData = findViewById(R.id.grp_receiver_Data);
        grpReceiverAddedData = findViewById(R.id.grpAdded_receiver_Data);

        grpSenderEmail = findViewById(R.id.group_sender_email);
        grpReceiverEmail = findViewById(R.id.group_receiver_email);

        txtReceiverName = findViewById(R.id.text_receiver_name);
        txtReceiverMobile = findViewById(R.id.text_receiver_mobile);
        txtReceiverEmail = findViewById(R.id.text_receiver_email);

        txtSenderName = findViewById(R.id.text_sender_name);
        txtSenderMobile = findViewById(R.id.text_sender_mobile);
        txtSenderEmail = findViewById(R.id.text_sender_email);

        txtSenderLocation = findViewById(R.id.text_sender_location);
        txtReceiverLocation = findViewById(R.id.text_receiver_location);
        tvSenderDocument=findViewById(R.id.tvSenderDocument);


        btnContinue.setEnabled(false);

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.enter_detail);
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