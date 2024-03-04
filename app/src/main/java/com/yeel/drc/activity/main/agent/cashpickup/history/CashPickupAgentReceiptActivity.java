package com.yeel.drc.activity.main.agent.cashpickup.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

public class CashPickupAgentReceiptActivity extends AppCompatActivity {
    Context context;
    CommonFunctions commonFunctions;
    DialogProgress dialogProgress;
    Bitmap myBitmapImage = null;
    LinearLayout llMain;
    TextView tvDate;
    TextView tvStatus;
    TextView tvRemitRefNumber;
    TextView tvDeliveryMethod;
    TextView tvPurpose;
    TextView tvAmount;
    TextView tvReceiverName;
    TextView tvMobileNumber;
    TextView tvCashPickupDetails;
    ImageView ivUser;
    TextView tvFirstLetter;
    LinearLayout layoutPurpose;
    TextView tvDone;
    TransactionDetailsData transactionDetailsData;
    APICall apiCall;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_receipt);
        setToolBar();
        context = CashPickupAgentReceiptActivity.this;
        transactionDetailsData = (TransactionDetailsData) getIntent().getSerializableExtra("data");
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvDone.setOnClickListener(view -> commonFunctions.callHomeIntent());
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        dialogProgress = new DialogProgress(context);
        tvDone = findViewById(R.id.tv_continue);
        llMain = findViewById(R.id.ll_main);
        tvDate = findViewById(R.id.tv_date);
        tvStatus = findViewById(R.id.tv_status);
        tvRemitRefNumber = findViewById(R.id.tv_remit_reff_number);
        tvDeliveryMethod = findViewById(R.id.tv_delivery_method);
        tvPurpose = findViewById(R.id.tv_purpose);
        tvAmount = findViewById(R.id.tv_amount);
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvCashPickupDetails = findViewById(R.id.tv_cash_pickup_details);
        ivUser = findViewById(R.id.iv_user);
        tvFirstLetter = findViewById(R.id.tv_first_Letter);
        layoutPurpose = findViewById(R.id.layout_purpose);
        setValue();
    }

    private void setValue() {
        String transactionType = transactionDetailsData.getTransaction_type();
        AgentData agentData = null;
        if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)) {
            agentData = transactionDetailsData.getReceiver_agent_detail();
        } else if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)) {
            //agent data
            agentData = transactionDetailsData.getAgentDetails();
        }
        //beneficiary data
        BeneficiaryData beneficiaryData = transactionDetailsData.getBeneficiaryDetails();
        String firstLetter = String.valueOf(beneficiaryData.getFirstname().charAt(0));
        tvFirstLetter.setText(firstLetter);
        tvReceiverName.setText(commonFunctions.generateFullName(beneficiaryData.getFirstname(), beneficiaryData.getMiddleName(), beneficiaryData.getLastname()));
        String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), beneficiaryData.getCountry_code());
        tvMobileNumber.setText(commonFunctions.formatAMobileNumber(beneficiaryData.getMobile(), beneficiaryData.getCountry_code(), mobileNumberFormat));
        //agent details
        if (agentData != null) {
            String name = "";
            String address = "";
            if (agentData.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                name = commonFunctions.generateFullName(agentData.getFirstname(), agentData.getMiddlename(), agentData.getLastname());
                address = agentData.getAddressline1() + ", " + agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            } else {
                name = agentData.getBusiness_name();
                address = agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            }

            String agentMobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), agentData.getCountry_code());
            String agentFullMobileNumber = commonFunctions.formatAMobileNumber(agentData.getMobile(), agentData.getCountry_code(), agentMobileNumberFormat);

            address = name + ", " + address + ", " + agentFullMobileNumber;
            tvCashPickupDetails.setText(address);
        } else {
            tvCashPickupDetails.setText("Agent Details not available");
        }


        tvStatus.setText(transactionDetailsData.getStatus());
        if (transactionDetailsData.getStatus().equals("Submitted")) {
            tvStatus.setTextColor(context.getColor(R.color.submittedLabelColor));
        } else if (transactionDetailsData.getStatus().equals("Success")) {
            tvStatus.setTextColor(context.getColor(R.color.green));
        } else if (transactionDetailsData.getStatus().equals("Rejected")) {
            tvStatus.setTextColor(context.getColor(R.color.red));
        } else if (transactionDetailsData.getStatus().equals("On-hold")) {
            tvStatus.setTextColor(context.getColor(R.color.holdOnColor));
        } else {
            tvStatus.setTextColor(context.getColor(R.color.submittedLabelColor));
        }

        tvRemitRefNumber.setText(transactionDetailsData.getYdb_ref_id());
        tvDeliveryMethod.setText(getString(R.string.cash_pickup));
        if (transactionDetailsData.getPurpose() == null) {
            layoutPurpose.setVisibility(View.GONE);
        } else {
            tvPurpose.setText(transactionDetailsData.getPurpose());
            layoutPurpose.setVisibility(View.VISIBLE);
        }
        tvAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getCollectionData(), "date2"));

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_share_gray, menu);
        return true;
    }


    //back button click
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                commonFunctions.callHomeIntent();
                return true;
            case R.id.id_share:
                if (permissionUtils.checkGalleryPermissionShare(requestPermissionShare)) {
                    new bitmapAsyncNew().execute();
                }
                return true;

        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class bitmapAsyncNew extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress.show();
        }

        protected String doInBackground(String... urls) {
            try {
                myBitmapImage = Bitmap.createBitmap(llMain.getWidth(), llMain.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                llMain.draw(canvas);
                return "";
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            if (myBitmapImage != null) {
                commonFunctions.logAValue("Bitmap status", "bitmap  is present");
                shareImage();
            } else {
                commonFunctions.logAValue("Bitmap status", "bitmap is empty");
            }
            dialogProgress.dismiss();
        }
    }

    public void shareImage() {
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmapImage, "TRANSACTION" + System.currentTimeMillis(), null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra("subject", "Transaction Details");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Transaction Details");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "text message"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        commonFunctions.callHomeIntent();
    }

    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });
}