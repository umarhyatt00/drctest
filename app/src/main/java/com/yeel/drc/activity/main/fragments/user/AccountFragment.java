package com.yeel.drc.activity.main.fragments.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.activity.main.agent.addfund.AddFundsActivity;
import com.yeel.drc.activity.main.exchange.ExchangeEstimateActivity;
import com.yeel.drc.adapter.AccountSummaryListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.accountsummary.AccountSummaryData;
import com.yeel.drc.api.accountsummary.AccountSummaryResponse;
import com.yeel.drc.api.basicdetails.BasicDetailsResponse;
import com.yeel.drc.dialogboxes.AddNewCurrencyDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;


public class AccountFragment extends Fragment {
    View view;
    Context context;
    TextView tvBalance;
    CommonFunctions commonFunctions;
    APICall apiCall;
    ErrorDialog errorDialog,errorDialogDismiss;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TextView tvCurrencySymbol;
    MultipleLoginDialog multipleLoginDialog;
    AddNewCurrencyDialog addNewCurrencyDialog;


    TextView tvNoItem;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    TextView tvNote;
    TextView tvShowAccountNumber;
    LinearLayout llAddFunds, llExchange;
    boolean show = false;

    AccountSummaryListAdapter accountSummaryListAdapter;
    List<AccountSummaryData> accountSummaryList;
    String fullAccountNumber;
    String smallAccountNumber;
    boolean getUserBasicDetailsRetry = false;
    boolean getAccountSummaryRetry = false;

    ShimmerFrameLayout shimmerFrameLayoutOne;

    int limit = 10;
    int offset = 0;
    boolean hasMore = false;
    NestedScrollView nestedScrollView;

