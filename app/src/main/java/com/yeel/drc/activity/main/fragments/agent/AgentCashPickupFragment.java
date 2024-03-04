package com.yeel.drc.activity.main.fragments.agent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.agent.cashpickup.history.PaidCompletedDetailsCommonActivity;
import com.yeel.drc.activity.main.agent.cashpickup.history.ToPayConfirmDetailsActivity;
import com.yeel.drc.adapter.AgentAllCashPickupAdapter;
import com.yeel.drc.adapter.AgentCashPickupFilterItemAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.alltransactions.AllTransactionsResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.cashpickup.CashPickupFilterModel;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class AgentCashPickupFragment extends Fragment {
    View view;
    Context context;
    CommonFunctions commonFunctions;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    TextView tvNoItem;

    RecyclerView rvCashPickup;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView imgFilter;

    //filter
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    List<CashPickupFilterModel> filterList;
    AgentCashPickupFilterItemAdapter agentCashPickupFilterItemAdapter;

    int offset=0;
    int limit=15;
    List<TransactionsData> allTransactionsList,allTransactionsListRemain;
    boolean hasMore=false;
    boolean getCashPickupListRemain =false;
    boolean getCashPickupListRetry =false;
    AgentAllCashPickupAdapter agentAllCashPickupAdapterAdapter;
    LinearLayoutManager linearLayoutManager;
    ShimmerFrameLayout shimmerFrameLayout;

    public AgentCashPickupFragment() {

    }

    public static AgentCashPickupFragment newInstance(String param1, String param2) {
        AgentCashPickupFragment fragment = new AgentCashPickupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agent_cash_pickup_fragement, container, false);
        context = getActivity();
        initView();
        setItemListeners();
        return view;
    }

    private void createFilterOptions() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_style_cash_pickup_filters, null);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCurved);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView filterRv = bottomSheetView.findViewById(R.id.rv_filter);
        TextView textDone = bottomSheetView.findViewById(R.id.text_done);
        TextView textClear = bottomSheetView.findViewById(R.id.text_clear);

        filterList = new ArrayList<>();
        filterList.add(new CashPickupFilterModel("To pay", false));
        filterList.add(new CashPickupFilterModel("Paid", true));
        filterList.add(new CashPickupFilterModel("Returned", false));

        agentCashPickupFilterItemAdapter = new AgentCashPickupFilterItemAdapter(filterList, context, new AgentCashPickupFilterItemAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
            }
        });
        filterRv.setAdapter(agentCashPickupFilterItemAdapter);

        textDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        textClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private void setItemListeners() {
        rvCashPickup.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            getCashPickupListRemain =false;
                            getTransactionsRemain();
                        }
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> callTransactionsAPI("swipe"));
        imgFilter.setOnClickListener(v -> bottomSheetDialog.show());

    }


    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);

        tvNoItem=view.findViewById(R.id.tv_no_item);
        rvCashPickup = view.findViewById(R.id.rv_cash_pickup);
        linearLayoutManager=new LinearLayoutManager(context);
        rvCashPickup.setLayoutManager(linearLayoutManager);
        imgFilter = view.findViewById(R.id.btn_filter);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        shimmerFrameLayout=view.findViewById(R.id.shimmer_view);
        shimmerFrameLayout.startShimmer();

        createFilterOptions();
        callTransactionsAPI("");

    }

    private void callTransactionsAPI(String from) {
        if(from.equals("")){
            if(!commonFunctions.getCashPickupTabList().equals("")){
                String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getCashPickupTabList());
                Gson gson = new Gson();
                AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    allTransactionsList=apiResponse.getList();
                    if (allTransactionsList != null && allTransactionsList.size() != 0) {
                        setTransactions();
                    }
                }
            }
        }
        offset=0;
        apiCall.getAgentCashPickupList(getCashPickupListRetry,commonFunctions.getUserAccountNumber(),limit+"",commonFunctions.getUserId(),offset+"",false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try{
                    commonFunctions.setCashPickupTabList(response.getKuttoosan());
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AllTransactionsResponse apiResponse=gson.fromJson(jsonString, AllTransactionsResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        allTransactionsList=apiResponse.getList();
                        if (allTransactionsList != null && allTransactionsList.size() != 0) {
                            setTransactions();
                        } else {
                           setNoItem();
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
                getCashPickupListRetry =true;
                callTransactionsAPI("");
            }
        });
    }

    private void getTransactionsRemain() {
        apiCall.getAgentCashPickupList(getCashPickupListRemain,commonFunctions.getUserAccountNumber(),limit+"",commonFunctions.getUserId(),offset+"",false, new CustomCallback() {
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
                            agentAllCashPickupAdapterAdapter.notifyDataSetChanged();
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
                getCashPickupListRemain =true;
                getTransactionsRemain();
            }
        });
    }


    private void setNoItem() {
        tvNoItem.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private void setTransactions() {
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
        shimmerFrameLayout.setVisibility(View.GONE);
        agentAllCashPickupAdapterAdapter =new AgentAllCashPickupAdapter(commonFunctions, hasMore, context, allTransactionsList, new AgentAllCashPickupAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                String status = allTransactionsList.get(position).getCollection_status();
                if (status.equals("NotCollected")) {
                    Intent intent = new Intent(getContext(), ToPayConfirmDetailsActivity.class);
                    intent.putExtra("data",allTransactionsList.get(position));
                    startActivity(intent);
                }else if (status.equals("Collected")) {
                    Intent intent = new Intent(getContext(), PaidCompletedDetailsCommonActivity.class);
                    intent.putExtra("ydb_ref_id",allTransactionsList.get(position).getYdb_ref_id());
                    startActivity(intent);


                }else{

                }
                }
        });
        rvCashPickup.setAdapter(agentAllCashPickupAdapterAdapter);
        rvCashPickup.scrollToPosition(i);
    }


}