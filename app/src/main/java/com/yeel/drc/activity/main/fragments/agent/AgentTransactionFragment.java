package com.yeel.drc.activity.main.fragments.agent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.exchange.ExchangeCreditReceiptActivity;
import com.yeel.drc.activity.main.exchange.ExchangeDebitReceiptActivity;
import com.yeel.drc.activity.main.receipt.credit.MoneyRecivedReceiptActivity;
import com.yeel.drc.activity.main.receipt.debit.AgentCashPickupDebitActivity;
import com.yeel.drc.activity.main.receipt.debit.AgentMobilePayDebitReceiptActivity;
import com.yeel.drc.activity.main.receipt.debit.AgentSendViaYeelDebitReceipt;
import com.yeel.drc.activity.main.receipt.debit.MoneySentReceiptActivity;
import com.yeel.drc.adapter.AgentAllTransactionsAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.alltransactions.AllTransactionsResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class AgentTransactionFragment extends Fragment {
    AgentAllTransactionsAdapter allTransactionsAdapter;

    private View view;
    private Context context;
    TextView tvNoItem;
    RecyclerView rvList;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    MultipleLoginDialog multipleLoginDialog;

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CommonFunctions commonFunctions;
    int offset=0;

    List<TransactionsData> allTransactionsList,allTransactionsListRemain;
    boolean hasMore=false;
    boolean getTransactionsByTypeRetry=false;
    boolean getTransactionsByTypeRetryRemain=false;

    ShimmerFrameLayout shimmerFrameLayoutOne;
    List<String> dateList;
    int limit=15;


    public AgentTransactionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AgentTransactionFragment newInstance(String param1, String param2) {
        AgentTransactionFragment fragment = new AgentTransactionFragment();
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
        view=inflater.inflate(R.layout.fragment_agent_transaction, container, false);
        context = getActivity();
        initView();
        setItemListeners();
        return view;
    }

    private void setItemListeners() {
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    if (hasMore) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            hasMore = false;
                            getTransactionsByTypeRetryRemain=false;
                            getTransactionsRemain();
                        }
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactions("swipe");
            }
        });
    }

    private void initView() {
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
        errorDialog=new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        commonFunctions=new CommonFunctions(context);
        tvNoItem=view.findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        rvList=view.findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        shimmerFrameLayoutOne = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutOne.startShimmer();
        getTransactions("");
    }

    private void getTransactions(String from) {
        if(from.equals("")){
            if(!commonFunctions.getAllTransactionsAgent().equals("")){
                String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getAllTransactionsAgent());
                Gson gson = new Gson();
                AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    allTransactionsList=apiResponse.getList();
                    if (allTransactionsList != null && allTransactionsList.size() != 0) {
                        setTransactions();
                    } else {
                        tvNoItem.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
        offset=0;
        apiCall.getTransactionsByType(getTransactionsByTypeRetry,commonFunctions.getUserAccountNumber(),limit+"",commonFunctions.getUserId(),"all",offset+"",false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try{
                    commonFunctions.setAllTransactionsAgent(response.getKuttoosan());
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        allTransactionsList=apiResponse.getList();
                        if (allTransactionsList != null && allTransactionsList.size() != 0) {
                            setTransactions();
                        } else {
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            shimmerFrameLayoutOne.setVisibility(View.GONE);
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getTransactionsByTypeRetry=true;
                getTransactions("");
            }
        });
    }

    private void getTransactionsRemain() {
        apiCall.getTransactionsByType(getTransactionsByTypeRetryRemain,commonFunctions.getUserAccountNumber(),limit+"",commonFunctions.getUserId(),"all",offset+"",false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        allTransactionsListRemain =apiResponse.getList();
                        if(allTransactionsListRemain !=null&& allTransactionsListRemain.size()!=0){
                            offset=offset+limit;
                            allTransactionsList.addAll(allTransactionsListRemain);
                            hasMore=true;
                            dateList=new ArrayList<>();
                            for(int i=0;i<allTransactionsList.size();i++){
                                String date = commonFunctions.getGalilioDateFormat(allTransactionsList.get(i).getTransaction_date(), "date");
                                if(!dateList.contains(date)){
                                    dateList.add(date);
                                    allTransactionsList.get(i).setShowHeading(true);
                                }else{
                                    allTransactionsList.get(i).setShowHeading(false);
                                }
                            }
                            allTransactionsAdapter.notifyDataSetChanged();
                        }else{
                            setList(allTransactionsList.size()-1);
                        }
                    }else{
                        setList(allTransactionsList.size()-1);
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getTransactionsByTypeRetryRemain=true;
                getTransactionsRemain();
            }
        });
    }


    private void setTransactions() {
        dateList=new ArrayList<>();
        for(int i=0;i<allTransactionsList.size();i++){
            String date = commonFunctions.getGalilioDateFormat(allTransactionsList.get(i).getTransaction_date(), "date");
            if(!dateList.contains(date)){
                dateList.add(date);
                allTransactionsList.get(i).setShowHeading(true);
            }else{
                allTransactionsList.get(i).setShowHeading(false);
            }
        }
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        if (allTransactionsList.size() < limit) {
            hasMore = false;
            setList(0);
        } else {
            offset=offset+limit;
            hasMore = true;
            setList(0);
        }
    }

    private void setList(int i) {
        tvNoItem.setVisibility(View.GONE);
        allTransactionsAdapter=new AgentAllTransactionsAdapter(commonFunctions, hasMore, context, allTransactionsList, new AgentAllTransactionsAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {

                String transactionTYpe = allTransactionsList.get(position).getTransaction_type();
                if(transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_EXCHANGE)){
                    Intent in;
                    if(SthiramValues.SELECTED_CURRENCY_SYMBOL.equals("SSP")){
                        //credit
                        in = new Intent(context, ExchangeCreditReceiptActivity.class);
                    }else{
                        //debit
                        in = new Intent(context, ExchangeDebitReceiptActivity.class);
                    }
                    in.putExtra("ydb_ref_id", allTransactionsList.get(position).getYdb_ref_id());
                    startActivity(in);
                }else{
                    if (commonFunctions.getUserId().equals(allTransactionsList.get(position).getSender_id())) {
                        //debit
                        if (transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL)) {
                            Intent in = new Intent(context, AgentSendViaYeelDebitReceipt.class);
                            in.putExtra("ydb_ref_id", allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        } else if (transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)) {
                            Intent in = new Intent(context, AgentCashPickupDebitActivity.class);
                            in.putExtra("ydb_ref_id", allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        }else  if(transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_MPESA_AGENT)){
                            Intent in = new Intent(context, AgentMobilePayDebitReceiptActivity.class);
                            in.putExtra("ydb_ref_id", allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        }else  if(transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_AIRTEL_UGANDA_AGENT)){
                            Intent in = new Intent(context, AgentMobilePayDebitReceiptActivity.class);
                            in.putExtra("ydb_ref_id", allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        }else if(transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT)||transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)){
                            Intent in=new Intent(context, MoneySentReceiptActivity.class);
                            in.putExtra("ydb_ref_id",allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        }
                    } else {
                        if(transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT)||transactionTYpe.equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)){
                            Intent in=new Intent(context, MoneyRecivedReceiptActivity.class);
                            in.putExtra("ydb_ref_id",allTransactionsList.get(position).getYdb_ref_id());
                            startActivity(in);
                        }
                    }
                }


            }
            @Override
            public void sendAgain(View v, int position) {
            }
        });
        rvList.setAdapter(allTransactionsAdapter);
        rvList.scrollToPosition(i);

    }
}