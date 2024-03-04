package com.yeel.drc.activity.main.mobilepay;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.mpesafees.MpesaFeeResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.mpesa.MobilePayData;
import com.yeel.drc.model.mpesa.response.MpesaExchangeRateResponse;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MobilePayEstimateActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CommonFunctions commonFunctions;

    EditText edtSendAmount;
    EditText edtGetAmount;
    TextView tvContinue;
    TextView tvExchangeRate;
    TextView tvLastTime;
    ImageView ivReceiverFlag;
    TextView tvReceiverCurrencyCode;
    RelativeLayout rlMain;
    ProgressBar progressBar;
    TextView tvDailyTransactionLimit;

    boolean sendAmountClick = false;
    boolean getAmountClick = false;
    Double conversionRate = 0.00;

    boolean getExchangeRateRetry = false;

    MobilePayData mobilePayData;

    String receiverCurrency;
    boolean getFeesRetry = false;
    double dailyTransactionLimit=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa_estimate);
        context = MobilePayEstimateActivity.this;
        mobilePayData = (MobilePayData) getIntent().getSerializableExtra("data");
        if (mobilePayData == null) {
            mobilePayData = new MobilePayData();
        }
        setToolBar();
        initView();
        setItemListeners();
        getExchangeRateMpesa();

    }

    private void getExchangeRateMpesa() {
        apiCall.getMpesaExchangeRate(
                mobilePayData.getMobilePayCode(),
                getExchangeRateRetry,
                false,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MpesaExchangeRateResponse apiResponse = gson.fromJson(jsonString, MpesaExchangeRateResponse.class);
                            if(apiResponse.getStatus().equals(SthiramValues.SUCCESS)) {
                                rlMain.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                conversionRate = Double.valueOf(apiResponse.getData().getPayRate() + "");
                                String date = apiResponse.getData().getDated();
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date parsedDate = inputFormat.parse(date);
                                String formattedDate = outputFormat.format(parsedDate);
                                tvLastTime.setText(getString(R.string.last_updated_on)+commonFunctions.dateOnlyFormat(formattedDate));
                                tvExchangeRate.setText("1.00 USD = "+conversionRate+" "+receiverCurrency);
                            }else{
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getExchangeRateRetry = true;
                        getExchangeRateMpesa();
                    }
                }

        );
    }

    private void getMpesaLimits() {
        apiCall.getMpesaLimits(
                mobilePayData.getMobilePayCode(),
                getExchangeRateRetry,
                true,
                mobilePayData.getSendAmount()+"",
                commonFunctions.getUserId()+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equals(SthiramValues.SUCCESS)){
                                mobilePayData.setConversionAmount(conversionRate + "");
                                mobilePayData.setReceiverCurrency(receiverCurrency);
                                Intent in = new Intent(context, MobilePayMobileEnterActivity.class);
                                in.putExtra("data", mobilePayData);
                                startActivity(in);
                            }else{
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getExchangeRateRetry = true;
                        getExchangeRateMpesa();
                    }
                }

        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setItemListeners() {

        edtSendAmount.setOnTouchListener((v, event) -> {
            edtSendAmount.removeTextChangedListener(textWatcherSendAmount);
            edtGetAmount.removeTextChangedListener(textWatcherGetAmount);
            sendAmountClick = true;
            getAmountClick = false;
            textListener();
            return false;
        });


        edtGetAmount.setOnTouchListener((v, event) -> {
            // mpesaDataModel.setSelectedAmountType("USD");
            edtSendAmount.removeTextChangedListener(textWatcherSendAmount);
            edtGetAmount.removeTextChangedListener(textWatcherGetAmount);
            sendAmountClick = false;
            getAmountClick = true;
            textListener();
            return false;
        });

        tvContinue.setOnClickListener(view -> {
            if (mobilePayData.getSendAmount() != null && !mobilePayData.getSendAmount().isEmpty()) {
                String type="";
                if(mobilePayData.getMobilePayCode().equals(SthiramValues.MPESA_KENYA_CODE)){
                    type= SthiramValues.TRANSACTION_TYPE_MPESA;
                }else if(mobilePayData.getMobilePayCode().equals(SthiramValues.AIRTAL_UGANDA_CODE)){
                    type= SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA;
                }
                double amount = Double.parseDouble(mobilePayData.getSendAmount());
                if(amount<=dailyTransactionLimit){
                    String message=commonFunctions.checkTransactionIsPossible(mobilePayData.getSendAmount(), mobilePayData.getSendAmount(),type);
                    if(message.equals(SthiramValues.SUCCESS)){
                        mobilePayData.setConversionAmount(conversionRate + "");
                        mobilePayData.setReceiverCurrency(receiverCurrency);
                        callFeesAPI();
                    }
                }else{

                }


            }else{
                errorDialog.show(getString(R.string.please_enter_an_amount));
            }
        });
    }

    private void initView() {
        commonFunctions=new CommonFunctions(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);
        edtSendAmount = findViewById(R.id.edt_send_amount);
        edtGetAmount = findViewById(R.id.edt_get_amount);
        tvContinue = findViewById(R.id.tv_continue);
        tvExchangeRate = findViewById(R.id.tv_exchange_rate);
        tvLastTime = findViewById(R.id.tv_last_time);
        ivReceiverFlag=findViewById(R.id.iv_receiver_flag);
        tvReceiverCurrencyCode=findViewById(R.id.tv_receiver_currency);
        rlMain=findViewById(R.id.rl_main);
        rlMain.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progress_bar);
        tvDailyTransactionLimit=findViewById(R.id.tvDailyTransactionLimit);

        if(mobilePayData.getMobilePayCode().equals(SthiramValues.MPESA_KENYA_CODE)){
            dailyTransactionLimit=Double.parseDouble(commonFunctions.getMpesaDailyLimit());
            setGetCurrencyDetails(SthiramValues.KENYA_CURRENCY_CODE, SthiramValues.KENYA_COUNTY_FLAG);
        }else if(mobilePayData.getMobilePayCode().equals(SthiramValues.AIRTAL_UGANDA_CODE)){
            dailyTransactionLimit=Double.parseDouble(commonFunctions.getAirtelDailyLimit());
            setGetCurrencyDetails(SthiramValues.UGANDA_CURRENCY_CODE, SthiramValues.UGANDA_COUNTY_FLAG);
        }

    }

    private void setGetCurrencyDetails(String currencyCode,String flag) {
        receiverCurrency=currencyCode;
        tvReceiverCurrencyCode.setText(receiverCurrency);
        tvDailyTransactionLimit.setText(getString(R.string.daily_limit)+" "+commonFunctions.setAmount(dailyTransactionLimit+"")+" "+receiverCurrency);
        setFlag(flag);
    }
    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivReceiverFlag);
    }


    //SEND TEXT WATCHER

    TextWatcher textWatcherSendAmount = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.equals("")) {
                try {
                    Double value = Double.parseDouble(s + "");
                    Double result = value * conversionRate;
                    BigDecimal credit = new BigDecimal(result);
                    BigDecimal roundOffC = credit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    edtGetAmount.setText(roundOffC + "");
                    mobilePayData.setGetAmount(roundOffC + "");
                    mobilePayData.setSendAmount(value + "");
                } catch (Exception e) {
                    edtGetAmount.setText("");
                }

            } else {
                edtGetAmount.setText("");
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    //GET TEXT WATCHER

    TextWatcher textWatcherGetAmount = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.equals("")) {
                try {
                    Double value = Double.parseDouble(s + "");
                    Double result = value / conversionRate;
                    BigDecimal credit = new BigDecimal(result);
                    BigDecimal roundOffC = credit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    edtSendAmount.setText(roundOffC + "");
                    mobilePayData.setGetAmount(value + "");
                    mobilePayData.setSendAmount(roundOffC + "");
                } catch (Exception e) {
                    edtSendAmount.setText("");
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void textListener() {
        if (sendAmountClick) {
            edtSendAmount.addTextChangedListener(textWatcherSendAmount);
        } else {
            edtGetAmount.addTextChangedListener(textWatcherGetAmount);
        }
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.get_an_estimate);
    }

    //back button click
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }


    private void callFeesAPI() {
        apiCall.getMPesaFeesDetails(
                mobilePayData.getMobilePayCode(),
                getFeesRetry,
                true,
                mobilePayData.getSendAmount() + "",
                commonFunctions.getUserId()+"",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MpesaFeeResponse apiResponse = gson.fromJson(jsonString, MpesaFeeResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                mobilePayData.setFeesType(apiResponse.getData().getType());
                                mobilePayData.setFeesValue(apiResponse.getData().getFees());
                                Intent in = new Intent(context, MobilePayMobileEnterActivity.class);
                                in.putExtra("data", mobilePayData);
                                startActivity(in);
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
                        callFeesAPI();
                    }
                }

        );
    }
}