package com.yeel.drc.activity.main.receipt.credit;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.PermissionUtils;
import java.util.Objects;

public class AgentSendViaYeelCreditReceipt extends BaseActivity {
    Context context;
    String ydbRefId;
    String notificationId;
    PermissionUtils permissionUtils;
    DialogProgress dialogProgress;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    Bitmap myBitmapImage = null;
    TransactionDetailsData transactionDetailsData;


    boolean isApiCalledRetry=false;


    TextView txtSenderName,txtSenderMobile,txtDate,txtSenderDetail,txtAgentDetail,txtTransactionId,txtRemarks,txtReceivedAmount;
    ConstraintLayout llShare;
    ProgressBar progressBar;
    ImageView senderProfileImage;
    TextView senderProfileLetter;
    Group grpRemarks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_send_via_yeel_credit_receipt);
        context= AgentSendViaYeelCreditReceipt.this;
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

        senderProfileImage=findViewById(R.id.iv_profile_image);
        senderProfileLetter=findViewById(R.id.tvFirstLetter);
        llShare=findViewById(R.id.screen_shot_layout);
        progressBar=findViewById(R.id.progressBar);
        txtSenderName = findViewById(R.id.tvSenderName);
        txtSenderMobile = findViewById(R.id.tvSenderMobile);
        txtDate = findViewById(R.id.text_date);
        txtSenderDetail = findViewById(R.id.textSenderDetails);
        txtAgentDetail = findViewById(R.id.textAgentDetail);
        txtTransactionId = findViewById(R.id.textTransactionId);
        txtRemarks = findViewById(R.id.text_remarks);
        txtReceivedAmount = findViewById(R.id.tvRecievedAmount);
        grpRemarks = findViewById(R.id.groupRemarks);

        llShare.setVisibility(View.GONE);
        getTransactionDetails();
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
//                    progressBar.setVisibility(View.GONE);
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
//                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void retry() {
                isApiCalledRetry=true;
                getTransactionDetails();
            }
        });
    }

    private void setValues(){
        llShare.setVisibility(View.VISIBLE);


        String profileImage = transactionDetailsData.getProfile_image();
        if (profileImage != null && !profileImage.equals("")) {
            Glide.with(context)
                    .load(profileImage)
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(senderProfileImage);
        } else {
            String firstLetter = String.valueOf(transactionDetailsData.getRemitterDetails().getFirstname().charAt(0));
            senderProfileLetter.setText(firstLetter);
        }

        txtSenderName.setText(commonFunctions.generateFullName(transactionDetailsData.getRemitterDetails().getFirstname(),transactionDetailsData.getRemitterDetails().getMiddleName(),transactionDetailsData.getRemitterDetails().getLastname()));


        String senderMobileNumber = "";
        if(transactionDetailsData.getRemitterDetails().getMobile()!=null&&!transactionDetailsData.getRemitterDetails().getMobile().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getRemitterDetails().getCountry_code());
            txtSenderMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getRemitterDetails().getMobile(), transactionDetailsData.getRemitterDetails().getCountry_code(), mobileNumberFormat));
            senderMobileNumber= commonFunctions.formatAMobileNumber(transactionDetailsData.getRemitterDetails().getMobile(), transactionDetailsData.getRemitterDetails().getCountry_code(), mobileNumberFormat);
        }else{
            txtSenderMobile.setText("Not available");
        }



        txtDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(),"time"));


        AgentData agentData=transactionDetailsData.getAgentDetails();
        if (agentData != null) {
            String fullAgentAddress=commonFunctions.getAgentNameWithFullAddress(agentData);
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),agentMobileNumberFormat);
            txtAgentDetail.setText(fullAgentAddress+", "+agentMobileNumber);
        } else {
            txtAgentDetail.setText("Agent Details not available");
        }



        txtTransactionId.setText(transactionDetailsData.getYdb_ref_id());


        if(transactionDetailsData.getRemarks()==null || transactionDetailsData.getRemarks().isEmpty()){
            grpRemarks.setVisibility(View.GONE);
        }else {
            grpRemarks.setVisibility(View.VISIBLE);
            txtRemarks.setText(transactionDetailsData.getRemarks());
        }

        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
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