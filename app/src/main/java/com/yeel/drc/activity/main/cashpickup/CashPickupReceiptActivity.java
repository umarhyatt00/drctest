package com.yeel.drc.activity.main.cashpickup;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.cashpickup.CashPickupData;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.Objects;

public class CashPickupReceiptActivity extends BaseActivity {
    Context context;
    DialogProgress dialogProgress;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    RelativeLayout rlMain;
    CashPickupData cashPickupData;
    TextView tvAmount;
    TextView tvReceiverName;
    TextView tvTransactionId;
    LinearLayout llDone;
    Bitmap myBitmapImage = null;
    PermissionUtils permissionUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_reciept);
        context = CashPickupReceiptActivity.this;
        cashPickupData=(CashPickupData) getIntent().getSerializableExtra("data");
        setToolBar();
        initViews();
        setOnClickListener();
    }

    private void setOnClickListener() {
        llDone.setOnClickListener(view -> commonFunctions.callHomeIntent());
    }

    private void initViews() {
        permissionUtils=new PermissionUtils(context);
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        rlMain = findViewById(R.id.rl_main);
        tvAmount=findViewById(R.id.tv_amount);
        tvReceiverName=findViewById(R.id.tv_receiver_name);
        tvTransactionId=findViewById(R.id.tv_transactionId);
        llDone=findViewById(R.id.ll_done);
        setValues();
    }

    private void setValues() {
        String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
        BeneficiaryCommonData cashPickupReceiverData=cashPickupData.getCashPickupReceiverData();
        tvAmount.setText(commonFunctions.setAmount(cashPickupData.getAmount())+" "+sign);
        String fullName=commonFunctions.generateFullName(cashPickupReceiverData.getFirstName(),cashPickupReceiverData.getMiddleName(),cashPickupReceiverData.getLastName());
        tvReceiverName.setText(fullName);
        tvTransactionId.setText(cashPickupData.getReferenceNumber());
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