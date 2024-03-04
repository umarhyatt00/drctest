package com.yeel.drc.activity.main.settings.sequrity;

import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.timeout.BaseActivity;

public class SecuritySettingsActivity extends BaseActivity {
   Context context;
   LinearLayout llResetPin;

    private Switch mSwitchFaceID;
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequrity_settings);
        setToolBar();
        context= SecuritySettingsActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llResetPin.setOnClickListener(view -> {
            Intent in=new Intent(context, ResetPINActivity.class);
            startActivity(in);
        });
        mSwitchFaceID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                    startActivityForResult(intent, 200);
                }else{
                    Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                    startActivityForResult(intent, 201);
                }
            }
        });
    }

    private void initView() {
         llResetPin=findViewById(R.id.ll_reset_pin);
        mSwitchFaceID = findViewById(R.id.switchFaceID);
        if(commonFunctions.isSequrityEnabled()){
            mSwitchFaceID.setChecked(true);
        }else{
            mSwitchFaceID.setChecked(false);
        }
    }

    //method to authenticate app
    private void authenticateApp() {
        commonFunctions.setSecurityShownStatus(true);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {

                    //If app is unable to find any Security settings then user has to set screen lock manually
                    //  textView.setText(getResources().getString(R.string.setting_label));
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                authenticateApp();
            }else if(requestCode == 201){
                commonFunctions.setSequrityEnabled(false);
            }else  if(requestCode==LOCK_REQUEST_CODE){
                if (resultCode == RESULT_OK) {
                    commonFunctions.setSequrityEnabled(true);
                }
            }else if(requestCode==SECURITY_SETTING_REQUEST_CODE){
                if (isDeviceSecure()) {
                    authenticateApp();
                } else {

                }
            }
        }
    }


    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.security_settings);
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}