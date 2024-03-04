package com.yeel.drc.activity.main.receipt.credit;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.Objects;

public class MoneyRecivedReceiptActivity extends BaseActivity {
    private LinearLayout llMain;
    private LinearLayout llShare;
    private TextView tvReceiverName;
    private TextView tvReceiverMobile;
    private RelativeLayout rlTvImage;
    private TextView tvImage;
    private RelativeLayout rlIvImage;
    private ImageView tvLogo;
    private TextView tvReceivedDate;
    private TextView tvTransactionId;
    private TextView tvDeliveryMethod;
    private TextView tvSenderName;
    private LinearLayout llRemark;
    private TextView tvRemarks;
    private TextView tvTotalToPay;
    private ProgressBar progressBar;
    private DialogProgress dialogProgress;
    private MoneyRecivedReceiptActivity context;
    Bitmap myBitmapImage = null;
    String ydbRefId;
    String notificationId;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TransactionDetailsData transactionDetailsData;
    boolean getTransactionDetailsRetry = false;
    RelativeLayout rlMain;
    PermissionUtils permissionUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_recived_receipt);
        context = MoneyRecivedReceiptActivity.this;
        dialogProgress = new DialogProgress(context);
        ydbRefId = getIntent().getStringExtra("ydb_ref_id");
        notificationId = getIntent().getStringExtra("notificationId");
        if (notificationId == null) {
            notificationId = "";
        }
        setToolBar();
        initViews();
    }


    private void initViews() {
        permissionUtils=new PermissionUtils(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        llMain = (LinearLayout) findViewById(R.id.ll_main);
        llMain.setVisibility(View.GONE);
        llShare = (LinearLayout) findViewById(R.id.ll_share);
        tvReceiverName = (TextView) findViewById(R.id.tvReceiverName);
        tvReceiverMobile = (TextView) findViewById(R.id.tvReceiverMobile);
        rlTvImage = (RelativeLayout) findViewById(R.id.rl_tv_image);
        tvImage = (TextView) findViewById(R.id.tv_image);
        rlIvImage = (RelativeLayout) findViewById(R.id.rl_iv_image);
        tvLogo = (ImageView) findViewById(R.id.tv_logo);
        tvReceivedDate = (TextView) findViewById(R.id.tvReceivedDate);
        tvTransactionId = (TextView) findViewById(R.id.tvTransactionId);
        tvDeliveryMethod = (TextView) findViewById(R.id.tvDeliveryMethod);
        tvSenderName = (TextView) findViewById(R.id.tv_sender_name);
        llRemark = (LinearLayout) findViewById(R.id.ll_remark);
        tvRemarks = (TextView) findViewById(R.id.tv_remarks);
        tvTotalToPay = findViewById(R.id.tvTotalToPay);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        rlMain = findViewById(R.id.rl_main);
        getTransactionDetails();

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
                finish();
                return true;
            case R.id.id_share:
                if(permissionUtils.checkGalleryPermissionShare(requestPermissionShare)){
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
                myBitmapImage = Bitmap.createBitmap(rlMain.getWidth(), rlMain.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                rlMain.draw(canvas);
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

    private void getTransactionDetails() {
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
        llMain.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvReceiverName.setText(transactionDetailsData.getSender_name());
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),transactionDetailsData.getCountryCode());
        tvReceiverMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getMobile(),transactionDetailsData.getCountryCode(),mobileNumberFormat));
        tvTransactionId.setText(transactionDetailsData.getYdb_ref_id());
        tvDeliveryMethod.setText(transactionDetailsData.getTransaction_type());
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            tvTotalToPay.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getAmount()));
        } else {
            tvTotalToPay.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        }
        tvReceivedDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "time"));
        if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
            if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                tvSenderName.setText(commonFunctions.getFullName());
            } else {
                tvSenderName.setText(commonFunctions.getBusinessName());
            }
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            tvSenderName.setText(commonFunctions.getFullName());
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            tvSenderName.setText(commonFunctions.getBusinessName());
        }
        if (transactionDetailsData.getRemarks() != null && !transactionDetailsData.getRemarks().equals("")) {
            tvRemarks.setText(transactionDetailsData.getRemarks());
            llRemark.setVisibility(View.VISIBLE);
        } else {
            llRemark.setVisibility(View.GONE);
            tvRemarks.setText("----");
        }
        String firstLetter = String.valueOf(transactionDetailsData.getSender_name().charAt(0));
        tvImage.setText(firstLetter);
        //profile image
        String profileImage = transactionDetailsData.getProfile_image();
        if (profileImage != null && !profileImage.equals("")) {
            rlIvImage.setVisibility(View.VISIBLE);
            rlTvImage.setVisibility(View.GONE);
            Glide.with(MoneyRecivedReceiptActivity.this)
                    .load(profileImage)
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(tvLogo);
        } else {
            rlIvImage.setVisibility(View.GONE);
            rlTvImage.setVisibility(View.VISIBLE);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });


}