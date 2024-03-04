package com.yeel.drc.activity.main.fragments.user;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.HomeActivity;
import com.yeel.drc.activity.main.ModeOfPaymentActivity;
import com.yeel.drc.activity.main.billpayments.ProvidersListActivity;
import com.yeel.drc.activity.main.exchange.ExchangeEstimateActivity;
import com.yeel.drc.activity.main.cashout.WhatIsCashOutActivity;
import com.yeel.drc.activity.main.notification.NotificationActivity;
import com.yeel.drc.activity.main.payme.PayMeActivity;
import com.yeel.drc.activity.main.homepayandsend.AmountEnteringActivity;
import com.yeel.drc.activity.main.homepayandsend.ScanAndPayActivity;
import com.yeel.drc.activity.main.settings.SettingsActivity;
import com.yeel.drc.activity.main.settings.kyc.ViewKYCImagesActivity;
import com.yeel.drc.adapter.BillPaymentListAdapter;
import com.yeel.drc.adapter.RecentTransactionAdapter;
import com.yeel.drc.adapter.ViewPagerAdapterHomeSlider;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.basicdetails.BasicDetailsResponse;
import com.yeel.drc.api.basicdetails.BillPaymentOptionsData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.login.LoginWithPINResponse;
import com.yeel.drc.api.recenttransactions.RecentTransactionResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.dialogboxes.AddNewCurrencyDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.HomeMenuData;
import com.yeel.drc.model.HomeMenu;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.TransactionsRedirections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.yeel.drc.utils.SthiramValues.KYC_STATUS_DEACTIVATED;
import static com.yeel.drc.utils.SthiramValues.isNotificationReceivedToRefresh;

public class HomeFragment extends Fragment {
    View view;
    ImageView ivLogout;
    ImageView tvSettings;
    Context context;
    CommonFunctions commonFunctions;
    PermissionUtils permissionUtils;

    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;

    LinearLayout llPayMe;
    LinearLayout llCashOut;
    LinearLayout llPay;
    LinearLayout llSend;

    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    public static final int PAY_INTENT_ID = 101;
    public static final int REFRESH_SCREEN = 102;


    String permissionContact = Manifest.permission.READ_CONTACTS;
    static final int CONTACT_REQUEST_CODE = 103;
    APICall apiCall;
    ErrorDialog errorDialog;
    ErrorDialog loginErrorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    List<TransactionsData> recentTransactionsList;
    RecentTransactionAdapter recentTransactionAdapter;
    RecyclerView rvRecentTransactions;
    LinearLayoutManager linearLayoutManager;
    LinearLayout llTransactions;

    List<BillPaymentOptionsData> billPaymentOptionsList;
    RecyclerView rvBillPayment;
    LinearLayoutManager linearLayoutManagerBillPayments;
    BillPaymentListAdapter billPaymentListAdapter;
    LinearLayout llBillPayment;
    RelativeLayout rlNotification;
    ImageView ivGreenIcon;
    boolean loginWithPINAPIRetry = false;
    boolean getUserDetailsUsingQrCodeRetry = false;
    boolean getUserBasicDetailsRetry = false;
    boolean recentTransactionsRetry = false;
    TextView tvViewALL;
    LinearLayout llNoTransactions;
    ShimmerFrameLayout shimmerFrameLayoutOne;
    ShimmerFrameLayout shimmerFrameLayoutTwo;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean kycErrorShown = false;


    MultipleLoginDialog multipleLoginDialog;


    ViewPager viewPager;
    ViewPagerAdapterHomeSlider viewPagerAdapter;
    LinearLayout sliderDots;
    private int dotsCount;
    private ImageView[] dots;