    public AccountFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        view = inflater.inflate(R.layout.fragment_account, container, false);
        context = getContext();
        show = false;
        initView();
        setItemListeners();
        return view;
    }


    private void initView() {
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        errorDialogDismiss = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        tvBalance = view.findViewById(R.id.tv_balance);
        addNewCurrencyDialog=new AddNewCurrencyDialog(context,commonFunctions);

        tvNoItem = view.findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        rvList = view.findViewById(R.id.recycler_accounts);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        tvNote = view.findViewById(R.id.tv_note);
        tvNote.setVisibility(View.GONE);
        tvShowAccountNumber = view.findViewById(R.id.show_account_number);
        llAddFunds = view.findViewById(R.id.ll_add_fund);
        llExchange = view.findViewById(R.id.ll_exchange);
        tvCurrencySymbol = view.findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        nestedScrollView = view.findViewById(R.id.nested_scroll);

        shimmerFrameLayoutOne = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutOne.startShimmer();


        setValues();
        getTransactionList("");
        llAddFunds.setOnClickListener(view -> {
            Intent in = new Intent(context, AddFundsActivity.class);
            startActivity(in);
        });
        llExchange.setOnClickListener(view -> {
            String exchangeStatus=commonFunctions.getExchangeStatus();
            switch (exchangeStatus) {
                case "1":
                    int totalCurrency = Integer.parseInt(commonFunctions.getTotalCurrency());
                    if (totalCurrency == 2) {
                        if(commonFunctions.checkExchangePossible().equals(SthiramValues.SUCCESS)){
                            Intent in = new Intent(context, ExchangeEstimateActivity.class);
                            startActivity(in);
                        }
                    }else{
                        addNewCurrencyDialog.show(getString(R.string.ssp_account_not_found));
                    }
                    break;
                case "0":
                    somethingWentWrongDialog.show();
                    break;
                case "2":
                    errorDialogDismiss.show(getString(R.string.coming_soon));
                    break;
            }
        });
    }

    private void getTransactionList(String from) {
        //get from local
        if (from.equals("")) {
            if (!commonFunctions.getAccountTransactionDetails().equals("")) {
                String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getAccountTransactionDetails());
                Gson gson = new Gson();
                AccountSummaryResponse apiResponse = gson.fromJson(jsonString, AccountSummaryResponse.class);
                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    if (apiResponse.getAccountSummaryList().size() > 20) {
                        accountSummaryList = apiResponse.getAccountSummaryList().subList(0, 20);
                    } else {
                        accountSummaryList = apiResponse.getAccountSummaryList();
                    }
                    setList(0);
                } else {
                    if (apiResponse.getMessage().contains(SthiramValues.Unauthorised)) {
                        if (!multipleLoginDialog.isShowing()) {
                            multipleLoginDialog.show();
                        }
                    } else {
                        setNoItem();
                    }
                }
            }
        }

        offset = 0;
        apiCall.getAccountSummary(getAccountSummaryRetry, commonFunctions.getUserId(), commonFunctions.getUserAccountNumber(), offset + "", limit + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    commonFunctions.setAccountTransactionDetails(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AccountSummaryResponse apiResponse = gson.fromJson(jsonString, AccountSummaryResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        accountSummaryList = apiResponse.getAccountSummaryList();
                        if (accountSummaryList != null && accountSummaryList.size() != 0) {
                            setTransactions();
                        } else {
                            setNoItem();
                        }
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
                getAccountSummaryRetry = true;
                getTransactionList("");
            }
        });
    }


    private void getTransactionsRemain() {
        apiCall.getAccountSummary(getAccountSummaryRetry, commonFunctions.getUserId(), commonFunctions.getUserAccountNumber(), offset + "", limit + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AccountSummaryResponse apiResponse = gson.fromJson(jsonString, AccountSummaryResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        List<AccountSummaryData> accountSummaryListRemain = apiResponse.getAccountSummaryList();
                        if (accountSummaryListRemain != null && accountSummaryListRemain.size() != 0) {
                            offset = offset + limit;
                            accountSummaryList.addAll(accountSummaryListRemain);
                            hasMore = true;
                            accountSummaryListAdapter.notifyDataSetChanged();
                        } else {
                            hasMore = false;
                            setList(accountSummaryList.size() - 1);
                        }
                    } else {
                        if (apiResponse.getMessage().contains(SthiramValues.Unauthorised)) {
                            if (!multipleLoginDialog.isShowing()) {
                                multipleLoginDialog.show();
                            }
                        } else {
                            hasMore = false;
                            setList(accountSummaryList.size() - 1);
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
                getAccountSummaryRetry = true;
                getTransactionsRemain();
            }
        });
    }

    private void setTransactions() {
        if (accountSummaryList.size() < limit) {
            hasMore = false;
            setList(0);
        } else {
            offset = offset + limit;
            hasMore = true;
            setList(0);
        }
    }

    private void setNoItem() {
        tvNoItem.setVisibility(View.VISIBLE);
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        tvNote.setVisibility(View.GONE);
    }

    private void setList(int position) {
        if (accountSummaryList != null && accountSummaryList.size() != 0) {
            tvNote.setVisibility(View.VISIBLE);
            shimmerFrameLayoutOne.setVisibility(View.GONE);
            tvNoItem.setVisibility(View.GONE);
            accountSummaryListAdapter = new AccountSummaryListAdapter(commonFunctions, hasMore, context, accountSummaryList, new AccountSummaryListAdapter.ItemClickAdapterListener() {
                @Override
                public void itemClick(View v, int position) {

                }
            });
            rvList.setAdapter(accountSummaryListAdapter);
        } else {
            setNoItem();
        }

    }

    private void setValues() {
        tvBalance.setText(commonFunctions.setAmount(commonFunctions.getUserAccountBalance()));
        try {
            fullAccountNumber = commonFunctions.getUserAccountNumber();
            smallAccountNumber = "XXXX" + fullAccountNumber.substring(fullAccountNumber.length() - 4);
            tvShowAccountNumber.setText(smallAccountNumber);
        } catch (Exception e) {
            tvShowAccountNumber.setText("XXXX");
        }

        //add funds button only for agents
        if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
            llAddFunds.setVisibility(View.VISIBLE);
            llExchange.setVisibility(View.GONE);
        } else if (SthiramValues.SELECTED_CURRENCY_ID.equals("2")) {
            llAddFunds.setVisibility(View.GONE);
           llExchange.setVisibility(View.VISIBLE);
        } else {
            llAddFunds.setVisibility(View.GONE);
            llExchange.setVisibility(View.GONE);
        }
    }

    private void setItemListeners() {
        tvShowAccountNumber.setOnClickListener(view -> {
            if (show) {
                show = false;
                Drawable img = getResources().getDrawable(R.drawable.ic_show_account);
                tvShowAccountNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                tvShowAccountNumber.setText(smallAccountNumber);
            } else {
                Intent in = new Intent(getActivity(), PINVerificationActivity.class);
                startActivityForResult(in, 120);
            }

        });
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (v.getChildAt(0).getBottom() <= (nestedScrollView.getHeight() + scrollY)) {
                    if (hasMore) {
                        hasMore = false;
                        getAccountSummaryRetry = false;
                        getTransactionsRemain();
                    }
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 120) {
                show = true;
                Drawable img = getResources().getDrawable(R.drawable.ic_hide_account);
                tvShowAccountNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                tvShowAccountNumber.setText(fullAccountNumber);
            }
        }
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
            getBasicDetails();
            getTransactionList("push");
        }
    };

    private void getBasicDetails() {
        apiCall.getUserBasicDetails(getUserBasicDetailsRetry, commonFunctions.getUserId(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setUserDetailsFromBasicDetails(apiResponse.getUserDetails());
                        commonFunctions.setPaymentMethodStatus(apiResponse.getPaymentMethodsList());
                        commonFunctions.setKYCDetails(apiResponse.getKycDetails());
                        commonFunctions.setCountryDetails(apiResponse.getCountryListData());
                        commonFunctions.setMinAndMaximumDetails(apiResponse.getMin_max_values(), apiResponse.getUserDetails().getCurrency_id());
                        commonFunctions.setAppCurrency();
                        setValues();
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
                getUserBasicDetailsRetry = true;
                getBasicDetails();
            }
        });
    }
}