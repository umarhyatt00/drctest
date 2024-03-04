package com.yeel.drc.activity.main.fragments.agent;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.ModeOfPaymentActivity;
import com.yeel.drc.activity.main.homepayandsend.AmountEnteringActivity;
import com.yeel.drc.activity.main.homepayandsend.ScanAndPayActivity;
import com.yeel.drc.activity.main.payme.PayMeActivity;
import com.yeel.drc.activity.main.settings.SettingsActivity;
import com.yeel.drc.adapter.DashboardFilterAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.agentdashboard.AgentDashBoardData;
import com.yeel.drc.api.agentdashboard.AgentDashBoardResponse;
import com.yeel.drc.api.basicdetails.BasicDetailsResponse;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.login.LoginWithPINResponse;
import com.yeel.drc.api.sendotp.SendOTPResponse;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.TransactionDateTypeModel;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class AgentHomeFragment extends Fragment {
    View view;
    Context context;
    ImageView ivSettings;
    ImageView ivLogout;
    LinearLayout llTransfer;
    LinearLayout llSend;
    LinearLayout llPayMe;
    TextView tvAgentName;
    TextView tvBalance;
    public static final int PAY_INTENT_ID = 101;
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

    CommonFunctions commonFunctions;
    ErrorDialog errorDialog,errorDialogDismiss;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    boolean getUserDetailsUsingQrCodeRetry = false;
    boolean getUserBasicDetailsRetry = false;
    boolean getUserDashBoardDetailsRetry = false;


    TextView tvCashOutAmount;
    TextView tvCashInAmount;
    TextView tvTotalTransactions;
    TextView tvRemittanceAmount;

    TextView tvRemittanceCommission;
    TextView tvCashInCommission;
    TextView tvCashOutCommission;
    TextView tvTotalCommission;


    LinearLayout llDashBoard;
    AgentDashBoardData agentDashBoardData;

    ShimmerFrameLayout shimmerFrameLayoutOne;
    ShimmerFrameLayout shimmerFrameLayoutTwo;
    LinearLayout llBalanceLoader;
    LinearLayout llBalance;
    LinearLayout llAgentDashBoardLoader;
    LinearLayout llFilter;
    TextView tvFilterValue;
    TextView tvCurrencySymbol;


    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    private ImageView ivClose;
    private RecyclerView mRecyclerTransactionDate;
    private LinearLayoutManager mLayoutManagerFilter;
    List<TransactionDateTypeModel> mTransactionDateTypeList;
    String mSelected;
    DashboardFilterAdapter dashboardFilterAdapter;


    View bottomSheetViewSequrity;
    BottomSheetDialog bottomSheetDialogSequrity;
    BottomSheetBehavior bottomSheetBehaviorSequrity;
    boolean loginWithPINAPIRetry = false;
    SwipeRefreshLayout swipeRefreshLayout;

    PermissionUtils permissionUtils;
    MultipleLoginDialog multipleLoginDialog;



    public AgentHomeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AgentHomeFragment newInstance(String param1, String param2) {
        AgentHomeFragment fragment = new AgentHomeFragment();
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
        view = inflater.inflate(R.layout.fragment_agent_home, container, false);
        context = getContext();
        initView();
        setItemListeners();
        return view;
    }

    private void initView() {
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        errorDialogDismiss = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        ivSettings = view.findViewById(R.id.iv_settings);
        ivLogout = view.findViewById(R.id.iv_logout);
        llTransfer = view.findViewById(R.id.ll_transfer);
        llSend = view.findViewById(R.id.ll_send_btn);
        llPayMe = view.findViewById(R.id.ll_pay_me);
        tvAgentName = view.findViewById(R.id.tv_agent_name);
        tvBalance = view.findViewById(R.id.tv_show_balance_account_summary);
        tvCurrencySymbol=view.findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        swipeRefreshLayout=view.findViewById(R.id.swipe_to_refresh);

        tvCashOutAmount = view.findViewById(R.id.tv_cash_out_amount);
        tvRemittanceAmount = view.findViewById(R.id.tv_remittance_amount);
        tvCashInAmount = view.findViewById(R.id.tv_cash_in_amount);
        tvTotalTransactions = view.findViewById(R.id.tv_total_transactions);

        tvCashOutCommission = view.findViewById(R.id.tv_cash_out_commission);
        tvRemittanceCommission = view.findViewById(R.id.tv_remittance_commission);
        tvCashInCommission = view.findViewById(R.id.tv_cash_in_commission);
        tvTotalCommission = view.findViewById(R.id.tv_total_commission);


        llDashBoard = view.findViewById(R.id.ll_dash_board);
        llDashBoard.setVisibility(View.GONE);
        llFilter = view.findViewById(R.id.ll_txn);
        tvFilterValue = view.findViewById(R.id.filter_txn);
        tvFilterValue.setText(getString(R.string.last) + " 28 " + getString(R.string.days));

        shimmerFrameLayoutOne = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutTwo = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_two);
        shimmerFrameLayoutOne.startShimmer();
        shimmerFrameLayoutTwo.startShimmer();
        llBalanceLoader = view.findViewById(R.id.ll_balance_loader);
        llBalance = view.findViewById(R.id.ll_balance);
        llBalance.setVisibility(View.GONE);
        llAgentDashBoardLoader = view.findViewById(R.id.ll_agent_dash_board_loader);



        createFilterOptions();
        createSecurityDialogue();
        if (commonFunctions.isSequrityEnabled()) {

            if (commonFunctions.getSecurityShownStatus()) {
                //security already shown for session
                callHomeAPI();
            } else {
                authenticateApp();
            }
        } else {
            if (commonFunctions.isSkipFingerPrint()) {
                //Finger print option already skipped
                callHomeAPI();
            } else {
                if (!commonFunctions.getPIN().equals("")) {
                    bottomSheetDialogSequrity.show();
                    //show popup
                } else {
                    callHomeAPI();
                }

            }
        }

    }

    private void createSecurityDialogue() {
        bottomSheetViewSequrity = getLayoutInflater().inflate(R.layout.dialog_style_enable_sequrity, null);
        bottomSheetDialogSequrity = new BottomSheetDialog(context,R.style.BottomSheetDialogCurved);
        bottomSheetDialogSequrity.setContentView(bottomSheetViewSequrity);
        BottomSheetBehavior.from((View) bottomSheetViewSequrity.getParent());
        bottomSheetDialogSequrity.setCanceledOnTouchOutside(false);
        bottomSheetDialogSequrity.setCancelable(false);
        TextView tvEnableSecurity = bottomSheetViewSequrity.findViewById(R.id.tv_button);
        TextView tvSkip = bottomSheetViewSequrity.findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.setSkipFingerPrint(true);
                bottomSheetDialogSequrity.dismiss();
                callHomeAPI();
            }
        });
        tvEnableSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialogSequrity.dismiss();
                authenticateApp();
            }
        });

    }

    private void callHomeAPI() {
        getBasicDetails("");
        getAgentDashBoard("", mSelected);
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


    private void createFilterOptions() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_style_transaction_date, null);
        bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetDialogCurved);
        ivClose = bottomSheetView.findViewById(R.id.iv_close);
        bottomSheetDialog.setContentView(bottomSheetView);
        mRecyclerTransactionDate = bottomSheetView.findViewById(R.id.rvTxnDate);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        mLayoutManagerFilter = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerTransactionDate.setLayoutManager(mLayoutManagerFilter);
        mTransactionDateTypeList = new ArrayList<>();

        String last = getString(R.string.last);
        String days = getString(R.string.days);
        mTransactionDateTypeList.add(new TransactionDateTypeModel(getString(R.string.today), "1", false));
        mTransactionDateTypeList.add(new TransactionDateTypeModel(last + " 7 " + days, "7", false));
        mTransactionDateTypeList.add(new TransactionDateTypeModel(last + " 14 " + days, "14", false));
        mTransactionDateTypeList.add(new TransactionDateTypeModel(last + " 28 " + days, "28", true));
        mTransactionDateTypeList.add(new TransactionDateTypeModel(getString(R.string.last_year), commonFunctions.dateDifference()+"", false));
        mSelected = "28";
        dashboardFilterAdapter = new DashboardFilterAdapter(mTransactionDateTypeList, context, new DashboardFilterAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                for (int i = 0; i < mTransactionDateTypeList.size(); i++) {
                    if (i == position) {
                        mSelected = mTransactionDateTypeList.get(i).getTransactionTYpeValue();
                        tvFilterValue.setText(mTransactionDateTypeList.get(i).getTransactionType());
                        mTransactionDateTypeList.get(i).setSelected(true);
                        getAgentDashBoard("filter", mSelected);
                        bottomSheetDialog.dismiss();
                    } else {
                        mTransactionDateTypeList.get(i).setSelected(false);
                    }
                }
                dashboardFilterAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerTransactionDate.setAdapter(dashboardFilterAdapter);

    }

    private void setValues() {
        llBalance.setVisibility(View.VISIBLE);
        llBalanceLoader.setVisibility(View.GONE);


        tvBalance.setText(commonFunctions.setAmount(commonFunctions.getUserAccountBalance()));


        if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS_AGENT)) {
            tvAgentName.setText(commonFunctions.getBusinessName());
        } else {
            tvAgentName.setText(commonFunctions.getFullName());
        }


    }

    private void setItemListeners() {
        ivSettings.setOnClickListener(view1 -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                Intent in = new Intent(context, SettingsActivity.class);
                startActivity(in);
            }

        });
        ivLogout.setOnClickListener(view1 -> {
            logOutIntent();
        });
        llTransfer.setOnClickListener(view1 -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(permissionUtils.checkCameraPermissionForScan(requestPermissionLauncherCamera)){
                    Intent intent = new Intent(getActivity(), ScanAndPayActivity.class);
                    intent.putExtra("from", "pay");
                    startActivityForResult(intent, PAY_INTENT_ID);
                }
            }

        });
        llSend.setOnClickListener(view1 -> {
            Intent in = new Intent(context, ModeOfPaymentActivity.class);
            startActivity(in);
           // errorDialogDismiss.show("Coming Soon");
        });
        llPayMe.setOnClickListener(view1 -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                Intent in = new Intent(context, PayMeActivity.class);
                startActivity(in);
            }

        });
        llFilter.setOnClickListener(v -> bottomSheetDialog.show());
        ivClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callHomeAPI();
            }
        });
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
        }
    }

    private void callLoginAPI() {
        apiCall.loginWithPINAPI(loginWithPINAPIRetry, commonFunctions.getMobile(),commonFunctions.getCountryCode(), commonFunctions.getPIN(), commonFunctions.getFCMToken(), true, new CustomCallback() {
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
                        if(apiResponse.getError_type().equals(SthiramValues.ERROR_TYPE_MULTIPLE_LOGIN)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            errorDialog.show(apiResponse.getMessage());
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
                loginWithPINAPIRetry = true;
                callLoginAPI();
            }
        });
    }

    private void logOutIntent() {
        callLogOutAPI();
        commonFunctions.callReturningUserIntent("");
    }

    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();
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
                        List<CurrencyListData> currencyList=apiResponse.getCurrencyList();
                        boolean isAccountNumberAvailable = false;
                        String receiverAccountNumber="";

                        if(currencyList!=null){
                            //check currency available or not
                            for(int i=0;i<currencyList.size();i++){
                                if(commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())){
                                    receiverAccountNumber=currencyList.get(i).getAccount_number();
                                    isAccountNumberAvailable=true;
                                    break;
                                }
                            }

                            if(isAccountNumberAvailable){
                                apiResponse.getUserDetails().setAccount_no(receiverAccountNumber);
                                Intent in = new Intent(context, AmountEnteringActivity.class);
                                in.putExtra("data",apiResponse.getUserDetails());
                                startActivity(in);
                            }else{
                                String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                            }

                        }else{
                            String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                            errorDialog.show(message);
                        }

                    } else {
                        if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            errorDialog.show(apiResponse.getMessage());
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
                getUserDetailsUsingQrCodeRetry = true;
                getUserDetailsAPI(qrCode);
            }
        });
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

    private void getBasicDetails(String from) {
        if (from.equals("")) {
            if (!commonFunctions.getBasicDetails().equals("")) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getBasicDetails());
                    Gson gson = new Gson();
                    BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setTotalCurrency(apiResponse.getCurrencyList().size()+"");
                        commonFunctions.setUserDetailsFromBasicDetails(apiResponse.getUserDetails());
                        commonFunctions.setPaymentMethodStatus(apiResponse.getPaymentMethodsList());
                        commonFunctions.setMinAndMaximumDetails(apiResponse.getMin_max_values(),apiResponse.getUserDetails().getCurrency_id());
                        commonFunctions.setKYCDetails(apiResponse.getKycDetails());
                        commonFunctions.setCountryDetails(apiResponse.getCountryListData());
                        commonFunctions.setAppCurrency();
                        setValues();
                    }
                }catch (Exception e){

                }

            }
        }
        apiCall.getUserBasicDetails(getUserBasicDetailsRetry, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    commonFunctions.setBasicDetails(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setTotalCurrency(apiResponse.getCurrencyList().size()+"");
                        commonFunctions.setUserDetailsFromBasicDetails(apiResponse.getUserDetails());
                        commonFunctions.setPaymentMethodStatus(apiResponse.getPaymentMethodsList());
                        commonFunctions.setKYCDetails(apiResponse.getKycDetails());
                        commonFunctions.setCountryDetails(apiResponse.getCountryListData());
                        commonFunctions.setMinAndMaximumDetails(apiResponse.getMin_max_values(),apiResponse.getUserDetails().getCurrency_id());
                        commonFunctions.setAppCurrency();
                        setValues();
                    } else {
                        if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            errorDialog.show(apiResponse.getMessage());
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
                getUserBasicDetailsRetry = true;
                getBasicDetails("");
            }
        });
    }


    private void getAgentDashBoard(String from, String numberOfDays) {
        boolean loader = false;
        if (from.equals("")) {
            if (!commonFunctions.getAgentDashBoard().equals("")) {
                String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getAgentDashBoard());
                Gson gson = new Gson();
                AgentDashBoardResponse apiResponse = gson.fromJson(jsonString, AgentDashBoardResponse.class);
                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    agentDashBoardData = apiResponse.getAgentDashBoardData();
                    setAgentDashBoard();
                }
            }
        } else {
            loader = true;
        }
        apiCall.getAgentDashBoard(getUserDashBoardDetailsRetry, commonFunctions.getUserId(), commonFunctions.getUserAccountNumber(), numberOfDays, loader, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    commonFunctions.setAgentDashBoard(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AgentDashBoardResponse apiResponse = gson.fromJson(jsonString, AgentDashBoardResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        agentDashBoardData = apiResponse.getAgentDashBoardData();
                        setAgentDashBoard();
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
                getUserDashBoardDetailsRetry = true;
                getAgentDashBoard("", numberOfDays);
            }
        });
    }

    private void setAgentDashBoard() {
        llDashBoard.setVisibility(View.VISIBLE);
        llAgentDashBoardLoader.setVisibility(View.GONE);


        tvCashOutAmount.setText(commonFunctions.setAmount(agentDashBoardData.getTotalCashOut()));
        tvCashOutCommission.setText(commonFunctions.setAmount(agentDashBoardData.getTotalCashOutCommission()));

        tvRemittanceAmount.setText(commonFunctions.setAmount(agentDashBoardData.getTotalRemittances()));
        tvRemittanceCommission.setText(commonFunctions.setAmount(agentDashBoardData.getTotalRemittancesCommission()));

        tvCashInAmount.setText(commonFunctions.setAmount(agentDashBoardData.getTotalCashIn()));
        tvCashInCommission.setText(commonFunctions.setAmount(agentDashBoardData.getTotalCashInCommission()));

        tvTotalTransactions.setText(commonFunctions.setAmount(agentDashBoardData.getTotalTransactions()));
        tvTotalCommission.setText(commonFunctions.setAmount(agentDashBoardData.getTotalCommission()));

        commonFunctions.setTotalCommission(agentDashBoardData.getAgentTotalCommission());
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("notification"));

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callHomeAPI();
        }


    };


    private ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(getActivity(), ScanAndPayActivity.class);
                    intent.putExtra("from", "pay");
                    startActivityForResult(intent, PAY_INTENT_ID);
                }
            });
}