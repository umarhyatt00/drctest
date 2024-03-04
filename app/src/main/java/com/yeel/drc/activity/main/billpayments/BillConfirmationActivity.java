package com.yeel.drc.activity.main.billpayments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.billpayment.BillPaymentResponse;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.billpayments.BillPaymentsData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.Objects;

public class BillConfirmationActivity extends BaseActivity {
    BillPaymentsData billPaymentData;
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    TextView tvStudentName;
    TextView tvStudentID;
    TextView tvStudentMobile;
    TextView tvBillAmount;
    TextView tvPurpose;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvPay;
    TextView tvProviderName;
    ImageView ivProvider;
    TextView tvProviderMobile;
    ScrollView llScrollView;
    ProgressBar progressBar;
    double amount;
    double calculatedFees;
    double totalAmount;
    boolean getFeesRetry = false;
    boolean paymentRetry = false;
    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_confirmation);
        context = BillConfirmationActivity.this;
        billPaymentData = (BillPaymentsData) getIntent().getSerializableExtra("data");
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvPay.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
            pinVerificationListener.launch(intent);
        });
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall = new APICall(context, SthiramValues.home);
        tvStudentName=findViewById(R.id.tvStudentName);
        tvStudentID=findViewById(R.id.tvStudentID);
        tvStudentMobile=findViewById(R.id.tvStudentMobile);
        tvBillAmount = findViewById(R.id.tv_bill_amount);
        tvPurpose = findViewById(R.id.tv_purpose);
        tvFees = findViewById(R.id.tv_fees);
        tvTotalToPay = findViewById(R.id.totalToPay);
        tvPay = findViewById(R.id.tv_button);
        tvProviderName = findViewById(R.id.tv_provider_name);
        ivProvider = findViewById(R.id.iv_provider);
        tvProviderMobile = findViewById(R.id.tv_provider_mobile);
        llScrollView = findViewById(R.id.ll_scroll_view);
        llScrollView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        callFeesAPI(billPaymentData.getAmount());
    }

    private void callFeesAPI(String amount) {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Bill Payment-Education",
                amount,
                commonFunctions.getUserId(),
                commonFunctions.getCurrencyID(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            FeeDetailsResponse apiResponse = gson.fromJson(jsonString, FeeDetailsResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                feesType = "PERC";
                                feesValue = apiResponse.getCommission().getPercentageRate();
                                feesTierName = apiResponse.getCommission().getTierName();
                                mCalculateCommission();
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getFeesRetry = true;
                        callFeesAPI(amount);
                    }
                }
        );
    }

    private void mCalculateCommission() {
        try {
            amount = Double.parseDouble(billPaymentData.getAmount());
            if (amount >= 1) {
                calculatedFees = commonFunctions.calculateCommission(feesType + "", amount + "", feesValue + "");
                totalAmount = amount + calculatedFees;
                setValues();
            } else {
                amount = 0.00;
                calculatedFees = 0.00;
                totalAmount = 0.00;
                setValues();
            }
        } catch (Exception e) {
            amount = 0.00;
            calculatedFees = 0.00;
            totalAmount = 0.00;
            setValues();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        progressBar.setVisibility(View.GONE);
        llScrollView.setVisibility(View.VISIBLE);
        setProviderDetails();
        tvStudentName.setText(billPaymentData.getStudentName());
        tvStudentID.setText(billPaymentData.getStudentId());
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),billPaymentData.getStudentMobileCountyCode());
        String number= commonFunctions.formatAMobileNumber(billPaymentData.getStudentMobileNumber(),billPaymentData.getStudentMobileCountyCode(),mobileNumberFormat);
        tvStudentMobile.setText(number);
        tvBillAmount.setText(commonFunctions.setAmount(amount + "") + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvPurpose.setText(billPaymentData.getProvidersListData().getService_type());
        tvFees.setText(commonFunctions.setAmount(calculatedFees + "") + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvTotalToPay.setText(commonFunctions.setAmount(totalAmount + "") + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
    }

    private void setProviderDetails() {
        ProvidersListData providersListData = billPaymentData.getProvidersListData();
        tvProviderName.setText(billPaymentData.getProvidersListData().getProvider_name());
        Glide.with(ivProvider.getContext()).load(SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL + "" + providersListData.getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProvider);
        String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), billPaymentData.getProvidersListData().getCountryCode());
        String number = commonFunctions.formatAMobileNumber(billPaymentData.getProvidersListData().getPhone(), billPaymentData.getProvidersListData().getCountryCode(), mobileNumberFormat);
        tvProviderMobile.setText(number);
    }


    private void billPaymentConfirmationAPI() {
        apiCall.billPaymentConfirmationAPI(
                paymentRetry,
                true,
                billPaymentData.getStudentMobileNumber()+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                billPaymentData.getProvidersListData().getProvider_name()+"",
                commonFunctions.getCountryCode()+"",
                commonFunctions.getMobile()+"",
                billPaymentData.getProvidersListData().getReceiver_id()+"",
                billPaymentData.getRemark()+"",
                billPaymentData.getStudentName()+"",
                feesValue+"",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                calculatedFees+"",
                commonFunctions.getUserAccountNumber()+"",
                feesTierName+"",
                billPaymentData.getProvidersListData().getService_type()+"",
                totalAmount+"",
                billPaymentData.getProvidersListData().getId()+"",
                billPaymentData.getStudentMobileCountyCode()+"",
                commonFunctions.getUserId()+"",
                billPaymentData.getProvidersListData().getAccountNumber()+"",
                billPaymentData.getStudentId()+"",
                commonFunctions.getFullName()+"",
                billPaymentData.getProvidersListData().getIs_external()+"",
                billPaymentData.getAmount()+"",
                commonFunctions.getUserType()+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            BillPaymentResponse apiResponse = gson.fromJson(jsonString, BillPaymentResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                 mBillPaymentSuccessDialog(apiResponse.getYdb_ref_id(),billPaymentData.getProvidersListData().getProvider_name(), totalAmount +"");
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        paymentRetry = true;
                        billPaymentConfirmationAPI();
                    }
                }
        );
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.verify_details);
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

    @SuppressLint("SetTextI18n")
    private void mBillPaymentSuccessDialog(String transactionId, String providerName, String amount) {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.bill_payment_success_dialog);
        TextView tvTransactionId = success.findViewById(R.id.transaction_id);
        TextView utilityProvider = success.findViewById(R.id.utilityProvider);
        TextView amountPaid = success.findViewById(R.id.amountPaid);
        tvTransactionId.setText(transactionId);
        utilityProvider.setText(providerName);
        amountPaid.setText(amount + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        TextView doneButton = success.findViewById(R.id.btn_done);
        doneButton.setOnClickListener(v -> {
            success.dismiss();
            commonFunctions.callHomeIntent();
        });
        Objects.requireNonNull(success.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    ActivityResultLauncher<Intent> pinVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            String message = commonFunctions.checkTransactionIsPossible(totalAmount+"",amount+"", SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS);
            if (message.equals(SthiramValues.SUCCESS)) {
                billPaymentConfirmationAPI();
            }
        }
    });


}