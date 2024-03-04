package com.yeel.drc.activity.main.receipt.debit;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.billpayments.internal.EducationInternalBillPaymentsActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.billpayments.BillPaymentsData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.PermissionUtils;

import java.util.Objects;

public class EducationDebitDetailsActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    private DialogProgress dialogProgress;
    Bitmap myBitmapImage = null;
    LinearLayout llShare;
    PermissionUtils permissionUtils;
    TextView tvPayAgain;
    ProgressBar progressBar;

    boolean getTransactionDetailsRetry=false;
    String ydbRefId="";
    String notificationId="";
    TransactionDetailsData transactionDetailsData;

    TextView tvUniversityName;
    TextView tvUniversityMobile;
    ImageView universityImage;
    TextView tvTransactionDate;
    TextView tvStudentName;
    TextView tvStudentID;
    TextView tvStudentMobile;
    TextView tvTransactionId;
    TextView textSendAmount;
    TextView textFees;
    TextView tvTotalPaid;
    TextView tvRemarks;
    LinearLayout llRemark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_debit_details);
        context=EducationDebitDetailsActivity.this;
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
        tvPayAgain.setOnClickListener(view -> {
            BillPaymentsData billPaymentsData=new BillPaymentsData();
            String receiverAccountNumber=commonFunctions.getReceiverAccountNumberFormCurrencyList(transactionDetailsData.getProviderCurrencyList());
            if(receiverAccountNumber!=null&&!receiverAccountNumber.equals("")){
                transactionDetailsData.getProviderData().setAccountNumber(receiverAccountNumber);
                billPaymentsData.setProvidersListData(transactionDetailsData.getProviderData());
                billPaymentsData.setStudentName(transactionDetailsData.getBillPaymentDetails().getName());
                billPaymentsData.setStudentId(transactionDetailsData.getBillPaymentDetails().getId());
                billPaymentsData.setStudentMobileNumber(transactionDetailsData.getBillPaymentDetails().getMobile());
                billPaymentsData.setStudentMobileCountyCode(transactionDetailsData.getBillPaymentDetails().getCountyCode());
                Intent in = new Intent(context, EducationInternalBillPaymentsActivity.class);
                in.putExtra("data", billPaymentsData);
                in.putExtra("from", "normal");
                startActivity(in);
            }else{
                String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                errorDialog.show(message);
            }
        });
    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        dialogProgress=new DialogProgress(context);
        tvPayAgain=findViewById(R.id.btnPayAgain);
        progressBar=findViewById(R.id.progressBar);

        llShare=findViewById(R.id.llShare);
        llShare.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        tvUniversityName=findViewById(R.id.tvUniversityName);
        tvUniversityMobile=findViewById(R.id.tvUniversityMobile);
        universityImage=findViewById(R.id.universityImage);
        tvTransactionDate=findViewById(R.id.tvTransactionDate);
        tvStudentName=findViewById(R.id.tvStudentName);
        tvStudentID=findViewById(R.id.tvStudentID);
        tvStudentMobile=findViewById(R.id.tvStudentMobile);
        tvTransactionId=findViewById(R.id.tvTransactionId);

        textSendAmount=findViewById(R.id.textSendAmount);
        textFees=findViewById(R.id.textFees);
        tvTotalPaid=findViewById(R.id.tvTotalPaid);
        tvRemarks=findViewById(R.id.tvRemarks);
        llRemark=findViewById(R.id.ll_remark);

        getTransactionDetails();
    }

    private void getTransactionDetails() {
        apiCall.getTransactionDetails(getTransactionDetailsRetry,commonFunctions.getUserId(),ydbRefId,notificationId,false, new CustomCallback() {
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
                getTransactionDetailsRetry=true;
                getTransactionDetails();
            }
        });
    }

    private void setValues() {
        llShare.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        //set image
        Glide.with(universityImage.getContext())
                .load(SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL+""+transactionDetailsData.getProviderData().getImage())
                .apply(new RequestOptions().circleCrop())
                .into(universityImage);


        tvUniversityName.setText(transactionDetailsData.getReceiver_name());

        //set mobile number
        if(transactionDetailsData.getMobile()!=null&&!transactionDetailsData.getMobile().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getCountryCode());
            tvUniversityMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getMobile(), transactionDetailsData.getCountryCode(), mobileNumberFormat));
        }else{
            tvUniversityMobile.setText("Not available");
        }


        //time
        tvTransactionDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(),"time"));

        //name
        tvStudentName.setText(transactionDetailsData.getBillPaymentDetails().getName());
        tvStudentID.setText(transactionDetailsData.getBillPaymentDetails().getId());

        //set student mobile number
        if(transactionDetailsData.getBillPaymentDetails().getCountyCode()!=null&&!transactionDetailsData.getBillPaymentDetails().getCountyCode().equals("")) {
            String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getBillPaymentDetails().getCountyCode());
            tvStudentMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getBillPaymentDetails().getMobile(), transactionDetailsData.getBillPaymentDetails().getCountyCode(), mobileNumberFormat));
        }else{
            tvStudentMobile.setText("Not available");
        }

        //transction id
        tvTransactionId.setText(transactionDetailsData.getYdb_ref_id());

        //send amount
        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
        textSendAmount.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getAmount()));
        textFees.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getCommission_amount()));
        tvTotalPaid.setText(sign+" "+commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()));

        if(transactionDetailsData.getRemarks()!=null&&!transactionDetailsData.getRemarks().equals("")&&!transactionDetailsData.getRemarks().equals("null")){
            llRemark.setVisibility(View.VISIBLE);
            tvRemarks.setText(transactionDetailsData.getRemarks());
        }else{
            llRemark.setVisibility(View.GONE);
        }






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