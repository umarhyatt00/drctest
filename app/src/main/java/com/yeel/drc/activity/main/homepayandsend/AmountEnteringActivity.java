package com.yeel.drc.activity.main.homepayandsend;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

public class AmountEnteringActivity extends BaseActivity {
    Context context;
    EditText edtAmount;
    ImageView imgContinue;
    UserDetailsData userDetailsData;
    TextView tvName;
    TextView tvFirstLetter;
    ImageView ivLogo;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    EditText edtRemark;
    TextView tvCurrencySymbol;
    String amountToSend;
    String remark;
    Boolean paymentRetry = false;
    String receiverName;
    String from;
    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount_entering);
        setToolBar();
        context = AmountEnteringActivity.this;
        userDetailsData = (UserDetailsData) getIntent().getSerializableExtra("data");
        from = getIntent().getStringExtra("from");
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        imgContinue.setOnClickListener(view -> {
            remark = edtRemark.getText().toString().trim();
            if (commonFunctions.validateRemark(remark)) {
                String type;
                if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                    type = SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT;
                } else {
                    type = SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT;
                }
                String message = commonFunctions.checkTransactionIsPossible(edtAmount.getText().toString().trim(), edtAmount.getText().toString().trim(), type);
                if (message.equals(SthiramValues.SUCCESS)) {
                    double doubleDebit = Double.parseDouble(edtAmount.getText().toString().trim());
                    amountToSend = doubleDebit + "";
                    if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                        userDetailsData.setAmount(amountToSend);
                        userDetailsData.setRemark(remark);
                        userDetailsData.setReceiverName(receiverName);
                        Intent in = new Intent(context, SendViaYeelConfirmationActivity.class);
                        in.putExtra("data", userDetailsData);
                        startActivity(in);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                        startActivityForResult(intent, 200);
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.enter_valid_remark), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initView() {
        commonFunctions = new CommonFunctions(context);
        tvCurrencySymbol = findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall = new APICall(context, SthiramValues.home);
        edtAmount = findViewById(R.id.edt_amount);
        edtAmount.requestFocus();
        imgContinue = findViewById(R.id.imgContinue);
        tvName = findViewById(R.id.tv_name);
        tvFirstLetter = findViewById(R.id.tv_first_letter);
        ivLogo = findViewById(R.id.iv_logo);
        edtRemark = findViewById(R.id.edt_remark);
        setValues();
    }

    private void setValues() {
        String firstLetter = "";
        if (from != null && from.equals("SendAgain")) {
            receiverName = userDetailsData.getReceiverName();
        } else {
            receiverName = commonFunctions.getFullName(userDetailsData);
        }
        tvName.setText(receiverName);
        firstLetter = String.valueOf(receiverName.charAt(0));
        if (userDetailsData.getProfileImage() != null && !userDetailsData.getProfileImage().equals("")) {
            tvFirstLetter.setVisibility(View.GONE);
            Glide.with(this)
                    .load(userDetailsData.getProfileImage())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(ivLogo);
        } else {
            tvFirstLetter.setVisibility(View.VISIBLE);
            tvFirstLetter.setText(firstLetter);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                if (userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                    callPaymentAPI(SthiramValues.ACCOUNT_TYPE_BUSINESS);
                } else {
                    callPaymentAPI("others");
                }
            }
        }
    }

    private void callPaymentAPI(String type) {
        String senderUserType = commonFunctions.getUserType();
        String senderAccountNumber = commonFunctions.getUserAccountNumber();
        String senderId = commonFunctions.getUserId();
        String senderName = commonFunctions.getFullName();
        String receiverAccountNumber = userDetailsData.getAccount_no();
        String receiverId = userDetailsData.getId();
        apiCall.makeY2YPayment(
                paymentRetry,
                type,
                senderUserType + "",
                senderId + "",
                senderName + "",
                senderAccountNumber + "",
                receiverAccountNumber + "",
                receiverName + "",
                receiverId + "",
                remark + "",
                amountToSend + "",
                SthiramValues.SELECTED_CURRENCY_ID + "",
                SthiramValues.SELECTED_CURRENCY_SYMBOL + "",
                true,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                paymentSuccessDialog(receiverName);
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            Log.e("Error",e+"");
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    }

                    @Override
                    public void retry() {
                        paymentRetry = true;
                        callPaymentAPI(type);
                    }
                });
    }

    private void paymentSuccessDialog(String receiverName) {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.payment_success_dialoge);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(commonFunctions.setAmount(amountToSend) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvUserName.setText(receiverName);
        tvTitle.setText(R.string.payment_sent_successfully);
        mButtonCancel.setOnClickListener(v -> {
            success.dismiss();
            commonFunctions.callHomeIntent();

         /*   Intent in = getIntent();
            setResult(Activity.RESULT_OK, in);
            finish();*/
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }
}