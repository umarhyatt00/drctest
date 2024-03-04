package com.yeel.drc.activity.common;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.NumberListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.api.verifyotp.VerifyOtpResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class CommonEmailOTPVerificationActivity extends BaseActivity {
    String emailAddress;
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

    NumberListAdapter numberListAdapter;
    List<String> numbersList;
    String otp = "";
    int retryAttempts = 0;

    boolean reSend=false;
    String resendOTPMessage = "";
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    boolean sendEmailOtpRetry=false;
    boolean verifyEmailOtpRetry=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_email_o_t_p_verification);
        emailAddress =getIntent().getStringExtra("emailAddress");
        otpId=getIntent().getStringExtra("otpId");
        context=CommonEmailOTPVerificationActivity.this;
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        setToolBar();
        initView();
        setItemListeners();
    }

    private void initView() {
        resendOTPMessage=getString(R.string.didnot_get_code)+" <font size='5' color='#5463E8'>"+getString(R.string.resend_otp)+"</font>";
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

        String enter4DigitCodeInEn = getString(R.string.email_verification_message)+" <b>"+emailAddress+"</b>";
        tvMessage.setText(Html.fromHtml(enter4DigitCodeInEn));

        createList();
        timeCounter();
    }


    private void setItemListeners() {
        tvZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = otp + "0";
                setImages(otp.length());
                if (otp.length() == 4) {
                    if (retryAttempts < 3) {
                        callVerificationAPI();
                    } else {
                        Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
        ivKeyBordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp.length() == 0) {
                    setImages(0);
                } else {
                    otp = otp.substring(0, otp.length() - 1);
                    setImages(otp.length());
                }

            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (retryAttempts < 3) {
                    sendEmailOtp();
                } else {
                    Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                }
            }
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
        numberListAdapter = new NumberListAdapter(numbersList, context, new NumberListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                otp = otp + numbersList.get(position);
                setImages(otp.length());
                if (otp.length() == 4) {
                    if (retryAttempts < 3) {
                        callVerificationAPI();
                    } else {
                        Toast.makeText(context, getString(R.string.number_of_attempts_exceeded), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
        rvNumbers.setAdapter(numberListAdapter);
    }


    private void timeCounter() {
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                String resendSecCountMessage="";
                if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
                    resendSecCountMessage=getString(R.string.resend_otp_in) +" "+ millisUntilFinished / 1000 +" "+ getString(R.string.second);
                }else if(SthiramValues.SELECTED_LANGUAGE_ID.equals("3")){
                    resendSecCountMessage=millisUntilFinished / 1000 +" saniye içinde kodu yeniden gönder";
                }else{
                    resendSecCountMessage=getString(R.string.resend_otp_in) +" "+ millisUntilFinished / 1000 +" "+ getString(R.string.second);
                }
                tvTime.setText(resendSecCountMessage);
                reSend=false;
            }
            public void onFinish() {
                reSend=true;
                tvTime.setText(Html.fromHtml(resendOTPMessage));
            }
        }.start();
    }

    private void callResendAPI() {
        retryAttempts = 0;
        setImages(0);
        otp = "";
        timeCounter();
    }


    private void callVerificationAPI() {
        apiCall.verifyEmailOtp(verifyEmailOtpRetry,emailAddress,otpId,otp,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    VerifyOtpResponse apiResponse=gson.fromJson(jsonString,VerifyOtpResponse.class);
                    if(apiResponse.getStatus().equals(SthiramValues.SUCCESS)){
                        Intent in = getIntent();
                        setResult(Activity.RESULT_OK, in);
                        finish();
                    }else{
                        if(apiResponse.getRetry_attempts()!=null&&!apiResponse.getRetry_attempts().equals("")){
                            retryAttempts = Integer.parseInt(apiResponse.getRetry_attempts());
                        }
                        setImages(0);
                        otp = "";
                        tvFailedMessage.setVisibility(View.VISIBLE);
                        tvFailedMessage.setText(apiResponse.getMessage());
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }

            }
            @Override
            public void retry() {
                verifyEmailOtpRetry=true;
                callVerificationAPI();
            }
        });
    }

    private void setImages(int pinLength) {
        commonFunctions.logAValue("Entered OTP is",otp+"   ");
        tvFailedMessage.setVisibility(View.INVISIBLE);
        if (pinLength == 1) {
            ivOne.setText(otp.charAt(0)+"");
            ivTwo.setText("");
            ivThree.setText("");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_gray_line);
            ivLineThree.setImageResource(R.drawable.ic_gray_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 2) {
            ivOne.setText(otp.charAt(0)+"");
            ivTwo.setText(otp.charAt(1)+"");
            ivThree.setText("");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_blue_line);
            ivLineThree.setImageResource(R.drawable.ic_gray_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 3) {
            ivOne.setText(otp.charAt(0)+"");
            ivTwo.setText(otp.charAt(1)+"");
            ivThree.setText(otp.charAt(2)+"");
            ivFour.setText("");

            ivLineOne.setImageResource(R.drawable.ic_blue_line);
            ivLineTwo.setImageResource(R.drawable.ic_blue_line);
            ivLineThree.setImageResource(R.drawable.ic_blue_line);
            ivLineFour.setImageResource(R.drawable.ic_gray_line);
        } else if (pinLength == 4) {

            ivOne.setText(otp.charAt(0)+"");
            ivTwo.setText(otp.charAt(1)+"");
            ivThree.setText(otp.charAt(2)+"");
            ivFour.setText(otp.charAt(3)+"");
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

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    private void sendEmailOtp() {
        retryAttempts=++retryAttempts;
        apiCall.sendEmailOtp(sendEmailOtpRetry,emailAddress,otpId,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        setImages(0);
                        otp = "";
                        timeCounter();
                    } else {

                        setImages(0);
                        otp = "";
                        timeCounter();
                        errorDialog.show(apiResponse.getMessage());
                    }

                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();

                        setImages(0);
                        otp = "";
                        timeCounter();
                    }
                }
            }
            @Override
            public void retry() {
                sendEmailOtpRetry=true;
                sendEmailOtp();
            }
        });
    }
}