package com.yeel.drc.activity.main.cashpickup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.CashPickupLocationListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.fullagentlist.FullAgentListResponse;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class CashPickupLocationActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    ImageView ivClear;

    RecyclerView rvList;
    LinearLayout llNoItem;
    ProgressBar progressBar;
    EditText edtSearch;
    LinearLayoutManager linearLayoutManager;
    List<AgentData> agentList,agentListFilter,agentListDummy;

    boolean agentListRetry =false;
    CashPickupLocationListAdapter agentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_location);
        setToolBar();
        context= CashPickupLocationActivity.this;
        initView();
        setItemListeners();

    }

    private void setItemListeners() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try{
                    if(agentList.size()!=0){
                        if(!query.toString().equals("")){
                            ivClear.setVisibility(View.VISIBLE);
                            query = query.toString().toLowerCase();
                            agentListFilter =null;
                            agentListFilter = new ArrayList<>();
                            for (int i = 0; i < agentList.size(); i++) {
                                String name = agentList.get(i).getNameToShow().toLowerCase();
                                String locality=agentList.get(i).getLocality().toLowerCase();
                                String province=agentList.get(i).getProvince().toLowerCase();
                                String district=agentList.get(i).getDistrict().toLowerCase();
                                String mobileNumber=agentList.get(i).getMobile();
                                if (name.contains(query)||locality.contains(query)||province.contains(query)||district.contains(query)
                                ||mobileNumber.contains(query)) {
                                    agentListFilter.add(agentList.get(i));
                                }
                            }
                            if(agentListFilter !=null&& agentListFilter.size()!=0){
                                setList(agentListFilter);
                            }else{
                                setNoItem();
                            }
                        }else{
                            ivClear.setVisibility(View.INVISIBLE);
                            // edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            setList(agentList);
                        }
                    }else{
                        setNoItem();
                    }
                }catch (Exception e){
                    setNoItem();
                }
            }
        });
        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
    }

    private void setNoItem() {
        llNoItem.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);
    }

    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        llNoItem=findViewById(R.id.ll_no_item);
        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        llNoItem.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        edtSearch=findViewById(R.id.edt_search);
        ivClear=findViewById(R.id.iv_clear);
        ivClear.setVisibility(View.INVISIBLE);
        getAgentsList();
    }

    private void getAgentsList() {
        apiCall.getFullAgentList(agentListRetry,false,commonFunctions.getUserId(), new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    FullAgentListResponse apiResponse=gson.fromJson(jsonString, FullAgentListResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        agentListDummy =apiResponse.getList();
                        progressBar.setVisibility(View.GONE);
                        if(agentListDummy !=null&& agentListDummy.size()!=0){
                            for(int i=0;i<agentListDummy.size();i++){
                                if(!commonFunctions.getUserId().equals(agentListDummy.get(i).getId())){
                                    //name to show
                                    String agentName="";
                                    if (agentListDummy.get(i).getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                                        agentName=commonFunctions.generateFullName(
                                                agentListDummy.get(i).getFirstname(),
                                                agentListDummy.get(i).getMiddlename(),
                                                agentListDummy.get(i).getLastname()
                                        );

                                    }else{
                                        agentName= agentListDummy.get(i).getBusiness_name();
                                    }
                                    agentListDummy.get(i).setNameToShow(agentName);

                                    boolean isAccountNumberAvailable=false;
                                    List<CurrencyListData> currencyList=agentListDummy.get(i).getCurrencyList();
                                    if(currencyList!=null){
                                        //check currency available or not
                                        for(int j=0;j<currencyList.size();j++){
                                            if(commonFunctions.getCurrencyID().equals(currencyList.get(j).getCurrency_id())){
                                                isAccountNumberAvailable=true;
                                            }
                                        }
                                        if(isAccountNumberAvailable) {
                                            if(agentList!=null){
                                                agentList.add(agentListDummy.get(i));
                                            }else{
                                                agentList=new ArrayList<>();
                                                agentList.add(agentListDummy.get(i));
                                            }

                                        }
                                    }
                                }
                            }
                            setList(agentList);
                        }else{
                            setNoItem();
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
                   // Log.e("Error",e+"");
                }
            }
            @Override
            public void retry() {
                agentListRetry =true;
                getAgentsList();
            }
        });
    }

    private void setList(List<AgentData> list) {

        rvList.setVisibility(View.VISIBLE);
        llNoItem.setVisibility(View.GONE);
        agentListAdapter =new CashPickupLocationListAdapter(list, context, (v, position) -> {
            try {
                List<CurrencyListData> currencyList=list.get(position).getCurrencyList();
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

                    if(isAccountNumberAvailable) {
                        list.get(position).setAccount_no(receiverAccountNumber);
                        Intent in = getIntent();
                        in.putExtra("agent_data", list.get(position));
                        setResult(Activity.RESULT_OK, in);
                        finish();
                    }else{
                        String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                        errorDialog.show(message);
                    }

                }else{
                    String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                    errorDialog.show(message);
                }



            }catch (Exception e){
                commonFunctions.logAValue("Error",e+"");
            }

        });
        rvList.setAdapter(agentListAdapter);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.select_pickup_location);
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

    private void mDialogAgentInfo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_cashout_info);
        TextView description = dialog.findViewById(R.id.description);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.what_is_agent_locator);
        description.setText(R.string.agent_locator_dec);
        TextView dialogButton = dialog.findViewById(R.id.tv_ok);
        dialogButton.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}