package com.yeel.drc.activity.main.receipt.debit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.Objects;

public class AgentMobilePayDebitReceiptActivity extends BaseActivity {
    Context context;
    DialogProgress dialogProgress;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    String ydbRefId;
    String notificationId;
    Bitmap myBitmapImage=null;
    boolean isApiCalledRetry=false;
    TransactionDetailsData transactionDetailsData;
    ConstraintLayout screenShotLayout;
    TextView textReceiverName,textReceiverMobile,tvFirstLetter,txtSenderDetail,txtAgentDetail;
    TextView textSendDate,textTransactionId,textPurpose,textExchangeRate,textSendAmount,textFees;
    TextView textSendAgain;
    TextView tvReceiverGetsAmount;
    TextView tvTotalPaid;
    TextView tvTransactionType;
    TextView tvSefireId;
    ProgressBar progressBar;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_mobile_pay_debit_receipt);
        context = AgentMobilePayDebitReceiptActivity.this;
        dialogProgress = new DialogProgress(context);
        ydbRefId=getIntent().getStringExtra("ydb_ref_id");
        notificationId=getIntent().getStringExtra("notificationId");
        if(notificationId==null){
            notificationId="";
        }
        setToolBar();
        initView();
    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        dialogProgress=new DialogProgress(context);
        screenShotLayout = findViewById(R.id.screen_shot_layout);
        progressBar=findViewById(R.id.progressBar);
        textReceiverName = findViewById(R.id.tvReceiverName);
        textReceiverMobile = findViewById(R.id.tvReceiverMobile);
        tvFirstLetter=findViewById(R.id.tv_first_letter);
        txtSenderDetail = findViewById(R.id.textSenderDetails);
        txtAgentDetail = findViewById(R.id.textAgentDetail);
        textSendDate = findViewById(R.id.tv_date);
        textTransactionId = findViewById(R.id.tv_transaction_id);
        textPurpose = findViewById(R.id.tv_purpose);
        textExchangeRate = findViewById(R.id.tv_exchange_rate);
        textSendAmount = findViewById(R.id.tv_send_amount);
        textFees = findViewById(R.id.tv_fees);
        tvTransactionType = findViewById(R.id.tv_delivery_method);
        tvSefireId=findViewById(R.id.tv_seafire_id);
        tvTotalPaid = findViewById(R.id.tv_total_paid);
        tvReceiverGetsAmount = findViewById(R.id.tv_receiver_gets_amount);
        screenShotLayout.setVisibility(View.GONE);
        textSendAgain = findViewById(R.id.tvSendAgain);
        getTransactionDetails();

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }



    private void getTransactionDetails() {
        progressBar.setVisibility(View.VISIBLE);
        apiCall.getTransactionDetails(isApiCalledRetry,commonFunctions.getUserId(),ydbRefId,notificationId,false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    TransactionDetailsResponse apiResponse=gson.fromJson(jsonString, TransactionDetailsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        transactionDetailsData=apiResponse.getTransactionDetailsData();
                        setValues();
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
                isApiCalledRetry=true;
                getTransactionDetails();
            }
        });
    }
    private void setValues() {
        screenShotLayout.setVisibility(View.VISIBLE);
        textReceiverName.setText(transactionDetailsData.getReceiver_name());
        if(transactionDetailsData.getmMoneyReceiverCountryCode()!=null&&!transactionDetailsData.getmMoneyReceiverCountryCode().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getmMoneyReceiverCountryCode());
            textReceiverMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getmMoneyReceiverMobile(), transactionDetailsData.getmMoneyReceiverCountryCode(), mobileNumberFormat));
        }else{
            textReceiverMobile.setText("Not available");
        }
        String senderMobileNumber ="";
        if(transactionDetailsData.getRemitterDetails().getMobile()!=null&&!transactionDetailsData.getRemitterDetails().getMobile().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getRemitterDetails().getCountry_code());
            senderMobileNumber= commonFunctions.formatAMobileNumber(transactionDetailsData.getRemitterDetails().getMobile(), transactionDetailsData.getRemitterDetails().getCountry_code(), mobileNumberFormat);
        }
        String senderEmail ="";
        if(transactionDetailsData.getRemitterDetails().getEmail()==null|| transactionDetailsData.getRemitterDetails().getEmail().isEmpty()){
            senderEmail="";
        }else {
            senderEmail = ", "+transactionDetailsData.getRemitterDetails().getEmail();
        }
        txtSenderDetail.setText(commonFunctions.generateFullName(transactionDetailsData.getRemitterDetails().getFirstname(),transactionDetailsData.getRemitterDetails().getMiddleName(),transactionDetailsData.getRemitterDetails().getLastname())+", "+senderMobileNumber+senderEmail);
        //set agent details
        AgentData agentData=transactionDetailsData.getAgentDetails();
        if (agentData!= null) {
            String fullAgentAddress=commonFunctions.getAgentNameWithFullAddress(agentData);
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),agentMobileNumberFormat);
            txtAgentDetail.setText(fullAgentAddress+", "+agentMobileNumber);
        } else {
            txtAgentDetail.setText("Agent Details not available");
        }
        //show first letter
        String firstLetter=String.valueOf(transactionDetailsData.getReceiver_name().charAt(0));
        tvFirstLetter.setText(firstLetter);
        String receiverCurrency="";
        if(transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA_AGENT)){
            receiverCurrency= SthiramValues.UGANDA_CURRENCY_CODE;
        }else if(transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_MPESA_AGENT)){
            receiverCurrency= SthiramValues.KENYA_CURRENCY_CODE;
        }
        //time
        textSendDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(),"time"));
        //transction id
        textTransactionId.setText(transactionDetailsData.getYdb_ref_id());
        //rate
        textExchangeRate.setText("1 USD = "+transactionDetailsData.getExchange_rate()+" "+receiverCurrency);
        //send amount
        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
        textSendAmount.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getAmount()));
        textFees.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getCommission_amount()));
        tvTotalPaid.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()));
        tvReceiverGetsAmount.setText(receiverCurrency+" "+commonFunctions.setAmount(transactionDetailsData.getReceiver_gets_amt()));
        //remark
        tvTransactionType.setText(transactionDetailsData.getTransaction_type());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_share_gray, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_share:
                if(permissionUtils.checkGalleryPermissionShare(requestPermissionShare)){
                    new BitmapAsyncNew().execute();
                }
                return true;

        }
        return false;
    }

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
    public  void shareImage(){
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmapImage, "TRANSACTION"+System.currentTimeMillis(), null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra("subject","Transaction Details");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Transaction Details");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "text message"));
    }
    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new BitmapAsyncNew().execute();
                }
            });
}