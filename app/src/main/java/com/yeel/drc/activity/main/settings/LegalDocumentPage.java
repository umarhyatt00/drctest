package com.yeel.drc.activity.main.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.activity.common.WebViewActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class LegalDocumentPage extends BaseActivity {
    private LinearLayout tvConsentAgreement;
    private LinearLayout tvPrivacyPolicy;
    private LinearLayout tvSustentaAgreement;
    LinearLayout llDeactivateAccount;
    private LegalDocumentPage context;

    APICall apiCall;
    Boolean deactivateRetry =false;
    ErrorDialog errorDialog;
    ErrorDialog errorDialogTwo;
    SomethingWentWrongDialog somethingWentWrongDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_document_page);
        context = LegalDocumentPage.this;
        initViews();
        setToolBar();
    }

    private void initViews() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        errorDialogTwo=new ErrorDialog(context, SthiramValues.loginScreen);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);

        tvConsentAgreement = findViewById(R.id.tvConsentAgreement);
        tvPrivacyPolicy =  findViewById(R.id.tvPrivacyPolicy);
        tvSustentaAgreement =  findViewById(R.id.tvSustentaAgreement);
        llDeactivateAccount=findViewById(R.id.llDeactivateAccount);

        tvConsentAgreement.setOnClickListener(view -> {
            openWebView("t_c");
        });
        tvPrivacyPolicy.setOnClickListener(view -> {
            openWebView("privacy_policy");
        });
        llDeactivateAccount.setOnClickListener(view -> confirmationDialogue());
    }

    private void deactivateAccount() {
        apiCall.closeAccount(deactivateRetry,true, commonFunctions.getUserId(), new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        errorDialogTwo.show(apiResponse.getMessage());
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    somethingWentWrongDialog.show();                }
            }

            @Override
            public void retry() {
                deactivateRetry=true;
                deactivateAccount();
            }
        });
    }


    private void openWebView(String type) {
        Intent in=new Intent(context, WebViewActivity.class);
        in.putExtra("type",type);
        startActivity(in);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.legal);
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

    public void confirmationDialogue() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_style_clear_sign_up_progress);
        TextView tvMessage = dialog.findViewById(R.id.tv_title);
        tvMessage.setText(R.string.delete_account_confirmation_message);
        TextView tvClose = dialog.findViewById(R.id.tv_cancel);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvClose.setOnClickListener(v -> dialog.dismiss());
        tvOk.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
            startActivityForResult(intent, 200);

        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                deactivateAccount();
            }
        }
    }

}