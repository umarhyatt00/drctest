package com.yeel.drc.activity.main.agent.addfund;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.AgentHistoryListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.agentaddfundhistroy.AddFundHistoryListData;
import com.yeel.drc.api.agentaddfundhistroy.AddFundHistoryResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.List;

public class AddFundsHistoryActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    RecyclerView rvList;
    TextView tvNoItem;
    ProgressBar progressBar;
    LinearLayoutManager linearLayoutManager;
    List<AddFundHistoryListData> historyList;

    boolean historyListRetry =false;
    AgentHistoryListAdapter agentHistoryListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds_history);
        context= AddFundsHistoryActivity.this;
        setToolBar();
        initView();
    }


    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);


        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);

        getAddFundHistoryList();
    }

    private void getAddFundHistoryList() {
        apiCall.getAddFundHistoryList(historyListRetry,false,commonFunctions.getUserId(),commonFunctions.getUserAccountNumber(), new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    AddFundHistoryResponse apiResponse=gson.fromJson(jsonString, AddFundHistoryResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        historyList=apiResponse.getList();
                        progressBar.setVisibility(View.GONE);
                        if(historyList!=null&&historyList.size()!=0){
                            setList();
                        }else{
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    }else{
                        if(!errorDialog.isShowing()){
                            errorDialog.show(apiResponse.getMessage());
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
                historyListRetry =true;
                getAddFundHistoryList();
            }
        });
    }

    private void setList() {
        agentHistoryListAdapter=new AgentHistoryListAdapter(historyList, context, new AgentHistoryListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                Intent in=new Intent(context,AddFundsDetailsActivity.class);
                in.putExtra("data",historyList.get(position));
                startActivity(in);
            }
        });
        rvList.setAdapter(agentHistoryListAdapter);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.all_deposit_requests);
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
}