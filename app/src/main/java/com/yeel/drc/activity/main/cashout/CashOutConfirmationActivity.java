package com.yeel.drc.activity.main.cashout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getfeedetails.FeeDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.CashOutData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.Objects;

public class CashOutConfirmationActivity extends BaseActivity {
    CashOutData cashOutData;
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    private Dialog success;;
    TextView tvCashOutAmount;
    TextView tvPurpose;
    TextView tvFees;
    TextView tvTotalToPay;
    TextView tvReceiveAmount;
    TextView tvPay;
    TextView tvAgentName;
    ImageView ivAgent;
    TextView tvAgentMobile;
    ScrollView llScrollView;
    ProgressBar progressBar;
    TextView tvFirstLetter;
    double amount;
    double calculatedFees;
    double totalAmount;
    boolean getFeesRetry = false;
    boolean cashOutRetry = false;
    String feesType = "PERC";
    String feesValue = "";
    String feesTierName = "";
    String agentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_out_confirmation);
        cashOutData = (CashOutData) getIntent().getSerializableExtra("data");
        context= CashOutConfirmationActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvPay.setOnClickListener(view -> {
            String message=commonFunctions.checkTransactionIsPossible(totalAmount+"",amount+"", SthiramValues.TRANSACTION_TYPE_CASH_OUT);
            if(message.equals(SthiramValues.SUCCESS)){
                Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
                pinVerificationListener.launch(intent);
            }
        });
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.home);
        apiCall = new APICall(context, SthiramValues.home);
        tvCashOutAmount =findViewById(R.id.tv_bill_amount);
        tvPurpose=findViewById(R.id.tv_purpose);
        tvFees=findViewById(R.id.tv_fees);
        tvTotalToPay=findViewById(R.id.totalToPay);
        tvReceiveAmount=findViewById(R.id.tvReceiveAmount);
        tvPay=findViewById(R.id.tv_button);
        tvAgentName =findViewById(R.id.tv_provider_name);
        ivAgent =findViewById(R.id.iv_agent);
        tvAgentMobile =findViewById(R.id.tv_provider_mobile);
        llScrollView=findViewById(R.id.ll_scroll_view);
        llScrollView.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        tvFirstLetter=findViewById(R.id.tv_first_Letter);
        callFeesAPI(cashOutData.getAmount());
    }

    private void callFeesAPI(String amount) {
        apiCall.getFeesDetails(
                getFeesRetry,
                false,
                "Yeel agent cashout",
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
            amount = Double.parseDouble(cashOutData.getAmount());
            if (amount >= 1) {
                calculatedFees=commonFunctions.calculateCommission(feesType+"",amount+"",feesValue+"");
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
    private void setValues() {
        progressBar.setVisibility(View.GONE);
        llScrollView.setVisibility(View.VISIBLE);
        setProviderDetails();
        tvPurpose.setText(SthiramValues.TRANSACTION_TYPE_CASH_OUT);
        tvCashOutAmount.setText(commonFunctions.setAmount(amount+"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvReceiveAmount.setText(commonFunctions.setAmount(amount+"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvFees.setText(commonFunctions.setAmount(calculatedFees +"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvTotalToPay.setText(commonFunctions.setAmount(totalAmount +"")+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
    }

    private void setProviderDetails() {
        AgentData agentData=cashOutData.getAgentData();
        if(agentData.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
            agentName=commonFunctions.generateFullName(
                    agentData.getFirstname(),
                    agentData.getMiddlename(),
                    agentData.getLastname()
            );
        }else{
            agentName=agentData.getBusiness_name();
        }
        tvAgentName.setText(agentName);
        if(agentData.getProfile_image()!=null&&!agentData.getProfile_image().equals("")&&agentData.getProfile_image().contains("http")){
            Glide.with(this)
                    .load(agentData.getProfile_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(ivAgent);
        }else{
            String firstLetter= String.valueOf(agentName.charAt(0));
            tvFirstLetter.setText(firstLetter);
        }
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
        String number=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),mobileNumberFormat);
        tvAgentMobile.setText(number);
    }

    ActivityResultLauncher<Intent> pinVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            doCashOut();
        }
    });

    private void doCashOut() {
        apiCall.doCashOut(
                cashOutRetry,
                true,
                commonFunctions.getUserType() + "",
                commonFunctions.getUserAccountNumber() + "",
                cashOutData.getAgentData().getAccount_no() + "",
                totalAmount + "",
                cashOutData.getRemark() + "",
                commonFunctions.getUserId() + "",
                commonFunctions.getFullName() + "",
                cashOutData.getAgentData().getId() + "",
                agentName+"",
                calculatedFees + "",
                amount + "",
                feesValue + "",
                feesTierName + "",
                SthiramValues.SELECTED_CURRENCY_ID+"",
                SthiramValues.SELECTED_CURRENCY_SYMBOL+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                        Gson gson = new Gson();
                        StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                        if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                            paymentSuccessDialog();
                        } else {
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }

                    @Override
                    public void retry() {
                        cashOutRetry = true;
                        doCashOut();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    private void paymentSuccessDialog() {
        success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.payment_success_dialoge);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        mButtonCancel.setText(R.string.ok);
        TextView tvUserName = success.findViewById(R.id.tv_user_name);
        TextView tvAmount = success.findViewById(R.id.tv_amount);
        TextView tvTitle = success.findViewById(R.id.tv_title);
        tvAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL+" "+commonFunctions.setAmount(amount+""));
        tvUserName.setText(R.string.cash_out_amount);
        tvTitle.setText(R.string.cashout_success_messgae);
        mButtonCancel.setOnClickListener(v -> {
            if (!isFinishing() || !isDestroyed()) {
                success.dismiss();
            }
            commonFunctions.callHomeIntent();
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        if (!isFinishing() || !isDestroyed()) {
            success.show();
        }
    }
}