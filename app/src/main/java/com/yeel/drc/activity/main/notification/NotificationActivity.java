package com.yeel.drc.activity.main.notification;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.exchange.ExchangeCreditReceiptActivity;
import com.yeel.drc.activity.main.exchange.ExchangeDebitReceiptActivity;
import com.yeel.drc.activity.main.receipt.debit.CashPickupUserReciptActivity;
import com.yeel.drc.activity.main.receipt.debit.CashoutReciptActivity;
import com.yeel.drc.activity.main.receipt.credit.MoneyRecivedReceiptActivity;
import com.yeel.drc.activity.main.receipt.debit.MoneySentReceiptActivity;
import com.yeel.drc.activity.main.settings.kyc.ViewKYCImagesActivity;
import com.yeel.drc.adapter.NotificationsAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.notification.NotificationData;
import com.yeel.drc.api.notification.NotificationResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationActivity extends BaseActivity {

    private NotificationActivity context;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    TextView tvNoItem;
    List<NotificationData> notificationList, notificationListCopy;
    NotificationsAdapter notificationsAdapter;
    private boolean read = false;
    private boolean refresh = false;
    boolean getNotificationListRetry = false;
    boolean readAllNotificationRetry = false;

    ShimmerFrameLayout shimmerFrameLayoutOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context = NotificationActivity.this;
        setToolBar();
        initView();
        setItemListeners();

    }

    private void setItemListeners() {

    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);


        rvList = findViewById(R.id.recyClerAlerts);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        tvNoItem = findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        shimmerFrameLayoutOne = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutOne.startShimmer();
        getNotifications();
    }


    private void getNotifications() {
        if (!commonFunctions.getNotifications().equals("")) {
            String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getNotifications());
            Gson gson = new Gson();
            NotificationResponse apiResponse = gson.fromJson(jsonString, NotificationResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                notificationList = apiResponse.getResults();
                if (notificationList != null && notificationList.size() != 0) {
                    setList();
                } else {
                    shimmerFrameLayoutOne.setVisibility(View.GONE);
                    tvNoItem.setVisibility(View.VISIBLE);
                }
            }
        }
        apiCall.getNotificationList(getNotificationListRetry, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    commonFunctions.setNotifications(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    NotificationResponse apiResponse = gson.fromJson(jsonString, NotificationResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        notificationList = apiResponse.getResults();
                        if (notificationList != null && notificationList.size() != 0) {
                            setList();
                        } else {
                            shimmerFrameLayoutOne.setVisibility(View.GONE);
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    } else {
                        shimmerFrameLayoutOne.setVisibility(View.GONE);
                        tvNoItem.setVisibility(View.VISIBLE);
                        /*errorDialog.show(apiResponse.getMessage());*/
                    }
                } catch (Exception e) {
                    shimmerFrameLayoutOne.setVisibility(View.GONE);
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getNotificationListRetry = true;
                getNotifications();
            }
        });
    }

    private void setList() {
        tvNoItem.setVisibility(View.GONE);
        notificationListCopy = new ArrayList<>();
        notificationListCopy = notificationList.stream().filter(p -> p.getType().equals("kyc") || p.getMessage().toLowerCase().contains(SthiramValues.SELECTED_CURRENCY_SYMBOL.toLowerCase())).collect(Collectors.toList());
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        read = true;
        notificationsAdapter = new NotificationsAdapter(commonFunctions, false, context, notificationListCopy, new NotificationsAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                refresh = true;
                if (notificationListCopy.get(position).getType().equals("kyc")) {
                    String KYCStatus = commonFunctions.getKYCStatus();
                    if (KYCStatus.equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)) {
                        Intent in = new Intent(context, ViewKYCImagesActivity.class);
                        startActivity(in);
                    } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_APPROVED)) {
                        Toast.makeText(context, "KYC is approved", Toast.LENGTH_SHORT).show();
                    } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_SUBMITTED)) {
                        String validationMessage = context.getString(R.string.kyc_submitted_message);
                        Toast.makeText(context, validationMessage, Toast.LENGTH_SHORT).show();
                    } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
                        Intent in = new Intent(context, ViewKYCImagesActivity.class);
                        startActivity(in);
                    } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)) {
                        String validationMessage = context.getString(R.string.kyc_deactivated_message);
                        Toast.makeText(context, validationMessage, Toast.LENGTH_SHORT).show();
                    } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
                        Intent in = new Intent(context, ViewKYCImagesActivity.class);
                        startActivity(in);
                    }
                } else {
                    if (notificationListCopy.get(position).getMessage().toLowerCase().contains(SthiramValues.TRANSACTION_TYPE_CASH_OUT.toLowerCase())) {
                        Intent in = new Intent(context, CashoutReciptActivity.class);
                        in.putExtra("ydb_ref_id", notificationListCopy.get(position).getTransactionId());
                        in.putExtra("notificationId", notificationListCopy.get(position).getId());
                        startActivity(in);
                    } else if (notificationListCopy.get(position).getMessage().toLowerCase().contains(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP.toLowerCase())) {
                        Intent in = new Intent(context, CashPickupUserReciptActivity.class);
                        in.putExtra("ydb_ref_id", notificationListCopy.get(position).getTransactionId());
                        in.putExtra("notificationId", notificationListCopy.get(position).getId());
                        startActivity(in);
                    } else if (notificationListCopy.get(position).getMessage().toLowerCase().contains(SthiramValues.TRANSACTION_TYPE_EXCHANGE.toLowerCase())) {
                        Intent in;
                        if (SthiramValues.SELECTED_CURRENCY_SYMBOL.equals("SSP")) {
                            //credit
                            in = new Intent(context, ExchangeCreditReceiptActivity.class);
                        } else {
                            //debit
                            in = new Intent(context, ExchangeDebitReceiptActivity.class);
                        }
                        in.putExtra("ydb_ref_id", notificationListCopy.get(position).getTransactionId());
                        in.putExtra("notificationId", notificationListCopy.get(position).getId());
                        startActivity(in);
                    } else {
                        Intent in;
                        if (!notificationListCopy.get(position).getType().equals("Debit")) {
                            in = new Intent(context, MoneyRecivedReceiptActivity.class);
                        } else {
                            //debit
                            in = new Intent(context, MoneySentReceiptActivity.class);
                        }
                        in.putExtra("ydb_ref_id", notificationListCopy.get(position).getTransactionId());
                        in.putExtra("notificationId", notificationListCopy.get(position).getId());
                        startActivity(in);
                    }
                }
            }

            @Override
            public void sendAgain(View v, int position) {

            }
        });
        rvList.setAdapter(notificationsAdapter);
    }

    private void kycErrorDialogue(String message, String buttonLabel) {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_validation_design);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        String formattedAmount = commonFunctions.setAmount(commonFunctions.getPreApprovedLimit());
        textLimitAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount);

        TextView tvMessage = success.findViewById(R.id.tv_message);
        TextView tvCancel = success.findViewById(R.id.tv_cancel);
        TextView tvOk = success.findViewById(R.id.tv_ok);


        tvMessage.setText(message);
        tvCancel.setText(context.getString(R.string.skip));
        tvOk.setText(buttonLabel);

        tvCancel.setOnClickListener(v -> success.dismiss());
        tvOk.setOnClickListener(view -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            context.startActivity(in);
        });

        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    private void readAllNotification() {
        apiCall.readAllNotification(readAllNotificationRetry, commonFunctions.getUserId(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        refresh = true;
                        commonFunctions.setNotificationCount("0");
                        for (int i = 0; i < notificationList.size(); i++) {
                            notificationList.get(i).setReadStatus("1");
                            read = false;
                        }
                        notificationsAdapter.notifyDataSetChanged();
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                readAllNotificationRetry = true;
                readAllNotification();
            }
        });
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.alerts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_all, menu);
        return true;
    }

    //back button click
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (refresh) {
                    Intent in = getIntent();
                    setResult(Activity.RESULT_OK, in);
                }
                finish();
                return true;
            case R.id.id_share:
                if (read) {
                    readAllNotification();
                } else {
                    Toast.makeText(this, "Nothing to read", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh) {
            getNotifications();
        }
    }

    @Override
    public void onBackPressed() {
        if (refresh) {
            Intent in = getIntent();
            setResult(Activity.RESULT_OK, in);
        }
        finish();
    }
}