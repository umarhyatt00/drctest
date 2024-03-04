package com.yeel.drc.activity.main.fragments.agent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.AgentAllCommissionListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.agentcommission.AgentCommissionResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class AgentCommissionFragment extends Fragment {
    View view;
    LinearLayout llMain;
    private Context context;

    TextView tvNoItem;
    TextView tvTotalCommission;
    RecyclerView rvList;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    TextView tvCurrencySymbol;


    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CommonFunctions commonFunctions;
    int offset = 0;

    boolean hasMore = false;
    boolean getCommissionListRetry = false;
    boolean getCommissionListRetryRemain = false;

    int limit = 15;
    List<TransactionsData> allCommissionList, allCommissionListRemain;

    AgentAllCommissionListAdapter commissionListAdapter;
    ShimmerFrameLayout shimmerFrameLayoutOne;
    MultipleLoginDialog multipleLoginDialog;


    public AgentCommissionFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AgentCommissionFragment newInstance(String param1, String param2) {
        AgentCommissionFragment fragment = new AgentCommissionFragment();
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
        view = inflater.inflate(R.layout.fragment_agent_commission, container, false);
        context = getActivity();
        llMain = view.findViewById(R.id.ll_main);
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
                            getCommissionListRetryRemain = false;
                            getCommissionListRemain();
                        }
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCommissionList("swipe");
            }
        });
    }

    private void initView() {
        multipleLoginDialog = new MultipleLoginDialog(context, SthiramValues.logout);
        tvCurrencySymbol=view.findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        commonFunctions = new CommonFunctions(context);
        tvNoItem = view.findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rvList = view.findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        tvTotalCommission=view.findViewById(R.id.tv_total_commission);
        tvTotalCommission.setText(commonFunctions.setAmount(commonFunctions.getTotalCommission()));

        shimmerFrameLayoutOne = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_one);
        shimmerFrameLayoutOne.startShimmer();


        getCommissionList("");
    }

    private void getCommissionList(String from) {
        if (from.equals("")) {
            if (!commonFunctions.getCommissionList().equals("")) {
                String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getCommissionList());
                Gson gson = new Gson();
                AgentCommissionResponse apiResponse = gson.fromJson(jsonString, AgentCommissionResponse.class);
                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

                    commonFunctions.setTotalCommission(apiResponse.getAgentTotalCommission());
                    tvTotalCommission.setText(commonFunctions.setAmount(commonFunctions.getTotalCommission()));

                    allCommissionList = apiResponse.getCommissionList();
                    if (allCommissionList != null && allCommissionList.size() != 0) {
                        setCommission(0);
                    } else {
                        setNoItem();
                    }
                }
            }
        }
        offset = 0;
        apiCall.getAgentCommissionList(getCommissionListRetry,commonFunctions.getUserAccountNumber(), limit + "", commonFunctions.getUserId(), offset + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    commonFunctions.setCommissionList(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AgentCommissionResponse apiResponse = gson.fromJson(jsonString, AgentCommissionResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

                        commonFunctions.setTotalCommission(apiResponse.getAgentTotalCommission());
                        tvTotalCommission.setText(commonFunctions.setAmount(commonFunctions.getTotalCommission()));

                        allCommissionList = apiResponse.getCommissionList();
                        if (allCommissionList != null && allCommissionList.size() != 0) {
                            setCommission(0);
                        } else {
                            setNoItem();
                        }
                    } else {
                        if(apiResponse.getMessage().contains(SthiramValues.Unauthorised)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
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
                getCommissionListRetry = true;
                getCommissionList("");
            }
        });
    }


    private void setNoItem() {
        tvNoItem.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);
        shimmerFrameLayoutOne.setVisibility(View.GONE);
    }

    private void getCommissionListRemain() {
        apiCall.getAgentCommissionList(getCommissionListRetryRemain,commonFunctions.getUserAccountNumber(), limit + "", commonFunctions.getUserId(), offset + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AgentCommissionResponse apiResponse = gson.fromJson(jsonString, AgentCommissionResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        allCommissionListRemain = apiResponse.getCommissionList();
                        if (allCommissionListRemain != null && allCommissionListRemain.size() != 0) {
                            offset = offset + limit;
                            allCommissionList.addAll(allCommissionListRemain);
                            hasMore = true;
                            commissionListAdapter.notifyDataSetChanged();
                        } else {
                            setCommission(allCommissionList.size() - 1);
                        }
                    } else {
                        setCommission(allCommissionList.size() - 1);
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getCommissionListRetryRemain = true;
                getCommissionListRemain();
            }
        });
    }

    private void setCommission(int i) {
        if (allCommissionList.size() < limit) {
            hasMore = false;
        } else {
            offset = offset + limit;
            hasMore = true;
        }
        shimmerFrameLayoutOne.setVisibility(View.GONE);
        commissionListAdapter = new AgentAllCommissionListAdapter(commonFunctions, hasMore, context, allCommissionList, new AgentAllCommissionListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {

            }

            @Override
            public void sendAgain(View v, int position) {

            }
        });
        rvList.setAdapter(commissionListAdapter);
        rvList.scrollToPosition(i);

    }


}