    AddNewCurrencyDialog addNewCurrencyDialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        initView();
        setItemListeners();
        return view;
    }

    private void setItemListeners() {
        ivLogout.setOnClickListener(view1 -> logOutIntent());
        tvSettings.setOnClickListener(view1 -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                Intent in = new Intent(context, SettingsActivity.class);
                startActivity(in);
            }
        });
        llPayMe.setOnClickListener(view -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                Intent in = new Intent(context, PayMeActivity.class);
                startActivity(in);
            }

        });
        llCashOut.setOnClickListener(view -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                Intent in = new Intent(context, WhatIsCashOutActivity.class);
                startActivity(in);
            }

        });
        llPay.setOnClickListener(view -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                if (permissionUtils.checkCameraPermissionForScan(requestPermissionLauncherCamera)) {
                    Intent intent = new Intent(getActivity(), ScanAndPayActivity.class);
                    intent.putExtra("from", "pay");
                    startActivityForResult(intent, PAY_INTENT_ID);
                }
            }
        });
        llSend.setOnClickListener(view -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                Intent in = new Intent(context, ModeOfPaymentActivity.class);
                startActivity(in);
            }
        });
        rlNotification.setOnClickListener(view -> {
            if (!commonFunctions.getBasicDetails().equals("")) {
                Intent in = new Intent(context, NotificationActivity.class);
                startActivityForResult(in, REFRESH_SCREEN);
            }

        });
        tvViewALL.setOnClickListener(view -> {
            HomeActivity.bottomNavigation.setCurrentItem(2);
            Fragment fragment = new TransactionFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecentTransactions("swipe");
                getBasicDetails("swipe");
            }
        });
    }


    private void initView() {
        addNewCurrencyDialog = new AddNewCurrencyDialog(context, commonFunctions);
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
        permissionUtils = new PermissionUtils(context);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        loginErrorDialog = new ErrorDialog(context, SthiramValues.logout);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        llPay = view.findViewById(R.id.home_btn_pay);
        llPayMe = view.findViewById(R.id.home_btn_pay_me);
        llCashOut = view.findViewById(R.id.home_btn_cash_out);
        llSend = view.findViewById(R.id.ll_send_btn);
        ivLogout = view.findViewById(R.id.home_btn_logot);
        tvSettings = view.findViewById(R.id.home_btn_settings);
        rvRecentTransactions = view.findViewById(R.id.rv_recent_transactions);
        linearLayoutManager = new LinearLayoutManager(context);
        rvRecentTransactions.setLayoutManager(linearLayoutManager);
        llTransactions = view.findViewById(R.id.ll_transactions);
        rlNotification = view.findViewById(R.id.rl_notification);
        tvViewALL = view.findViewById(R.id.tvViewALL);
        llNoTransactions = view.findViewById(R.id.ll_no_transaction);
        llNoTransactions.setVisibility(View.GONE);


        rvBillPayment = view.findViewById(R.id.rv_bill_payment);
        linearLayoutManagerBillPayments = new GridLayoutManager(context, 4);
        rvBillPayment.setLayoutManager(linearLayoutManagerBillPayments);
        llBillPayment = view.findViewById(R.id.ll_bill_payment);
        llBillPayment.setVisibility(View.GONE);
        ivGreenIcon = view.findViewById(R.id.iv_green_icon);
        shimmerFrameLayoutOne = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutTwo = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_two);
        shimmerFrameLayoutOne.startShimmer();
        shimmerFrameLayoutTwo.startShimmer();
        createSecurityDialogue();
        if (commonFunctions.isSequrityEnabled()) {
            commonFunctions.logAValue("isSequrityEnabled", "True");
            if (commonFunctions.getSecurityShownStatus()) {
                //security already shown for session
                callHomeAPI();
            } else {
                authenticateApp();
            }
        } else {
            commonFunctions.logAValue("isSequrityEnabled", "False");
            if (commonFunctions.isSkipFingerPrint()) {
                commonFunctions.logAValue("isSkipFingerPrint", "True");
                //Finger print option already skipped
                callHomeAPI();
            } else {
                commonFunctions.logAValue("isSkipFingerPrint", "False");
                if (!commonFunctions.getPIN().equals("")) {
                    commonFunctions.logAValue("PIN", "available");
                    bottomSheetDialog.show();
                    //show popup
                } else {
                    commonFunctions.logAValue("PIN", "not available");
                    callHomeAPI();
                }

            }
        }
        viewPager = view.findViewById(R.id.viewPager);
        sliderDots = view.findViewById(R.id.SliderDots);
        //setSlider();
    }


    private void setSlider() {
        List<HomeMenu> homeMenuLists = new ArrayList<>();

        List<HomeMenuData> menuFirst = new ArrayList<>();
        menuFirst.add(new HomeMenuData(getString(R.string.pay), "PAY", R.drawable.ic_qr_scan));
        menuFirst.add(new HomeMenuData(getString(R.string.send), "SEND", R.drawable.ic_home_send));
        menuFirst.add(new HomeMenuData(getString(R.string.payme), "PAYME", R.drawable.ic_home_pay_me));
        menuFirst.add(new HomeMenuData(getString(R.string.cash_out), "CASHOUT", R.drawable.ic_home_checkout));
        homeMenuLists.add(new HomeMenu(menuFirst));

        List<HomeMenuData> menuSecond = new ArrayList<>();
        menuSecond.add(new HomeMenuData(getString(R.string.exchange), "EXCHANGE", R.drawable.ic_exchange_large));
        homeMenuLists.add(new HomeMenu(menuSecond));

        viewPagerAdapter = new ViewPagerAdapterHomeSlider(context, homeMenuLists, (position, innerPosition) -> {
            HomeMenuData homeMenuData = homeMenuLists.get(position).getHomeMenuList().get(innerPosition);
            switch (homeMenuData.getType()) {
                case "PAY":
                    if (!commonFunctions.getBasicDetails().equals("")) {
                        if (permissionUtils.checkCameraPermissionForScan(requestPermissionLauncherCamera)) {
                            Intent intent = new Intent(getActivity(), ScanAndPayActivity.class);
                            intent.putExtra("from", "pay");
                            startActivityForResult(intent, PAY_INTENT_ID);
                        }
                    }
                    break;
                case "SEND":
                    if (!commonFunctions.getBasicDetails().equals("")) {
                        Intent in = new Intent(context, ModeOfPaymentActivity.class);
                        startActivity(in);
                    }
                    break;
                case "PAYME":
                    if (!commonFunctions.getBasicDetails().equals("")) {
                        Intent in = new Intent(context, PayMeActivity.class);
                        startActivity(in);
                    }
                    break;
                case "CASHOUT":
                    if (!commonFunctions.getBasicDetails().equals("")) {
                        Intent in = new Intent(context, WhatIsCashOutActivity.class);
                        startActivity(in);
                    }
                    break;
                case "EXCHANGE":
                    int totalCurrency = Integer.parseInt(commonFunctions.getTotalCurrency());
                    if (totalCurrency == 2) {
                        Intent in = new Intent(context, ExchangeEstimateActivity.class);
                        startActivity(in);
                    } else {
                        addNewCurrencyDialog.show(getString(R.string.mobile_pay_not_available_messages));
                    }
                    break;
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDots.addView(dots[i], params);

        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    private void createSecurityDialogue() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_style_enable_sequrity, null);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCurved);
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        TextView tvEnableSecurity = bottomSheetView.findViewById(R.id.tv_button);
        TextView tvSkip = bottomSheetView.findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(view -> {
            commonFunctions.setSkipFingerPrint(true);
            bottomSheetDialog.dismiss();
            callHomeAPI();
        });
        tvEnableSecurity.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            authenticateApp();
        });

    }

    private void authenticateApp() {
        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {

                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    commonFunctions.setSequrityEnabled(true);
                    callLoginAPI();
                } else {
                    logOutIntent();
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                if (isDeviceSecure()) {
                    authenticateApp();
                } else {
                    logOutIntent();
                }
                break;
            case PAY_INTENT_ID:
                String receiverQRCode = data.getStringExtra("receiver_qr_code");
                if (receiverQRCode != null && !receiverQRCode.equals("")) {
                    if (!receiverQRCode.equals(commonFunctions.getQRCode())) {
                        getUserDetailsAPI(receiverQRCode);
                    } else {
                        errorDialog.show(getString(R.string.canot_transfer));
                    }

                }
                break;
            case REFRESH_SCREEN:
                if (resultCode == RESULT_OK) {
                    callHomeAPI();
                }
                break;
        }
    }

    private void getUserDetailsAPI(String qrCode) {
        apiCall.getUserDetailsUsingQrCode(getUserDetailsUsingQrCodeRetry, qrCode, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

                        List<CurrencyListData> currencyList = apiResponse.getCurrencyList();
                        boolean isAccountNumberAvailable = false;
                        String receiverAccountNumber = "";

                        if (currencyList != null) {
                            //check currency available or not
                            for (int i = 0; i < currencyList.size(); i++) {
                                if (commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())) {
                                    receiverAccountNumber = currencyList.get(i).getAccount_number();
                                    isAccountNumberAvailable = true;
                                    break;
                                }
                            }

                            if (isAccountNumberAvailable) {
                                apiResponse.getUserDetails().setAccount_no(receiverAccountNumber);
                                Intent in = new Intent(context, AmountEnteringActivity.class);
                                in.putExtra("data", apiResponse.getUserDetails());
                                paymentResponse.launch(in);
                            } else {
                                String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                            }
                        } else {
                            String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                            errorDialog.show(message);
                        }

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
                getUserDetailsUsingQrCodeRetry = true;
                getUserDetailsAPI(qrCode);
            }
        });
    }

    ActivityResultLauncher<Intent> paymentResponse = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            int secondsDelayed = 5;
            new Handler().postDelayed(this::callHomeAPI, secondsDelayed * 1000);
        }
    });


    private void callHomeAPI() {
        getRecentTransactions("");
        getBasicDetails("");
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
    }


    private void logOutIntent() {
        callLogOutAPI();
        commonFunctions.callReturningUserIntent("");
    }

    private void callLogOutAPI() {
        apiCall.logOutAPI(true, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    SendOTPResponse apiResponse = gson.fromJson(jsonString, SendOTPResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

                    } else {
                        // errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                   /* if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }*/
                }
            }

            @Override
            public void retry() {

            }
        });
    }

    private void callLoginAPI() {
        apiCall.loginWithPINAPI(loginWithPINAPIRetry, commonFunctions.getMobile(), commonFunctions.getCountryCode() + "", commonFunctions.getPIN(), commonFunctions.getFCMToken(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    LoginWithPINResponse apiResponse = gson.fromJson(jsonString, LoginWithPINResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setUserDetailsAfterLogin(apiResponse.getData().getAccess_token(), apiResponse.getData().getUser_details());
                        callHomeAPI();
                    } else {
                        if (apiResponse.getError_type().equals(SthiramValues.ERROR_TYPE_MULTIPLE_LOGIN)) {
                            if (!multipleLoginDialog.isShowing()) {
                                multipleLoginDialog.show();
                            }
                        } else {
                            loginErrorDialog.show(apiResponse.getMessage());
                        }
                    }
                } catch (Exception e) {
                    loginErrorDialog.show(getString(R.string.something_wrong));
                }
            }

            @Override
            public void retry() {
                loginWithPINAPIRetry = true;
                callLoginAPI();
            }
        });
    }

    private void getBasicDetails(String from) {
        if (from.equals("")) {
            if (!commonFunctions.getBasicDetails().equals("")) {
                String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getBasicDetails());
                Gson gson = new Gson();
                BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    commonFunctions.setTotalCurrency(apiResponse.getCurrencyList().size() + "");
                    commonFunctions.setUserDetailsFromBasicDetails(apiResponse.getUserDetails());
                    commonFunctions.setPaymentMethodStatus(apiResponse.getPaymentMethodsList());
                    commonFunctions.setKYCDetails(apiResponse.getKycDetails());
                    commonFunctions.setCountryDetails(apiResponse.getCountryListData());
                    commonFunctions.setAppCurrency();
                    commonFunctions.setMinAndMaximumDetails(apiResponse.getMin_max_values(), apiResponse.getUserDetails().getCurrency_id());
                    billPaymentOptionsList = apiResponse.getBillPaymentOptionsList();
                    setNotificationIcon();
                    setBillPaymentOptions();
                }
            }
        }

        apiCall.getUserBasicDetails(getUserBasicDetailsRetry, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    commonFunctions.setBasicDetails(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        String kycStatus = apiResponse.getKycDetails().getKyc_status();
                        if (kycStatus.equals(KYC_STATUS_DEACTIVATED)) {
                            logOutIntent();
                        } else {
                            if (HomeActivity.isFromNotification) {
                                if (!kycErrorShown) {
                                    if (kycStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
                                        String validationMessage = context.getString(R.string.kyc_rejected_message);
                                        kycErrorDialogue(validationMessage, context.getString(R.string.submit_again));
                                        HomeActivity.isFromNotification = true;
                                    } else if (kycStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
                                        String validationMessage = context.getString(R.string.kyc_expired_message);
                                        kycErrorDialogue(validationMessage, context.getString(R.string.upload));
                                        HomeActivity.isFromNotification = true;
                                    }
                                }
                            }
                            commonFunctions.setTotalCurrency(apiResponse.getCurrencyList().size() + "");
                            commonFunctions.setUserDetailsFromBasicDetails(apiResponse.getUserDetails());
                            commonFunctions.setPaymentMethodStatus(apiResponse.getPaymentMethodsList());
                            commonFunctions.setKYCDetails(apiResponse.getKycDetails());
                            commonFunctions.setCountryDetails(apiResponse.getCountryListData());
                            commonFunctions.setMinAndMaximumDetails(apiResponse.getMin_max_values(), apiResponse.getUserDetails().getCurrency_id());
                            billPaymentOptionsList = apiResponse.getBillPaymentOptionsList();
                            commonFunctions.setAppCurrency();
                            setNotificationIcon();
                            setBillPaymentOptions();
                        }
                    } else {
                        if (apiResponse.getMessage().contains(SthiramValues.Unauthorised)) {
                            if (!multipleLoginDialog.isShowing()) {
                                multipleLoginDialog.show();
                            }
                        } else {
                            errorDialog.show(apiResponse.getMessage());
                        }

                    }
                } catch (Exception e) {
                    Log.e("Error Basic Details", e + "");
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getUserBasicDetailsRetry = true;
                getBasicDetails("");
            }
        });
    }

    private void kycErrorDialogue(String message, String buttonLabel) {
        HomeActivity.isFromNotification = false;
        kycErrorShown = true;
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

    private void setNotificationIcon() {
        if (commonFunctions.getNotificationCount()) {
            ivGreenIcon.setVisibility(View.VISIBLE);
        } else {
            ivGreenIcon.setVisibility(View.GONE);
        }
    }


    private void getRecentTransactions(String from) {
        if (from.equals("")) {
            try {
                if (!commonFunctions.getHomeTransactionDetails().equals("")) {
                    String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getHomeTransactionDetails());
                    Gson gson = new Gson();
                    RecentTransactionResponse apiResponse = gson.fromJson(jsonString, RecentTransactionResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        recentTransactionsList = apiResponse.getResult().getRecentTransactions();
                        setRecentTransactionsList();
                    } else {
                        setNoItem();
                    }
                }
            } catch (Exception e) {

            }

        }
        apiCall.recentTransactions(recentTransactionsRetry, commonFunctions.getUserAccountNumber(), commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    commonFunctions.setHomeTransactionDetails(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    RecentTransactionResponse apiResponse = gson.fromJson(jsonString, RecentTransactionResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        recentTransactionsList = apiResponse.getResult().getRecentTransactions();
                        setRecentTransactionsList();
                    } else {
                        if (apiResponse.getMessage().contains(SthiramValues.Unauthorised)) {
                            if (!multipleLoginDialog.isShowing()) {
                                multipleLoginDialog.show();
                            }
                        } else {
                            setNoItem();
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
                recentTransactionsRetry = true;
                getRecentTransactions("");
            }
        });
    }

    private void setNoItem() {
        llNoTransactions.setVisibility(View.VISIBLE);
        llTransactions.setVisibility(View.GONE);
        shimmerFrameLayoutTwo.setVisibility(View.GONE);
        rvRecentTransactions.setVisibility(View.GONE);
    }

    private void setRecentTransactionsList() {
        llNoTransactions.setVisibility(View.GONE);
        llTransactions.setVisibility(View.VISIBLE);
        shimmerFrameLayoutTwo.setVisibility(View.GONE);
        rvRecentTransactions.setVisibility(View.VISIBLE);
        recentTransactionAdapter = new RecentTransactionAdapter(commonFunctions, false, context, recentTransactionsList, (v, position) -> {
            TransactionsRedirections.openTransactionDetailsScreen(context,commonFunctions,recentTransactionsList.get(position));
        });
        rvRecentTransactions.setAdapter(recentTransactionAdapter);
    }

    private void setBillPaymentOptions() {
        llBillPayment.setVisibility(View.VISIBLE);
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            Collections.reverse(billPaymentOptionsList);
        }
        billPaymentListAdapter = new BillPaymentListAdapter(context, billPaymentOptionsList, (v, position) -> {
            if (billPaymentOptionsList.get(position).getStatus().equals("1")) {
                Intent intent = new Intent(context, ProvidersListActivity.class);
                intent.putExtra("optionId", billPaymentOptionsList.get(position).getId());
                intent.putExtra("optionName", billPaymentOptionsList.get(position).getOption_name());
                startActivity(intent);
            } else {
                errorDialog.show(context.getString(R.string.coming_soon));
            }
        });
        rvBillPayment.setAdapter(billPaymentListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("notification"));

        if (isNotificationReceivedToRefresh) {
            getBasicDetails("push");
            getRecentTransactions("push");
            isNotificationReceivedToRefresh = false;

        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getBasicDetails("push");
            getRecentTransactions("push");
        }


    };

    //camera permission
    private ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(getActivity(), ScanAndPayActivity.class);
                    intent.putExtra("from", "pay");
                    startActivityForResult(intent, PAY_INTENT_ID);
                }
            });


}