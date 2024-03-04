package com.yeel.drc.activity.main.receipt.debit;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.List;
import java.util.Objects;

public class CashoutReciptActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CommonFunctions commonFunctions;

    private DialogProgress dialogProgress;
    TextView tvReceiverName, tvReceiverMobile, textDate, textCashOutDetails, textRefNo, textDeliveryMethod, textPurpose, textSenderName, textRemarks;
    TextView textSendAmount, textFees, textTotalAmount;
    TextView btnSendAgain;
    TextView textPurposeLabel, textRemarksLabel;
    TextView tvImageLetter;
    RelativeLayout rlTvImage;
    ImageView imgProfileImage;
    ProgressBar progressBar;
    NestedScrollView mainLayout;
    RelativeLayout rlIvImage;
    TransactionDetailsData transactionDetailsData;
    String ydbRefId;
    String notificationId;
    Bitmap myBitmapImage = null;
    boolean getTransactionDetailsRetry=false;
    PermissionUtils permissionUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout_recipt);
        context = CashoutReciptActivity.this;
        dialogProgress = new DialogProgress(context);
        ydbRefId = getIntent().getStringExtra("ydb_ref_id");
        notificationId = getIntent().getStringExtra("notificationId");
        if (notificationId == null) {
            notificationId = "";
        }
        setToolBar();
        initViews();
        setOnClickListener();
    }
    private void setOnClickListener() {
        btnSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentData agentData=transactionDetailsData.getAgentDetails();
                List<CurrencyListData> currencyList=transactionDetailsData.getCurrencyList();
                boolean isAccountNumberAvailable = false;
                String receiverAccountNumber="";

                if(currencyList!=null){
                    //check currency available or not
                    for(int i=0;i<currencyList.size();i++){
                        if(commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())){
                            receiverAccountNumber=currencyList.get(i).getAccount_number();
                            isAccountNumberAvailable=true;
                            break;
                        }
                    }
                    if(isAccountNumberAvailable){
                        agentData.setAccount_no(receiverAccountNumber);
                        Intent intent= new Intent(context, CashOutAmountEnteringActivity.class);
                        intent.putExtra("data",agentData);
                        startActivity(intent);
                    }else{
                        String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                        errorDialog.show(message);
                    }
                }else{
                    String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                    errorDialog.show(message);
                }
               /* Intent intent= new Intent(context, CashOutAmountEnteringActivity.class);
                intent.putExtra("data",transactionDetailsData.getAgentDetails());
                startActivity(intent);*/
            }
        });
    }
    private void initViews() {
        permissionUtils=new PermissionUtils(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        commonFunctions=new CommonFunctions(getApplicationContext());
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverMobile = findViewById(R.id.tvReceiverMobile);
        textDate = findViewById(R.id.text_date);
        textCashOutDetails = findViewById(R.id.text_cash_pickup_details);
        textRefNo = findViewById(R.id.text_ref_no);
        textDeliveryMethod = findViewById(R.id.text_delivery_method);
        textPurpose = findViewById(R.id.text_purpose);
        textSenderName = findViewById(R.id.text_sender_name);
        textRemarks = findViewById(R.id.text_remarks);
        textSendAmount = findViewById(R.id.tvSendAmount);
        textFees = findViewById(R.id.tvFees);
        textTotalAmount = findViewById(R.id.tvTotalToPay);
        btnSendAgain = findViewById(R.id.tvSendAgain);
        tvImageLetter = findViewById(R.id.tv_image);
        rlTvImage = findViewById(R.id.rl_tv_image);
        mainLayout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressBar);
        imgProfileImage = findViewById(R.id.tv_logo);
        rlIvImage = findViewById(R.id.rl_iv_image);
//        screenshotLayout = findViewById(R.id.screen_shot_layout);
        textPurposeLabel = findViewById(R.id.text_purpose_label);
        textRemarksLabel = findViewById(R.id.text_remarks_label);

        getTransactionDetails();
    }

    private void getTransactionDetails() {
        viewLoader();
        apiCall.getTransactionDetails(getTransactionDetailsRetry,commonFunctions.getUserId(),ydbRefId,notificationId,false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    TransactionDetailsResponse apiResponse=gson.fromJson(jsonString, TransactionDetailsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        transactionDetailsData=apiResponse.getTransactionDetailsData();
                        setValues(transactionDetailsData);
                    }else{
                        errorDialog.show(apiResponse.getMessage());
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getTransactionDetailsRetry=true;
                getTransactionDetails();
            }
        });
    }
    private void setValues(TransactionDetailsData data) {
        hideLoader();
        tvReceiverName.setText(data.getReceiver_name());

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),data.getCountryCode());
        tvReceiverMobile.setText(commonFunctions.formatAMobileNumber(data.getMobile(),data.getCountryCode(),mobileNumberFormat));

        textDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "time"));

        AgentData agentData=data.getAgentDetails();
        if(agentData!=null){
            String fullAddress=commonFunctions.getAgentNameWithFullAddress(agentData);
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),agentMobileNumberFormat);
            textCashOutDetails.setText(fullAddress+", "+agentMobileNumber);
        }else{
            textCashOutDetails.setText("Agent Details not available");
        }

        textRefNo.setText(data.getYdb_ref_id());
        textPurpose.setText(data.getPurpose());
        if(commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)){
            if(commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                textSenderName.setText(commonFunctions.getFullName());
            }else{
                textSenderName.setText(commonFunctions.getBusinessName());
            }
        }else if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)){
            textSenderName.setText(commonFunctions.getFullName());
        }else if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            textSenderName.setText(commonFunctions.getBusinessName());
        }
        textRemarks.setText(data.getRemarks());
        textSendAmount.setText(commonFunctions.setAmount(data.getAmount())+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        textFees.setText(commonFunctions.setAmount(data.getCommission_amount())+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        textTotalAmount.setText(commonFunctions.setAmount(data.getTotal_transaction_amount())+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);

        if(data.getAgentDetails().getProfile_image()!=null&&!data.getAgentDetails().getProfile_image().equals("")){
            rlIvImage.setVisibility(View.VISIBLE);
            rlTvImage.setVisibility(View.GONE);
            Glide.with(context)
                    .load(data.getProfile_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(imgProfileImage);
        }else {
            rlTvImage.setVisibility(View.VISIBLE);
            String firstLetter = String.valueOf(transactionDetailsData.getReceiver_name().charAt(0));
            tvImageLetter.setText(firstLetter);
            rlIvImage.setVisibility(View.GONE);
        }


        if (data.getPurpose() == null || data.getPurpose().isEmpty()) {
            textPurposeLabel.setVisibility(View.GONE);
            textPurpose.setVisibility(View.GONE);
        } else {
            textPurposeLabel.setVisibility(View.VISIBLE);
        }

        if (data.getRemarks() == null || data.getRemarks().isEmpty()) {
            textRemarksLabel.setVisibility(View.GONE);
            textRemarks.setVisibility(View.GONE);
        } else {
            textRemarksLabel.setVisibility(View.VISIBLE);
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
                myBitmapImage = Bitmap.createBitmap(mainLayout.getWidth(), mainLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                mainLayout.draw(canvas);
                return "";
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            if (myBitmapImage != null) {
                commonFunctions.logAValue("Bitmap status", "bitmap  is present");
                shareImage();
                dialogProgress.dismiss();
            } else {
                commonFunctions.logAValue("Bitmap status", "bitmap is empty");
                dialogProgress.dismiss();

            }
        }
    }

    public void shareImage() {
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmapImage, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra("subject", "Transaction Details");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Transaction Details");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "text message"));
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });
}