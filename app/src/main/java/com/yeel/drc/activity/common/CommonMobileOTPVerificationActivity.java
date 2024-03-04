package com.yeel.drc.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.DistrictListAdapter;
import com.yeel.drc.adapter.NumberListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.api.verifyotp.VerifyOtpResponse;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class CommonMobileOTPVerificationActivity extends BaseActivity {
    String mobileNumber;
    String otpId;
    Context context;

    RecyclerView rvNumbers;
    LinearLayoutManager linearLayoutManager;
    TextView tvFailedMessage;
    TextView ivOne, ivTwo, ivThree, ivFour;
    ImageView ivLineOne, ivLineTwo, ivLineThree, ivLineFour;
    ImageView ivKeyBordBack;
    TextView tvZero;
    TextView tvTime;
    TextView tvMessage;
    String countyCode;

    NumberListAdapter numberListAdapter;
    List<String> numbersList;
    String otp = "";
    int retryAttempts = 0;
    Boolean sendOtpRetry = false;
    boolean reSend = false;
    String resendOTPMessage = "";
    APICall apiCall;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean otpVerificationRetry = false;
    MultipleLoginDialog multipleLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_mobile_o_t_p_verification);
        context = CommonMobileOTPVerificationActivity.this;
        mobileNumber = getIntent().getStringExtra("mobileNumber");
        otpId = getIntent().getStringExtra("otpId");
        countyCode=getIntent().getStringExtra("country_code");
        apiCall = new APICall(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        setToolBar();
        initView();
        setItemListeners();
        startSMSRetriever();
    }

    private void startSMSRetriever() {
        SmsRetrieverClient client = SmsRetriever.getClient(context);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
        });
        task.addOnFailureListener(e -> {
        });
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("SustentaApp"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            getOTP(message);

        }
    };

    private void getOTP(String message) {
        String value = message.substring(message.lastIndexOf(": ") + 1);
        otp = value.substring(0, 5).trim();
        setImages(otp.length());
        if (otp.length() == 4) {
            otpVerification();
        }
    }

    private void initView() {
        resendOTPMessage = getString(R.string.didnot_get_code) + " <font size='5' color='#5463E8'>" + getString(R.string.resend_otp) + "</font>";
        multipleLoginDialog=new MultipleLoginDialog(context, SthiramValues.finish);
        rvNumbers = findViewById(R.id.rv_numbers);
        linearLayoutManager = new GridLayoutManager(context, 3);
        rvNumbers.setLayoutManager(linearLayoutManager);
        tvFailedMessage = findViewById(R.id.tv_failed_message);
        tvFailedMessage.setVisibility(View.INVISIBLE);
        ivOne = findViewById(R.id.tv_pin_one);
        ivTwo = findViewById(R.id.tv_pin_two);
        ivThree = findViewById(R.id.tv_pin_three);
        ivFour = findViewById(R.id.tv_pin_four);
        ivLineOne = findViewById(R.id.iv_line_one);
        ivLineTwo = findViewById(R.id.iv_line_two);
        ivLineThree = findViewById(R.id.iv_line_three);
        ivLineFour = findViewById(R.id.iv_line_four);
        ivKeyBordBack = findViewById(R.id.iv_key_bord_back);
        tvZero = findViewById(R.id.iv_zero);
        tvTime = findViewById(R.id.tv_time);
        tvMessage = findViewById(R.id.tv_message);

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),countyCode);
        String number= commonFunctions.formatAMobileNumber(mobileNumber,countyCode,mobileNumberFormat);



        String enter4DigitCodeInEn=getString(R.string.enter_4_gigit_otp);
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            tvMessage.setText(number);
        }else{
            tvMessage.setText(enter4DigitCodeInEn+" "+number);
        }

        createList();
        timeCounter();
    }


    private void setItemListeners() {
        tvZero.setOnClickListener(view -> {
            otp = otp + "0";
            setImages(otp.length());
            if (otp.length() == 4) {
                if (retryAttempts < 3) {
                    otpVerification();
                } else {
                    Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        ivKeyBordBack.setOnClickListener(view -> {
            if (otp.length() == 0) {
                setImages(0);
            } else {
                otp = otp.substring(0, otp.length() - 1);
                setImages(otp.length());
            }
        });
        tvTime.setOnClickListener(view -> {
            if(reSend){
                if (retryAttempts < 3) {
                    bottomSheetDialogue();
                } else {
                    tvFailedMessage.setText(getString(R.string.number_of_attempts_exceeded));
                    Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                }
            }

        });
        tvMessage.setOnClickListener(view -> {
            Intent in = getIntent();
            setResult(Activity.RESULT_OK, in);
            finish();
        });
    }


    private void createList() {
        numbersList = new ArrayList<>();
        numbersList.add("1");
        numbersList.add("2");
        numbersList.add("3");
        numbersList.add("4");
        numbersList.add("5");
        numbersList.add("6");
        numbersList.add("7");
        numbersList.add("8");
        numbersList.add("9");
        numberListAdapter = new NumberListAdapter(numbersList, context, (v, position) -> {
            otp = otp + numbersList.get(position);
            setImages(otp.length());
            if (otp.length() == 4) {
                if (retryAttempts < 3) {
                    otpVerification();
                } else {
                    Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        rvNumbers.setAdapter(numberListAdapter);
    }


    private void timeCounter() {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                String resendSecCountMessage = "";
                if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
                    resendSecCountMessage = getString(R.string.resend_otp_in) + " " + millisUntilFinished / 1000 + " " + getString(R.string.second);
                } else if (SthiramValues.SELECTED_LANGUAGE_ID.equals("3")) {
                    resendSecCountMessage = millisUntilFinished / 1000 + " saniye içinde kodu yeniden gönder";
                } else {
                    resendSecCountMessage = getString(R.string.resend_otp_in) + " " + millisUntilFinished / 1000 + " " + getString(R.string.second);
                }
                tvTime.setText(resendSecCountMessage);

                reSend = false;
            }

            public void onFinish() {
                reSend = true;
                tvTime.setText(Html.fromHtml(resendOTPMessage));
            }
        }.start();
    }


    private void setImages(int pinLength) {
        tvFailedMessage.setVisibility(View.INVISIBLE);
        if (pinLength == 1) {
            ivOne.setText(otp.charAt(0) + "");
            ivTwo.setText("");
            ivThree.setText("");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_gray_line);
            ivLineThree.setImageResource(R.drawable.ic_gray_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 2) {
            ivOne.setText(otp.charAt(0) + "");
            ivTwo.setText(otp.charAt(1) + "");
            ivThree.setText("");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_blue_line);
            ivLineThree.setImageResource(R.drawable.ic_gray_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 3) {
            ivOne.setText(otp.charAt(0) + "");
            ivTwo.setText(otp.charAt(1) + "");
            ivThree.setText(otp.charAt(2) + "");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_blue_line);
            ivLineThree.setImageResource(R.drawable.ic_blue_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 4) {
            ivOne.setText(otp.charAt(0) + "");
            ivTwo.setText(otp.charAt(1) + "");
            ivThree.setText(otp.charAt(2) + "");
            ivFour.setText(otp.charAt(3) + "");
            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_blue_line);
            ivLineThree.setImageResource(R.drawable.ic_blue_line);
            ivLineFour.setImageResource(R.drawable.ic_blue_line);
        } else if (pinLength == 0) {
            ivOne.setText("");
            ivTwo.setText("");
            ivThree.setText("");
            ivFour.setText("");
            ivLineOne.setImageResource(R.drawable.ic_gray_line);
            ivLineTwo.setImageResource(R.drawable.ic_gray_line);
            ivLineThree.setImageResource(R.drawable.ic_gray_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        }
    }


    private void otpVerification() {
        apiCall.otpVerification(otpVerificationRetry, mobileNumber,countyCode, otpId, otp, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    VerifyOtpResponse apiResponse = gson.fromJson(jsonString, VerifyOtpResponse.class);
                    if (apiResponse.getStatus().equals(SthiramValues.SUCCESS)) {
                        Intent in = getIntent();
                        setResult(Activity.RESULT_OK, in);
                        finish();
                    } else {
                        if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            if (apiResponse.getRetry_attempts() != null && !apiResponse.getRetry_attempts().equals("")) {
                                retryAttempts = Integer.parseInt(apiResponse.getRetry_attempts());
                            }
                            setImages(0);
                            otp = "";
                            tvFailedMessage.setVisibility(View.VISIBLE);
                            tvFailedMessage.setText(apiResponse.getMessage());
                        }
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }

            }

            @Override
            public void retry() {
                otpVerificationRetry = true;
                otpVerification();
            }
        });
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
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

    private void sendOtpAPICall(String type) {
        retryAttempts = ++retryAttempts;
        apiCall.sendOTP(type,sendOtpRetry,commonFunctions.getFCMToken(), mobileNumber,countyCode, otpId, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        otp = "";
                        setImages(0);
                        timeCounter();
                        startSMSRetriever();
                    } else {
                        setImages(0);
                        otp = "";
                        timeCounter();
                        tvFailedMessage.setText(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                        setImages(0);
                        timeCounter();
                        otp = "";
                    }
                }
            }

            @Override
            public void retry() {
                if (retryAttempts > 3) {
                    sendOtpRetry = true;
                } else {
                    sendOtpRetry = false;
                    sendOtpAPICall(type);
                }

            }
        });
    }

    @SuppressLint("InflateParams")
    private void bottomSheetDialogue() {
        View moreBottomSheetView;
        BottomSheetDialog moreBottomSheetDialog;
        moreBottomSheetView = getLayoutInflater().inflate(R.layout.add_fund_and_exchange_layout, null);
        moreBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCurved);
        moreBottomSheetDialog.setContentView(moreBottomSheetView);
        BottomSheetBehavior.from((View) moreBottomSheetView.getParent());
        moreBottomSheetDialog.setCanceledOnTouchOutside(false);
        moreBottomSheetDialog.setCancelable(false);
        moreBottomSheetDialog.show();
        RecyclerView rvMenuItem = moreBottomSheetView.findViewById(R.id.rvMenuList);
        ImageView ivCloseMore = moreBottomSheetView.findViewById(R.id.ivCloseMore);
        TextView title=moreBottomSheetView.findViewById(R.id.income_title);
        title.setText(R.string.send_otp_via);
        List<String> menuList = new ArrayList<>();
        menuList.add(getString(R.string.whatsapp));
        menuList.add(getString(R.string.sms));
        DistrictListAdapter menuAdapter = new DistrictListAdapter(menuList, context, (v, position) -> {
            moreBottomSheetDialog.dismiss();
            if (position == 0) {
                sendOtpAPICall("WhatsApp");
            } else if (position == 1) {
                sendOtpAPICall("SMS");
            }
        });
        rvMenuItem.setAdapter(menuAdapter);
        ivCloseMore.setOnClickListener(view -> moreBottomSheetDialog.dismiss());
    }


}