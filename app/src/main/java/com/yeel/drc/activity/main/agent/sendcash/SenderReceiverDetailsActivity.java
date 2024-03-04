package com.yeel.drc.activity.main.agent.sendcash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;

import com.yeel.drc.R;
import com.yeel.drc.activity.main.agent.common.AddBeneficiaryDetailsActivity;
import com.yeel.drc.activity.main.agent.common.AddYeelUserDetailsActivity;
import com.yeel.drc.activity.main.agent.common.UploadSenderDocumentsActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.sendviayeel.SendYeelToYeelModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class SenderReceiverDetailsActivity extends BaseActivity {

    CommonFunctions commonFunctions;
    SendYeelToYeelModel sendYeelToYeelData;
    APICall apiCall;
    Context context;

    TextView btnAddSender, btnAddReceiver, btnContinue, txtSenderEdit, txtReceiverEdit;
    TextView txtReceiverName, txtReceiverEmail, txtReceiverMobile, txtSenderName, txtSenderEmail, txtSenderMobile,tvSenderLocation,tvSenderDocument;
    Group grpSenderData, grpSenderAddedData, grpReceiverData, grpReceiverAddedData, groupReceiverEmail, groupSenderEmail;


    private static final int SENDER_DETAILS_INTENT_ID = 110;
    private static final int RECEIVER_DETAILS_INTENT_ID = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_reciever_details);
        context=SenderReceiverDetailsActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void initView() {
        commonFunctions = new CommonFunctions(getBaseContext());
        sendYeelToYeelData = new SendYeelToYeelModel();
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

        groupSenderEmail = findViewById(R.id.group_sender_email);
        groupReceiverEmail = findViewById(R.id.group_receiver_email);

        txtReceiverName = findViewById(R.id.text_receiver_name);
        txtReceiverMobile = findViewById(R.id.text_receiver_mobile);
        txtReceiverEmail = findViewById(R.id.text_receiver_email);

        txtSenderName = findViewById(R.id.text_sender_name);
        txtSenderMobile = findViewById(R.id.text_sender_mobile);
        txtSenderEmail = findViewById(R.id.text_sender_email);
        tvSenderLocation=findViewById(R.id.tvSenderLocation);
        tvSenderDocument=findViewById(R.id.tvSenderDocument);


        btnContinue.setEnabled(false);

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
            in.putExtra("customer_data", sendYeelToYeelData.getSenderData());
            startActivityForResult(in, SENDER_DETAILS_INTENT_ID);
        });
        btnAddReceiver.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddYeelUserDetailsActivity.class);
            in.putExtra("customer_type", "receiver");
            startActivityForResult(in, RECEIVER_DETAILS_INTENT_ID);
        });
        txtReceiverEdit.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), AddYeelUserDetailsActivity.class);
            in.putExtra("customer_type", "receiver");
            in.putExtra("customer_data", sendYeelToYeelData.getReceiverData());
            startActivityForResult(in, RECEIVER_DETAILS_INTENT_ID);
        });

        btnContinue.setOnClickListener(v -> {
            Intent in = new Intent(getBaseContext(), EnterAmountForSendViaYeelAgentActivity.class);
            in.putExtra("data", sendYeelToYeelData);
            startActivity(in);
        });
        tvSenderDocument.setOnClickListener(view -> {
            Intent in=new Intent(context, UploadSenderDocumentsActivity.class);
            in.putExtra("beneficiaryID",sendYeelToYeelData.getSenderData().getBeneficiaryId());
            in.putExtra("beneficiaryIDImage",sendYeelToYeelData.getSenderData().getBeneficiaryIdImage());
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
                        sendYeelToYeelData.getSenderData().setBeneficiaryIdImage(beneficiaryIDImage);

                    }
                }
            });


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if (requestCode == SENDER_DETAILS_INTENT_ID) {
                if (data.hasExtra("customer_data")) {
                    BeneficiaryCommonData customerData = (BeneficiaryCommonData) data.getSerializableExtra("customer_data");
                    setSenderDetails(customerData);
                }

            } else if (requestCode == RECEIVER_DETAILS_INTENT_ID) {
                if (data.hasExtra("customer_data")) {
                    UserDetailsData customerData = (UserDetailsData) data.getSerializableExtra("customer_data");
                    setReceiverDetails(customerData);
                }
            }
        }
    }

    private void setReceiverDetails(UserDetailsData customerData) {
        grpReceiverData.setVisibility(View.GONE);
        grpReceiverAddedData.setVisibility(View.VISIBLE);
        String fullName=commonFunctions.generateFullNameFromUserDetails(customerData);
        txtReceiverName.setText(fullName);
        //set sender mobile number
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),customerData.getCountry_code());
        txtReceiverMobile.setText(commonFunctions.formatAMobileNumber(customerData.getMobile(),customerData.getCountry_code(),mobileNumberFormat));
        txtReceiverEmail.setText(customerData.getEmail());

        sendYeelToYeelData.setReceiverData(customerData);

        if (customerData.getEmail()!=null) {
            groupReceiverEmail.setVisibility(View.VISIBLE);
        } else {
            groupReceiverEmail.setVisibility(View.GONE);
        }

        enableContinueBtn();

    }

    private void setSenderDetails(BeneficiaryCommonData customerData) {
        grpSenderData.setVisibility(View.GONE);
        grpSenderAddedData.setVisibility(View.VISIBLE);

        tvSenderLocation.setText(customerData.getCountryName());

        //set sender mobile number
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),customerData.getMobileCountryCode());
        txtSenderMobile.setText(commonFunctions.formatAMobileNumber(customerData.getMobileNumber(),customerData.getMobileCountryCode(),mobileNumberFormat));

        txtSenderName.setText(commonFunctions.generateFullName(customerData.getFirstName(), customerData.getMiddleName(), customerData.getLastName()));
        txtSenderEmail.setText(customerData.getEmailAddress());
        sendYeelToYeelData.setSenderData(customerData);
        if (!customerData.getEmailAddress().isEmpty()) {
            groupSenderEmail.setVisibility(View.VISIBLE);
        } else {
            groupSenderEmail.setVisibility(View.GONE);
        }
        enableContinueBtn();
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

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.enter_detail);
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