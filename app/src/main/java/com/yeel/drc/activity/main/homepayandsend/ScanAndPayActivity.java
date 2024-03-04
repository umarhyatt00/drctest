package com.yeel.drc.activity.main.homepayandsend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yeel.drc.R;
import com.yeel.drc.timeout.BaseActivity;

public class ScanAndPayActivity extends BaseActivity {
    private String mQRCodeData="";
    Activity screenShotActivity;
    public  static String QRCodeNumber="", from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sacn_and_pay);
        screenShotActivity=this;
        from=getIntent().getStringExtra("from");
       /* if(from.equals("pay")){*/
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setOrientationLocked(false);
            integrator.setCaptureActivity(SmallCaptureActivity.class);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
       /* }else {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setOrientationLocked(false);
            integrator.setCaptureActivity(ScanCaptureActivity.class);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }*/


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result.getContents() == null) {
            if (QRCodeNumber.equals("")) {
                Intent in = getIntent();
                in.putExtra("receiver_qr_code", "");
                setResult(Activity.RESULT_CANCELED, in);
                finish();
            } else {
                Intent in = getIntent();
                in.putExtra("receiver_qr_code", QRCodeNumber);
                setResult(Activity.RESULT_OK, in);
                finish();
            }
        } else {
            mQRCodeData = result.getContents();
            Intent in = getIntent();
            in.putExtra("receiver_qr_code", mQRCodeData);
            setResult(Activity.RESULT_OK, in);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent in = getIntent();
        in.putExtra("receiver_qr_code", "");
        setResult(Activity.RESULT_CANCELED, in);
        finish();
    }
}