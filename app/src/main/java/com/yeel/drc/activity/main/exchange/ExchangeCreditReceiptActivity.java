package com.yeel.drc.activity.main.exchange;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class ExchangeCreditReceiptActivity extends BaseActivity {
    Context context;
    DialogProgress dialogProgress;
    PermissionUtils permissionUtils;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean isApiCalledRetry = false;
    TransactionDetailsData transactionDetailsData;

    String ydbRefId;
    String notificationId;
    ConstraintLayout screenShotLayout;

    Bitmap myBitmapImage = null;

    TextView tvDate;
    TextView tvStatus;
    TextView tvTransactionId;
    TextView tvExchangeAmount;
    TextView tvExchangeRate;
    TextView tvFees;
    TextView tvTotalAmount;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_credit_receipt);
        context = ExchangeCreditReceiptActivity.this;
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

    }

    private void initView() {
        dialogProgress = new DialogProgress(context);
        permissionUtils = new PermissionUtils(context);
        screenShotLayout = findViewById(R.id.screen_shot_layout);
        apiCall = new APICall(context, SthiramValues.dismiss);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);

        tvDate=findViewById(R.id.tvDate);
        tvStatus=findViewById(R.id.tvStatus);
        tvTransactionId=findViewById(R.id.tvTransactionId);
        tvExchangeAmount=findViewById(R.id.tvExchangeAmount);
        tvExchangeRate=findViewById(R.id.tvExchangeRate);
        tvFees=findViewById(R.id.tvFees);
        tvTotalAmount=findViewById(R.id.tvTotalAmount);
        progressBar=findViewById(R.id.progressBar);
        nestedScrollView=findViewById(R.id.main_layout);
        nestedScrollView.setVisibility(View.GONE);

        getTransactionDetails();
    }

    private void getTransactionDetails() {
        progressBar.setVisibility(View.VISIBLE);
        apiCall.getTransactionDetails(isApiCalledRetry, commonFunctions.getUserId(), ydbRefId, notificationId, false, new CustomCallback() {
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
                isApiCalledRetry = true;
                getTransactionDetails();
            }
        });
    }

    private void setValues() {
        nestedScrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(),"time"));
        tvTransactionId.setText(transactionDetailsData.getYdb_ref_id());
        tvExchangeAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmountUSD())+" USD");
        tvExchangeRate.setText("1 USD = "+commonFunctions.setAmount(transactionDetailsData.getExchange_rate())+" SSP");
        tvFees.setText(commonFunctions.setAmount("0")+" USD");
        tvTotalAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmountSSP())+" SSP");
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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
                if (permissionUtils.checkGalleryPermissionShare(requestPermissionLauncherGallery)) {
                    new BitmapAsyncNew().execute();
                }
                return true;
        }
        return false;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncherGallery =
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