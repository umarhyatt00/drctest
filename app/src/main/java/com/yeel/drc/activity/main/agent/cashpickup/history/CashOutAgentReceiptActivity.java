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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.DialogProgress;

public class CashOutAgentReceiptActivity extends BaseActivity {
    Context context;
    CommonFunctions commonFunctions;
    DialogProgress dialogProgress;
    Bitmap myBitmapImage = null;
    LinearLayout llMain;
    TextView tvDate;
    TextView tvStatus;
    TextView tvTransactionId;
    TextView tvDeliveryMethod;
    TextView tvAmount;
    TextView tvReceiverName;
    TextView tvMobileNumber;
    TextView tvCashPickupDetails;
    ImageView ivUser;
    TextView tvFirstLetter;
    TransactionDetailsData transactionDetailsData;
    APICall apiCall;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_out_agent_receipt);
        setToolBar();
        context = CashOutAgentReceiptActivity.this;
        transactionDetailsData = (TransactionDetailsData) getIntent().getSerializableExtra("data");
        initView();
    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        apiCall=new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        dialogProgress = new DialogProgress(context);
        llMain = findViewById(R.id.ll_main);
        tvDate = findViewById(R.id.tv_date);
        tvStatus = findViewById(R.id.tv_status);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvDeliveryMethod = findViewById(R.id.tv_delivery_method);
        tvAmount = findViewById(R.id.tv_amount);
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvCashPickupDetails = findViewById(R.id.tv_cash_pickup_details);
        ivUser = findViewById(R.id.iv_user);
        tvFirstLetter = findViewById(R.id.tv_first_Letter);
        setValue();
    }

    private void setValue() {
        try{
            String firstLetter = String.valueOf(transactionDetailsData.getSender_detail().getFirstname().charAt(0));
            tvFirstLetter.setText(firstLetter);
            tvReceiverName.setText(commonFunctions.generateFullName(transactionDetailsData.getSender_detail().getFirstname(), transactionDetailsData.getSender_detail().getMiddlename(), transactionDetailsData.getSender_detail().getLastname()));

            String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),transactionDetailsData.getSender_detail().getCountry_code());
            tvMobileNumber.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getSender_detail().getMobile(),transactionDetailsData.getSender_detail().getCountry_code(),mobileNumberFormat));


            String address = transactionDetailsData.getAgentDetails().getAddressline1() + ", " + transactionDetailsData.getAgentDetails().getLocality() + ", " + transactionDetailsData.getAgentDetails().getProvince() + ", " + transactionDetailsData.getAgentDetails().getDistrict();
            String agentName = commonFunctions.generateFullName(transactionDetailsData.getAgentDetails().getFirstname(), transactionDetailsData.getAgentDetails().getMiddlename(), transactionDetailsData.getAgentDetails().getLastname());

            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),transactionDetailsData.getAgentDetails().getCountry_code());
            String agentFullMobileNumber=commonFunctions.formatAMobileNumber(transactionDetailsData.getAgentDetails().getMobile(),transactionDetailsData.getAgentDetails().getCountry_code(),agentMobileNumberFormat);

            address = agentName + ", " + address + ", " +agentFullMobileNumber;
            tvCashPickupDetails.setText(address);


            tvDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getCollectionData(), "date2"));
            tvCashPickupDetails.setText(address);

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

            tvTransactionId.setText(transactionDetailsData.getYdb_ref_id());
            tvDeliveryMethod.setText(getString(R.string.cash_out));
            tvAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        }catch (Exception e){
            Toast.makeText(context,e+"",Toast.LENGTH_SHORT).show();
        }

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
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
                if(permissionUtils.checkGalleryPermissionShare(requestPermissionShare)){
                    new bitmapAsyncNew().execute();
                }
                return true;

        }
        return false;
    }

    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });

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
}