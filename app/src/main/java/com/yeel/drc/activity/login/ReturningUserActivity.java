package com.yeel.drc.activity.login;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.HomeActivity;
import com.yeel.drc.adapter.NumberListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.forceupdate.ForceUpdateData;
import com.yeel.drc.api.forceupdate.ForceUpdateResponse;
import com.yeel.drc.api.login.LoginWithPINResponse;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.ForceUpdateDialog;
import com.yeel.drc.dialogboxes.ForceUpdateNotMandatoryDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SharedPrefUtil;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReturningUserActivity extends BaseActivity {
    Context context;
    RecyclerView rvNumbers;
    LinearLayoutManager linearLayoutManager;
    NumberListAdapter numberListAdapter;
    List<String> numbersList;
    String pin = "";
    ImageView ivOne, ivTwo, ivThree, ivFour, ivFive, ivSix;
    ImageView ivKeyBordBack;
    TextView tvZero, mTVLoginWithOTP;
    TextView mTvNotYou, tvUserFirstLetter, tvUserName;
    TextView tvErrorMsg;
    APICall apiCall;
    APICall apiCallTwo;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    SomethingWentWrongDialog somethingWentWrongDialogTwo;
    ImageView ivProfileImage;
    boolean sendOTPForLoginRetry = false;
    boolean loginWithPINAPIRetry = false;
    boolean forceUpdateAPIRetry = false;
    DialogProgress dialogProgress;
    RelativeLayout rlBottom;
    TextView tvNewDevice;
    String from = "";
    String pushReceiverId = "";
    MultipleLoginDialog multipleLoginDialog;
    String fromLogout = "";
    boolean isDanger = false;
    boolean fromLoginWithPIN = false;
    String mobileNumber;
    String countryCode;
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returning_user);
        context = ReturningUserActivity.this;
        sharedPrefUtil = new SharedPrefUtil(context);
        if (getIntent().hasExtra("from-kyc")) {
            from = getIntent().getStringExtra("from-kyc");
            pushReceiverId = getIntent().getStringExtra("receiver-push-id");
        }
        if (getIntent().hasExtra("from-logout")) {
            fromLogout = getIntent().getStringExtra("from-logout");
        }
        if (getIntent().hasExtra("from-danger")) {
            isDanger = getIntent().getBooleanExtra("from-danger", false);
        }
        if (getIntent().hasExtra("mobileNumber")) {
            mobileNumber = getIntent().getStringExtra("mobileNumber");
        }
        if (getIntent().hasExtra("countryCode")) {
            countryCode = getIntent().getStringExtra("countryCode");
        }
        if (getIntent().hasExtra("fromLoginWithPIN")) {
            fromLoginWithPIN = getIntent().getBooleanExtra("fromLoginWithPIN", false);
        }
        initView();
        setItemListeners();
    }


    private void setItemListeners() {
        tvZero.setOnClickListener(v -> {
            pin = pin + "0";
            setImages(pin.length());
            if (pin.length() == 6) {
                callLoginAPI();
            }
        });
        ivKeyBordBack.setOnClickListener(v -> {
            if (pin.length() == 0) {
                setImages(0);
            } else {
                pin = pin.substring(0, pin.length() - 1);
                setImages(pin.length());
            }

        });
        mTVLoginWithOTP.setOnClickListener(v -> sendOtpAPICall());
        mTvNotYou.setOnClickListener(v -> {
            commonFunctions.setLoginStatus(false);
            Intent intent = new Intent(getApplicationContext(), ThudakkamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }


    private void initView() {
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.dismiss);
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialogTwo = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.dismiss);
        apiCallTwo = new APICall(context, SthiramValues.finish);
        tvNewDevice = findViewById(R.id.tv_new_device);

        rvNumbers = findViewById(R.id.rv_numbers);
        linearLayoutManager = new GridLayoutManager(context, 3);
        rvNumbers.setLayoutManager(linearLayoutManager);
        ivKeyBordBack = findViewById(R.id.iv_key_bord_back);
        tvZero = findViewById(R.id.iv_zero);
        ivOne = findViewById(R.id.iv_one);
        ivTwo = findViewById(R.id.iv_two);
        ivThree = findViewById(R.id.iv_three);
        ivFour = findViewById(R.id.iv_four);
        ivFive = findViewById(R.id.iv_five);
        ivSix = findViewById(R.id.iv_six);
        mTVLoginWithOTP = findViewById(R.id.tvLoginOTP);
        mTvNotYou = findViewById(R.id.tvNotYou);
        tvUserFirstLetter = findViewById(R.id.tv_first_Letter);
        tvUserName = findViewById(R.id.tv_name);
        tvErrorMsg = findViewById(R.id.tv_failed_message);
        tvErrorMsg.setVisibility(View.INVISIBLE);
        ivProfileImage = findViewById(R.id.iv_profile);
        rlBottom = findViewById(R.id.rl_bottom);
        createList();
        setValues();

        if (!fromLoginWithPIN && !isDanger) {
            callForceUpdateAPI();
        }
        if (fromLogout.equals("logout")) {
            callLogOutAPI();
        }

        commonFunctions.setUserId("");
        commonFunctions.setAccessToken("");
        commonFunctions.setLogoutStatus(true);

    }

    private void callLogOutAPI() {
        apiCall.logOutAPI(true, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
            }

            @Override
            public void retry() {

            }
        });
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


    private void setValues() {
        if (isDanger || fromLoginWithPIN) {
            commonFunctions.setUserId("");
            commonFunctions.setAccessToken("");
            commonFunctions.setPage("");
            commonFunctions.setLoginStatus(false);
            mTVLoginWithOTP.setVisibility(View.GONE);

            if (isDanger) {
                tvNewDevice.setVisibility(View.VISIBLE);
                tvUserName.setVisibility(View.GONE);
            } else {
                tvNewDevice.setVisibility(View.GONE);
                tvUserName.setVisibility(View.VISIBLE);
                tvUserName.setText(getString(R.string.welcom_back_yeel_user));
            }
            Glide.with(this)
                    .load(R.drawable.ic_user_dummy)
                    .placeholder(R.drawable.gray_round)
                    .into(ivProfileImage);
        } else {
            tvNewDevice.setVisibility(View.GONE);
            tvUserName.setVisibility(View.VISIBLE);
            mTVLoginWithOTP.setVisibility(View.VISIBLE);
            String firstLetter = "";
            String nameToShow = commonFunctions.getFullName();
            firstLetter = String.valueOf(commonFunctions.getFullName().charAt(0));
            if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
                nameToShow = commonFunctions.getFirstName();
            } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                nameToShow = commonFunctions.getBusinessName();
            } else if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
                if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                    nameToShow = commonFunctions.getFirstName();
                } else {
                    nameToShow = commonFunctions.getBusinessName();
                }
            }

            tvUserName.setText(getString(R.string.welcome) + ", " + commonFunctions.capitalize(nameToShow));
            if (commonFunctions.getProfileImage() != null && !commonFunctions.getProfileImage().equals("")) {
                tvUserFirstLetter.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(commonFunctions.getProfileImage())
                        .apply(new RequestOptions().circleCrop())
                        .placeholder(R.drawable.gray_round)
                        .into(ivProfileImage);

                Log.e("Image", commonFunctions.getProfileImage());
            } else {
                tvUserFirstLetter.setVisibility(View.VISIBLE);
                tvUserFirstLetter.setText(firstLetter);
            }
        }
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
        Collections.shuffle(numbersList);
        numberListAdapter = new NumberListAdapter(numbersList, context, (v, position) -> {
            pin = pin + numbersList.get(position);
            setImages(pin.length());
            if (pin.length() == 6) {
                callLoginAPI();
            }
        });
        rvNumbers.setAdapter(numberListAdapter);
    }

    private void callLoginAPI() {
        String newMobile = "";
        String newCountryCode = "";
        if (isDanger) {
            newMobile = mobileNumber;
            newCountryCode = countryCode;
        } else {
            newMobile = commonFunctions.getMobile();
            newCountryCode = commonFunctions.getCountryCode();
        }
        apiCall.loginWithPINAPI(loginWithPINAPIRetry, newMobile, newCountryCode, pin, commonFunctions.getFCMToken(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    LoginWithPINResponse apiResponse = gson.fromJson(jsonString, LoginWithPINResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.clearLocalStorage();
                        commonFunctions.setPIN(pin);
                        commonFunctions.setUserDetailsAfterLogin(apiResponse.getData().getAccess_token(), apiResponse.getData().getUser_details());
                        if (from.equals("kyc")) {
                            Intent in = new Intent(context, HomeActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            in.putExtra("isFromNotification", true);
                            startActivity(in);
                        } else {
                            Intent in = new Intent(context, HomeActivity.class);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                        }
                    } else {
                        setImages(0);
                        if (apiResponse.getError_type().equals(SthiramValues.ERROR_TYPE_MULTIPLE_LOGIN)) {
                            if (!multipleLoginDialog.isShowing()) {
                                multipleLoginDialog.show();
                            }
                        } else {
                            tvErrorMsg.setVisibility(View.VISIBLE);
                            tvErrorMsg.setText(apiResponse.getMessage());
                            if (apiResponse.getTriesLeft().equals("0")) {
                                rlBottom.setVisibility(View.INVISIBLE);
                                if(isDanger){
                                    errorDialogBoxTwo(apiResponse.getMessage());
                                }else{
                                    errorDialogBox(apiResponse.getMessage());
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    commonFunctions.logAValue("Error", e + "");
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                loginWithPINAPIRetry = true;
                callLoginAPI();

            }
        });
    }

    private void setImages(int length) {
        if (length == 1) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_white_round_blue_border);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 2) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 3) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 4) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 5) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_blue_round);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 6) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_blue_round);
            ivSix.setImageResource(R.drawable.ic_blue_round);
        } else {
            pin = "";
            ivOne.setImageResource(R.drawable.ic_white_round_blue_border);
            ivTwo.setImageResource(R.drawable.ic_white_round_blue_border);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        }
    }

    private void sendOtpAPICall() {
        apiCall.sendOTPForLogin("SMS",sendOTPForLoginRetry, commonFunctions.getFCMToken(), commonFunctions.getMobile(), commonFunctions.getCountryCode() + "", "", true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.toastOTP(apiResponse.getOtp());
                        Intent in = new Intent(context, LoginOtpVerificationActivity.class);
                        in.putExtra("mobileNumber", commonFunctions.getMobile());
                        in.putExtra("otpId", apiResponse.getOtp_id());
                        in.putExtra("country_code", commonFunctions.getCountryCode());
                        startActivity(in);
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
                sendOTPForLoginRetry = true;
                sendOtpAPICall();

            }
        });
    }

    public void errorDialogBox(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_style_login_with_otp);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        TextView tvClose = dialog.findViewById(R.id.tv_close);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvMessage.setText(message);
        tvClose.setOnClickListener(v -> dialog.dismiss());
        tvOk.setOnClickListener(view -> {
            dialog.dismiss();
            sendOtpAPICall();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public void errorDialogBoxTwo(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_style_login_with_otp_two);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvMessage.setText(message);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


}