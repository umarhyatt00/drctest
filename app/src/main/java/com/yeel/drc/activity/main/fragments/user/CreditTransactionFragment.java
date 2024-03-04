package com.yeel.drc.activity.main.fragments.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.yeel.drc.adapter.AllTransactionsAdapter;
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
import com.yeel.drc.utils.TransactionsRedirections;

import java.util.List;

public class CreditTransactionFragment extends Fragment {
    AllTransactionsAdapter allTransactionsAdapter;
    MultipleLoginDialog multipleLoginDialog;

    private View view;
    private Context context;
    TextView tvNoItem;
    RecyclerView rvList;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;

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

    public CreditTransactionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static CreditTransactionFragment newInstance(String param1, String param2) {
        CreditTransactionFragment fragment = new CreditTransactionFragment();
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
        view=inflater.inflate(R.layout.fragment_credit_transaction, container, false);
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
            if(!commonFunctions.getInTransactions().equals("")){
                String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getInTransactions());
                Gson gson = new Gson();
                AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    shimmerFrameLayoutOne.setVisibility(View.GONE);
                    allTransactionsList=apiResponse.getList();
                    if (allTransactionsList != null && allTransactionsList.size() != 0) {
                        setTransactions();
                    }else{
                        setNoItem();
                    }
                }else{
                    setNoItem();
                }
            }
        }
        offset=0;
        apiCall.getTransactionsByType(getTransactionsByTypeRetry,commonFunctions.getUserAccountNumber(),"10",commonFunctions.getUserId(),"moneyIn",offset+"",false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try{
                    commonFunctions.setInTransactions(response.getKuttoosan());
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        shimmerFrameLayoutOne.setVisibility(View.GONE);
                        allTransactionsList=apiResponse.getList();
                        if (allTransactionsList != null && allTransactionsList.size() != 0) {
                            setTransactions();
                        } else {
                            if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                                if(!multipleLoginDialog.isShowing()){
                                    multipleLoginDialog.show();
                                }
                            }else{
                                setNoItem();
                            }
                        }
                    }else{
                        setNoItem();
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

    private void setNoItem() {
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.VISIBLE);
    }

    private void getTransactionsRemain() {
        apiCall.getTransactionsByType(getTransactionsByTypeRetryRemain,commonFunctions.getUserAccountNumber(),"10",commonFunctions.getUserId(),"moneyIn",offset+"",false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        allTransactionsListRemain =apiResponse.getList();
                        if(allTransactionsListRemain !=null&& allTransactionsListRemain.size()!=0){
                            offset=offset+10;
                            allTransactionsList.addAll(allTransactionsListRemain);
                            hasMore=true;
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
        if (allTransactionsList.size() < 10) {
            hasMore = false;
            setList(0);
        } else {
            offset=offset+10;
            hasMore = true;
            setList(0);
        }
    }

    private void setList(int i) {
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.GONE);
        allTransactionsAdapter=new AllTransactionsAdapter(commonFunctions, hasMore, context, allTransactionsList, new AllTransactionsAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                TransactionsRedirections.openTransactionDetailsScreen(context,commonFunctions,allTransactionsList.get(position));
            }

            @Override
            public void sendAgain(View v, int position) {

            }
        });
        rvList.setAdapter(allTransactionsAdapter);
        rvList.scrollToPosition(i);

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
            getTransactions("push");
        }
    };
}