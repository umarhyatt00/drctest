package com.yeel.drc.activity.main.receipt.debit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
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
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.PermissionUtils;
import java.util.Objects;

public class AgentCashPickupDebitActivity extends BaseActivity {
    Context context;
    String ydbRefId;
    String notificationId;
    PermissionUtils permissionUtils;
    DialogProgress dialogProgress;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    Bitmap myBitmapImage = null;
    LinearLayout llShare;

    TextView tvReceiverName;
    TextView tvReceiverMobile;
    TextView tvTransactionDate;
    TextView textSenderDetails;
    TextView textAgentDetail;
    TextView textTransactionId;
    TextView tvDeliveryMethod;
    Group grpRemarks,grpPurpose;
    TextView txtRemarks,textPurpose;
    TextView   txtSendAmount,txtFees,txtTotalamount,txtReceivedAmount,senderProfileLetter;
    ProgressBar progressBar;
    NestedScrollView nestedScrollView;


    boolean isApiCalledRetry=false;
    TransactionDetailsData transactionDetailsData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_cash_pickup_debit);
        context= AgentCashPickupDebitActivity.this;
        ydbRefId=getIntent().getStringExtra("ydb_ref_id");
        notificationId=getIntent().getStringExtra("notificationId");
        if(notificationId==null){
            notificationId="";
        }
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {

    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        dialogProgress=new DialogProgress(context);

        tvReceiverName=findViewById(R.id.tvReceiverName);
        tvReceiverMobile=findViewById(R.id.tvReceiverMobile);
        tvTransactionDate=findViewById(R.id.text_date);
        textSenderDetails=findViewById(R.id.textSenderDetails);
        textAgentDetail=findViewById(R.id.textAgentDetail);
        textTransactionId=findViewById(R.id.textTransactionId);
        tvDeliveryMethod=findViewById(R.id.text_delivery_method);
        grpRemarks = findViewById(R.id.groupRemarks);
        txtRemarks = findViewById(R.id.text_remarks);

        txtSendAmount = findViewById(R.id.tvSendAmount);
        txtFees = findViewById(R.id.tvFees);
        txtTotalamount = findViewById(R.id.tvTotalToPay);
        txtReceivedAmount = findViewById(R.id.tvRecieverGets);
        senderProfileLetter=findViewById(R.id.tvFirstLetter);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        textPurpose=findViewById(R.id.text_purpose);
        grpPurpose=findViewById(R.id.groupPurpose);
        nestedScrollView=findViewById(R.id.main_layout);
        nestedScrollView.setVisibility(View.GONE);

        getTransactionDetails();
    }

    private void getTransactionDetails() {
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
        progressBar.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);
        BeneficiaryData beneficiaryData=transactionDetailsData.getBeneficiaryDetails();
        BeneficiaryData remitterData=transactionDetailsData.getRemitterDetails();
        //name
          String fullName=commonFunctions.generateFullName(beneficiaryData.getFirstname(),beneficiaryData.getMiddleName(),beneficiaryData.getLastname());
          tvReceiverName.setText(fullName);


        String firstLetter = String.valueOf(fullName.charAt(0));
        senderProfileLetter.setText(firstLetter);

          //reciver mobile
        String receiverMobileNumber = "";
        if(beneficiaryData.getMobile()!=null&&!beneficiaryData.getMobile().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), beneficiaryData.getCountry_code());
            receiverMobileNumber= commonFunctions.formatAMobileNumber(beneficiaryData.getMobile(), beneficiaryData.getCountry_code(), mobileNumberFormat);
            tvReceiverMobile.setText(receiverMobileNumber);
        }else{
            tvReceiverMobile.setText("Not available");
        }

        //date
        tvTransactionDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(),"time"));


        String senderMobileNumber = "";
        if(remitterData.getMobile()!=null&&!remitterData.getMobile().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), remitterData.getCountry_code());
            senderMobileNumber= commonFunctions.formatAMobileNumber(remitterData.getMobile(), remitterData.getCountry_code(), mobileNumberFormat);
        }else{
            tvReceiverMobile.setText("Not available");
        }
        //get full address from a beneficiary object
        textSenderDetails.setText(commonFunctions.getBeneficiaryFullAddress(remitterData,senderMobileNumber));


        if (transactionDetailsData.getReceiver_agent_detail() != null) {
            String fullAgentAddress=commonFunctions.getAgentNameWithFullAddress(transactionDetailsData.getReceiver_agent_detail());
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),transactionDetailsData.getReceiver_agent_detail().getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(transactionDetailsData.getReceiver_agent_detail().getMobile(),transactionDetailsData.getReceiver_agent_detail().getCountry_code(),agentMobileNumberFormat);
            textAgentDetail.setText(fullAgentAddress+", "+agentMobileNumber);
        } else {
            textAgentDetail.setText("Agent Details not available");
        }


        textTransactionId.setText(transactionDetailsData.getYdb_ref_id());

        if(transactionDetailsData.getRemarks()!=null&&! transactionDetailsData.getRemarks().equals("")&& !transactionDetailsData.getRemarks().equals("null")){
            grpRemarks.setVisibility(View.VISIBLE);
            txtRemarks.setText(transactionDetailsData.getRemarks());
        }else {
            grpRemarks.setVisibility(View.GONE);
        }

        //purpose
        if(transactionDetailsData.getPurpose()!=null&&! transactionDetailsData.getPurpose().equals("")&& !transactionDetailsData.getPurpose().equals("null")){
            grpPurpose.setVisibility(View.VISIBLE);
            textPurpose.setText(transactionDetailsData.getPurpose());
        }else {
            grpPurpose.setVisibility(View.GONE);
        }

        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
        txtSendAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount())+" "+sign);
        txtFees.setText(commonFunctions.setAmount(transactionDetailsData.getCommission_amount())+" "+sign);
        txtTotalamount.setText(commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount())+" "+sign);
        txtReceivedAmount.setText(commonFunctions.setAmount(transactionDetailsData.getReceiver_gets_amt())+" "+sign);



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
                if(permissionUtils.checkGalleryPermissionShare(requestPermissionLauncherGallery)){
                    new bitmapAsyncNew().execute();
                }
                return true;

        }
        return false;
    }

    private ActivityResultLauncher<String> requestPermissionLauncherGallery =
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
                myBitmapImage = Bitmap.createBitmap(llShare.getWidth(), llShare.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                llShare.draw(canvas);
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
}