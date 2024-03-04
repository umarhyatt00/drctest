package com.yeel.drc.activity.main.homepayandsend;

import static com.yeel.drc.utils.SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.QuickPayListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.quicklinklist.QuickLinkListResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class SendViaYeelActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean quickPay = false;
    boolean getUserDetailsUsingQrCodeRetry = false;
    List<TransactionsData> listMain, list;
    ProgressBar progressBar;
    LinearLayout llMain;
    LinearLayout llQuickpay;
    RecyclerView rvList;
    GridLayoutManager gridLayoutManager;
    QuickPayListAdapter adapter;
    RelativeLayout rlScanPay;
    public static final int PAY_INTENT_ID = 101;
    List<String> idList;

    PermissionUtils permissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_via_yeel);
        setToolBar();
        context = SendViaYeelActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlScanPay.setOnClickListener(view -> {
            if(permissionUtils.checkCameraPermissionForScan(requestPermissionLauncherCamera)){
                Intent intent = new Intent(context, ScanAndPayActivity.class);
                intent.putExtra("from", "pay");
                startActivityForResult(intent, PAY_INTENT_ID);
            }
        });
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        llMain = findViewById(R.id.ll_main);
        llMain.setVisibility(View.GONE);
        llQuickpay = findViewById(R.id.ll_quick_pay);
        llQuickpay.setVisibility(View.GONE);
        rvList = findViewById(R.id.rv_list);
        gridLayoutManager = new GridLayoutManager(context, 4);
        rvList.setLayoutManager(gridLayoutManager);
        rlScanPay = findViewById(R.id.rl_scan_pay);
        getQuickPayList();
    }

    private void getQuickPayList() {
        apiCall.quickLinkTransList(quickPay, commonFunctions.getUserAccountNumber(), commonFunctions.getUserId(), TRANSACTION_TYPE_YEEL_ACCOUNT, false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    QuickLinkListResponse apiResponse = gson.fromJson(jsonString, QuickLinkListResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        listMain = apiResponse.getResult().getQuickLinkList();
                        idList = new ArrayList<>();
                        list = new ArrayList<>();
                        for (int i = 0; i < listMain.size(); i++) {
                            String receiverId = listMain.get(i).getReceiver_id();
                            if (!idList.contains(receiverId)) {
                                idList.add(receiverId);
                                list.add(listMain.get(i));
                            }
                        }
                        setList();
                    } else {
                        llMain.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        list = new ArrayList<>();
                        setList();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                quickPay = true;
                getQuickPayList();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                                startActivity(in);
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

    private void setList() {
        progressBar.setVisibility(View.GONE);
        llMain.setVisibility(View.VISIBLE);
        if (list.size() > 6) {
            list = new ArrayList<>(list.subList(0, 7));
        } else if (list.size() == 0) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            TransactionsData four = new TransactionsData("");
            TransactionsData five = new TransactionsData("");
            TransactionsData six = new TransactionsData("");
            TransactionsData seven = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
            list.add(four);
            list.add(five);
            list.add(six);
            list.add(seven);
        } else if (list.size() == 1) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            TransactionsData four = new TransactionsData("");
            TransactionsData five = new TransactionsData("");
            TransactionsData six = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
            list.add(four);
            list.add(five);
            list.add(six);
        } else if (list.size() == 2) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            TransactionsData four = new TransactionsData("");
            TransactionsData five = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
            list.add(four);
            list.add(five);

        } else if (list.size() == 3) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            TransactionsData four = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
            list.add(four);

        } else if (list.size() == 4) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
        } else if (list.size() == 5) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            list.add(one);
            list.add(two);
        } else if (list.size() == 6) {
            TransactionsData one = new TransactionsData("");
            list.add(one);
        }
        llQuickpay.setVisibility(View.VISIBLE);
        adapter = new QuickPayListAdapter(list, context, (v, position, from) -> {
            if (from.equals("last")) {
                if (permissionUtils.checkContactPermission(requestPermissionLauncherContact)) {
                    Intent in = new Intent(context, MyContactListActivity.class);
                    in.putExtra("from", "send");
                    startActivity(in);
                }
            } else {
                String  type="";
                if(list.get(position).getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)){
                    type= SthiramValues.ACCOUNT_TYPE_BUSINESS;
                }else{
                    type= SthiramValues.ACCOUNT_TYPE_INDIVIDUAL;
                }
                UserDetailsData userDetails = new UserDetailsData(
                        list.get(position).getReceiver_name(),
                        list.get(position).getProfile_image(),
                        list.get(position).getReceiver_account_no() + "",
                        list.get(position).getReceiver_id() + "",
                        type
                );
                Intent in = new Intent(context, AmountEnteringActivity.class);
                in.putExtra("from", "SendAgain");
                in.putExtra("data", userDetails);
                startActivity(in);
            }
        });
        rvList.setAdapter(adapter);

    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.send_via_yeel);
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

    //contact permission
    private final ActivityResultLauncher<String> requestPermissionLauncherContact =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent in = new Intent(context, MyContactListActivity.class);
                    in.putExtra("from", "send");
                    startActivity(in);
                }
            });

    //camera permission
    private final ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Intent intent = new Intent(context, ScanAndPayActivity.class);
                    intent.putExtra("from", "pay");
                    startActivityForResult(intent, PAY_INTENT_ID);
                }
            });

}