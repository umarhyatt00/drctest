package com.yeel.drc.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.scottyab.rootbeer.RootBeer;
import com.yeel.drc.R;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.AppSignatureHelper;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.LocaleUtils;
import com.yeel.drc.utils.SharedPrefUtil;

import java.util.ArrayList;

public class MinnalActivity extends BaseActivity {
    Context context;
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = MinnalActivity.this;
        sharedPrefUtil = new SharedPrefUtil(context);
        commonFunctions.setLogoutStatus(true);
        commonFunctions.loginAndLogoutStatus();
        initView();
    }

    private void initView() {
        String signature = getSignature();
        commonFunctions.logAValue("signature", signature);
        RootBeer rootBeer = new RootBeer(MinnalActivity.this);
       /* if (!rootBeer.isRooted() && !isEmulator()) {*/
            callIntent();
      /*  } else {
            RootedDeviceDialog rootedDeviceDialog = new RootedDeviceDialog(context, SthiramValues.finish);
            rootedDeviceDialog.show();
        }*/
        if (sharedPrefUtil.getEncryptedStringPreference("LANGUAGE", "en").equals("en")) {
            setLocale("en");
            SthiramValues.SELECTED_LANGUAGE_ID = "1";
            SthiramValues.SELECTED_LANGUAGE = "en";
            SthiramValues.SELECTED_LANGUAGE_NAME = "English";
        }else {
            setLocale("fr");
            SthiramValues.SELECTED_LANGUAGE_ID = "2";
            SthiramValues.SELECTED_LANGUAGE = "fr";
            SthiramValues.SELECTED_LANGUAGE_NAME = "French";
        }
    }
    private void callIntent() {
        int secondsDelayed = 1;
        new Handler().postDelayed(() -> {
            if (commonFunctions.isSequrityEnabled()) {
                commonFunctions.setSecurityShownStatus(false);
            }
            commonFunctions.redirectionFromSplash();
        }, secondsDelayed * 1000);
    }

    private boolean isEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");
    }

    private void setLocale(String lang) {
        commonFunctions.logAValue("Language", lang);
        LocaleUtils.initialize(MinnalActivity.this, lang);
    }

    private String getSignature() {
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(context);
        ArrayList<String> signatureList = appSignatureHelper.getAppSignatures();
        return signatureList.get(0);
    }
}