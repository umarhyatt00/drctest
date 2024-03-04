package com.yeel.drc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.login.ThudakkamActivity;
import com.yeel.drc.activity.signup.MobileNumberEnterActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.forceupdate.ForceUpdateData;
import com.yeel.drc.api.forceupdate.ForceUpdateResponse;
import com.yeel.drc.dialogboxes.ForceUpdateDialog;
import com.yeel.drc.dialogboxes.ForceUpdateNotMandatoryDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.AppSignatureHelper;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;

public class WelcomeActivity extends BaseActivity {
    Context context;
    TextView tvOpenFreeAccount;
    TextView tvLogin;
    TextView tvHeading;
    APICall apiCallTwo;
    SomethingWentWrongDialog somethingWentWrongDialogTwo;
    boolean forceUpdateAPIRetry = false;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = WelcomeActivity.this;
        initView();
        setItemListeners();
        checkPermission();
    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        somethingWentWrongDialogTwo = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCallTwo = new APICall(context, SthiramValues.finish);
        commonFunctions.setPage("");
        tvOpenFreeAccount = findViewById(R.id.tv_open_free_account);
        tvLogin = findViewById(R.id.tv_login);
        tvHeading = findViewById(R.id.tv_heading);
        callForceUpdateAPI();
    }

    private void setItemListeners() {
        tvOpenFreeAccount.setOnClickListener(view -> {
            Intent in = new Intent(WelcomeActivity.this, MobileNumberEnterActivity.class);
            startActivity(in);
        });
        tvLogin.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, ThudakkamActivity.class);
            startActivity(intent);
        });
        tvHeading.setOnClickListener(view -> {
            String signature = "Android SMS Key:  " + getSignature();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Android SMS Key ");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, signature);
            startActivity(Intent.createChooser(sharingIntent, "share"));
        });
    }



    private String getSignature() {
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(context);
        ArrayList<String> signatureList = appSignatureHelper.getAppSignatures();
        return signatureList.get(0);
    }

    private void callForceUpdateAPI() {
        apiCallTwo.forceUpdate(forceUpdateAPIRetry, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCallTwo.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ForceUpdateResponse apiResponse = gson.fromJson(jsonString, ForceUpdateResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        ForceUpdateData forceUpdateData = apiResponse.getData();
                        assert pInfo != null;
                        boolean updateAvailable = commonFunctions.checkUpdateAvailable(forceUpdateData, pInfo.versionCode);
                        if (updateAvailable) {
                            if (forceUpdateData.getForceUpdate().equalsIgnoreCase("y")) {
                                ForceUpdateDialog forceUpdateDialog = new ForceUpdateDialog(context, SthiramValues.finish);
                                forceUpdateDialog.show(getString(R.string.force_update));
                            } else {
                                ForceUpdateNotMandatoryDialog forceUpdateNotMandatoryDialog = new ForceUpdateNotMandatoryDialog(context);
                                forceUpdateNotMandatoryDialog.show();
                            }
                        }

                    } else {
                        if (!somethingWentWrongDialogTwo.isShowing()) {
                            somethingWentWrongDialogTwo.show();
                        }
                    }
                } catch (Exception e) {
                    commonFunctions.logAValue("Error", e + "");
                    if (!somethingWentWrongDialogTwo.isShowing()) {
                        somethingWentWrongDialogTwo.show();
                    }
                }
            }
            @Override
            public void retry() {
                forceUpdateAPIRetry = true;
                callForceUpdateAPI();
            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionUtils permissionUtils = new PermissionUtils(WelcomeActivity.this);
            if (permissionUtils.checkPushNotificationPermission(requestPermissionPushNotification)) {
                getFCMToken();
            }
        } else {
            getFCMToken();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionPushNotification = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getFCMToken();
        }
    });

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                commonFunctions.logAValue("Error", "Fetching FCM registration token failed"+ task.getException());
                return;
            }
            String token = task.getResult();
            commonFunctions.logAValue("Token", token);
            commonFunctions.setFCMToken(token);
        });
    }



}