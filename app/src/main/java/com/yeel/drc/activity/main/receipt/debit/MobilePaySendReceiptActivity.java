package com.yeel.drc.activity.main.receipt.debit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.mobilepay.MobilePayEstimateActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.mpesa.MobilePayData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.Objects;

public class MobilePaySendReceiptActivity extends BaseActivity {
    Context context;
    DialogProgress dialogProgress;
    ConstraintLayout screenShotLayout;
    TextView textReceiverName, textReceiverMobile;
    TextView tvFirstLetter;
    TextView textSendDate, textTransactionId, textPurpose, textExchangeRate, textSendAmount, textFees;
    LinearLayout purposeLayout;
    TextView textTotalPaid, textReceiverGetsAmount;
    TextView textSendAgain;
    TextView tvReceiverGetsAmount;
    TextView tvTotalPaid;
    TextView tvTransactionType;
    ProgressBar progressBar;
    Bitmap myBitmapImage = null;
    NestedScrollView scrollView;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TransactionDetailsData transactionDetailsData;
    String notificationId;
    boolean getTransactionDetailsRetry = false;
    String ydbRefId;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mepsa_send_receipt);
        context = MobilePaySendReceiptActivity.this;
        dialogProgress = new DialogProgress(context);
        ydbRefId = getIntent().getStringExtra("ydb_ref_id");
        notificationId = getIntent().getStringExtra("notificationId");
        if (notificationId == null) {
            notificationId = "";
        }
        setToolBar();
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        textSendAgain.setOnClickListener(view -> {
            boolean buttonStatus = false;
            MobilePayData mobilePayData = new MobilePayData();
            if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_MPESA)) {
                mobilePayData.setMobilePayName("M-Pesa Kenya");
                mobilePayData.setMobilePayCode("MP");
                if (commonFunctions.getMpesaKenyaStatus().equals("1")) {
                    buttonStatus = true;
                }
            } else if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA)) {
                mobilePayData.setMobilePayName("Airtel Uganda");
                mobilePayData.setMobilePayCode("AU");
                if (commonFunctions.getAirtelUgandaStatus().equals("1")) {
                    buttonStatus = true;
                }
            }
            if (buttonStatus) {
                Intent in = new Intent(context, MobilePayEstimateActivity.class);
                in.putExtra("data", mobilePayData);
                startActivity(in);
            } else {
                errorDialog.show(getString(R.string.send_again_disabled_message));
            }

        });
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        screenShotLayout = findViewById(R.id.screen_shot_layout);
        progressBar = findViewById(R.id.progressBar);
        textReceiverName = findViewById(R.id.tvReceiverName);
        textReceiverMobile = findViewById(R.id.tvReceiverMobile);
        tvFirstLetter = findViewById(R.id.tv_first_letter);
        textSendDate = findViewById(R.id.tv_date);
        textTransactionId = findViewById(R.id.tv_transaction_id);
        textPurpose = findViewById(R.id.tv_purpose);
        textExchangeRate = findViewById(R.id.tv_exchange_rate);
        textSendAmount = findViewById(R.id.tv_send_amount);
        textFees = findViewById(R.id.tv_fees);
        tvReceiverGetsAmount = findViewById(R.id.tv_receiver_gets_amount);
        tvTotalPaid = findViewById(R.id.tv_total_paid);
        purposeLayout = findViewById(R.id.layout_purpose);
        textTotalPaid = findViewById(R.id.tv_receiver_gets_amount);
        textReceiverGetsAmount = findViewById(R.id.tv_receiver_gets_amount);
        textSendAgain = findViewById(R.id.tvSendAgain);
        scrollView = findViewById(R.id.main_layout);
        scrollView.setVisibility(View.GONE);
        tvTransactionType = findViewById(R.id.tv_delivery_method);
        getTransactionDetails();
    }

    private void getTransactionDetails() {
        progressBar.setVisibility(View.VISIBLE);
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
                    progressBar.setVisibility(View.GONE);
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                    progressBar.setVisibility(View.GONE);
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
        scrollView.setVisibility(View.VISIBLE);
        textReceiverName.setText(transactionDetailsData.getReceiver_name());
        //set mobile number
        if (transactionDetailsData.getmMoneyReceiverCountryCode() != null && !transactionDetailsData.getmMoneyReceiverCountryCode().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getmMoneyReceiverCountryCode());
            textReceiverMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getmMoneyReceiverMobile(), transactionDetailsData.getmMoneyReceiverCountryCode(), mobileNumberFormat));
        } else {
            textReceiverMobile.setText("Not available");
        }
        //show first letter
        String firstLetter = String.valueOf(transactionDetailsData.getReceiver_name().charAt(0));
        tvFirstLetter.setText(firstLetter);
        String receiverCurrency = "";
        if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA)) {
            receiverCurrency = SthiramValues.UGANDA_CURRENCY_CODE;
        } else if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_MPESA)) {
            receiverCurrency = SthiramValues.KENYA_CURRENCY_CODE;
        }
        //time
        textSendDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "time"));
        //transction id
        textTransactionId.setText(transactionDetailsData.getYdb_ref_id());
        //rate
        textExchangeRate.setText("1 USD = " + transactionDetailsData.getExchange_rate() + " " + receiverCurrency);
        //send amount
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        textSendAmount.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getAmount()));
        textFees.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getCommission_amount()));
        tvTotalPaid.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()));
        tvReceiverGetsAmount.setText(receiverCurrency + " " + commonFunctions.setAmount(transactionDetailsData.getReceiver_gets_amt()));
        //remark
        purposeLayout.setVisibility(View.GONE);
        tvTransactionType.setText(transactionDetailsData.getTransaction_type());
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
                finish();
                return true;
            case R.id.id_share:
                if (permissionUtils.checkGalleryPermissionShare(requestPermissionShare)) {
                    new BitmapAsyncNew().execute();
                }
                return true;

        }
        return false;
    }


    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new BitmapAsyncNew().execute();
                }
            });


    @SuppressLint("StaticFieldLeak")
    class BitmapAsyncNew extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress.show();
        }

        protected String doInBackground(String... urls) {
            try {
                myBitmapImage = Bitmap.createBitmap(screenShotLayout.getWidth(), screenShotLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                screenShotLayout.draw(canvas);
                return "";
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            if (myBitmapImage != null) {
                shareImage();
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

}