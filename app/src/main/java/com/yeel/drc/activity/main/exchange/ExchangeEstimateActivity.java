package com.yeel.drc.activity.main.exchange;

import androidx.appcompat.widget.Toolbar;
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

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExchangeEstimateActivity extends BaseActivity {

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CommonFunctions commonFunctions;
    Context context;

    EditText edtSendAmount;
    EditText edtGetAmount;
    TextView tvContinue;
    TextView tvExchangeRate;
    TextView tvLastTime;
    ImageView ivReceiverFlag;
    TextView tvReceiverCurrencyCode;
    RelativeLayout rlMain;
    ProgressBar progressBar;

    boolean sendAmountClick = false;
    boolean getAmountClick = false;
    Double conversionRate = 107.0;

    public ExchangeData exchangeData;

    String receiverCurrency="SSP";
    String senderCurrency="USD";
    boolean getExchangeRateRetry=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_estimate);
        context = ExchangeEstimateActivity.this;

        setToolBar();
        initView();
        setItemListeners();
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
            edtSendAmount.removeTextChangedListener(textWatcherSendAmount);
            edtGetAmount.removeTextChangedListener(textWatcherGetAmount);
            sendAmountClick = false;
            getAmountClick = true;
            textListener();
            return false;
        });

        tvContinue.setOnClickListener(view -> {
            if(!edtGetAmount.getText().toString().equals("")){
                if (exchangeData.getSendAmount() != null && !exchangeData.getSendAmount().isEmpty()) {
                    double amount = Double.parseDouble(exchangeData.getSendAmount());
                    String message = commonFunctions.checkTransactionIsPossible(exchangeData.getSendAmount(), exchangeData.getSendAmount(), SthiramValues.TRANSACTION_TYPE_EXCHANGE);
                    if (message.equals(SthiramValues.SUCCESS)) {
                        exchangeData.setConversionAmount(conversionRate + "");
                        exchangeData.setSenderCurrency(senderCurrency);
                        exchangeData.setReceiverCurrency(receiverCurrency);

                        Intent in=new Intent(context,ExchangeConfirmationActivity.class);
                        in.putExtra("data",exchangeData);
                        startActivity(in);
                    }
                } else {
                    errorDialog.show(getString(R.string.please_enter_an_amount));
                }
            }else{
                errorDialog.show(getString(R.string.please_enter_an_amount));
            }
        });
    }

    private void textListener() {
        if (sendAmountClick) {
            edtSendAmount.addTextChangedListener(textWatcherSendAmount);
        } else {
            edtGetAmount.addTextChangedListener(textWatcherGetAmount);
        }
    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);
        edtSendAmount = findViewById(R.id.edt_send_amount);
        edtGetAmount = findViewById(R.id.edt_get_amount);
        tvContinue = findViewById(R.id.tv_continue);
        tvExchangeRate = findViewById(R.id.tv_exchange_rate);
        tvLastTime = findViewById(R.id.tv_last_time);
        ivReceiverFlag = findViewById(R.id.iv_receiver_flag);
        tvReceiverCurrencyCode = findViewById(R.id.tv_receiver_currency);
        rlMain = findViewById(R.id.rl_main);
        rlMain.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress_bar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getConversionRate();
    }

    private void  getConversionRate() {
        exchangeData = new ExchangeData();
        edtGetAmount.setText("");
        edtSendAmount.setText("");
        rlMain.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        apiCall.getExchangeRate(
                commonFunctions.getUserId(),
                getExchangeRateRetry,
                false,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            ExchangeRateResponse apiResponse = gson.fromJson(jsonString, ExchangeRateResponse.class);
                            if(apiResponse.getStatus().equals(SthiramValues.SUCCESS)) {
                                rlMain.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                conversionRate = Double.valueOf(apiResponse.getExchangeRate().getConversionRate()+ "");

                               // 2022-09-05 09:59:43

                                String date = apiResponse.getExchangeRate().getConversionDate();
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                Date parsedDate = inputFormat.parse(date);
                                String formattedDate = outputFormat.format(parsedDate);
                                tvLastTime.setText(getString(R.string.last_updated_on)+" "+commonFunctions.dateOnlyFormat(formattedDate));
                                tvExchangeRate.setText("1.00 USD ="+conversionRate+" "+receiverCurrency);
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
                        getConversionRate();
                    }
                }

        );
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
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //SEND TEXT WATCHER
    TextWatcher textWatcherSendAmount = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.equals("")) {
                try {
                    Double value = Double.parseDouble(s + "");
                    double result = value * conversionRate;
                    BigDecimal credit = new BigDecimal(result);
                    BigDecimal roundOffC = credit.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                    edtGetAmount.setText(roundOffC + "");
                    exchangeData.setGetAmount(roundOffC + "");
                    exchangeData.setSendAmount(value + "");
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
                    exchangeData.setGetAmount(value + "");
                    exchangeData.setSendAmount(roundOffC + "");
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

}