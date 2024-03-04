package com.yeel.drc.activity.main.billpayments;

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
import com.yeel.drc.activity.main.billpayments.internal.EducationInternalBillPaymentsActivity;
import com.yeel.drc.adapter.ProvidersListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.api.providersList.ProvidersListResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.billpayments.BillPaymentsData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProvidersListActivity extends BaseActivity {
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    boolean getProvidersListRetry=false;
    List<ProvidersListData> providersList;

    String optionId;
    String optionName;
    Context context;
    ProgressBar progressBar;

    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    ProvidersListAdapter providersListAdapter;
    BillPaymentsData billPaymentsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers_list);
        optionId=getIntent().getStringExtra("optionId");
        optionName=getIntent().getStringExtra("optionName");
        context=ProvidersListActivity.this;
        setToolBar();
        initView();
        setItemListeners();

    }

    private void setItemListeners() {

    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        progressBar=findViewById(R.id.progressBar);
        billPaymentsData=new BillPaymentsData();
        getProvidersList();

    }

    private void getProvidersList() {
        progressBar.setVisibility(View.VISIBLE);
        apiCall.getProvidersList(getProvidersListRetry,optionId,false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    commonFunctions.setNotifications(response.getKuttoosan());
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ProvidersListResponse apiResponse = gson.fromJson(jsonString, ProvidersListResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        List<ProvidersListData> providersListMain=apiResponse.getCurrencyList();
                        providersList=new ArrayList<>();
                        for(int i=0;i<providersListMain.size();i++){
                            String currencyId=providersListMain.get(i).getCurrency_id();
                            if(currencyId==null){
                                currencyId= SthiramValues.SELECTED_CURRENCY_ID;
                            }

                            if(currencyId.equals(SthiramValues.SELECTED_CURRENCY_ID)){
                                providersList.add(providersListMain.get(i));
                            }
                        }
                        setList();
                    }else{
                        errorDialog.show(apiResponse.getStatus());
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getProvidersListRetry=true;
                getProvidersList();
            }
        });
    }

    private void setList() {
        progressBar.setVisibility(View.GONE);
        providersListAdapter = new ProvidersListAdapter(context, providersList, (v, position) -> {
            String status = providersList.get(position).getStatus();  //1- Active  0-Coming Soon
            String providerAccountNumber = providersList.get(position).getAccountNumber(); //receiver account number
            String isExternal = providersList.get(position).getIs_external();  //1- External, 0- Internal
            String serviceType = providersList.get(position).getService_type();
            String multiChannel = providersList.get(position).getMulti_channel();
            if (status.equals("1")) {
                //get account number from currency list
              //  List<CurrencyListData> currencyList = providersList.get(position).getCurrencyList();
             //   String receiverAccountNumber= commonFunctions.getReceiverAccountNumberFormCurrencyList(currencyList);
  /*              if(receiverAccountNumber!=null&&!receiverAccountNumber.equals("")){
                    providersList.get(position).setAccountNumber(receiverAccountNumber);*/
                    if (!commonFunctions.getUserAccountNumber().equals(providerAccountNumber)) {
                        if (multiChannel.equals("0")) {
                            if (isExternal.equals("1")) {
                                //this is for external bill payment options
                            } else {
                                //internal bill payment options
                                if (serviceType.equals(SthiramValues.BILL_PAYMENT_TYPE_EDUCATION)) {
                                    billPaymentsData.setProvidersListData(providersList.get(position));
                                    Intent in = new Intent(context, EducationInternalBillPaymentsActivity.class);
                                    in.putExtra("data", billPaymentsData);
                                    in.putExtra("from", "normal");
                                    startActivity(in);
                                } else {
                                    errorDialog.show(getString(R.string.coming_soon));
                                }
                            }
                        } else {
                            //providers sublist
                        }
                    } else {
                        errorDialog.show(getString(R.string.same_account_money_transfer_validation));
                    }
/*
                }else{
                    String message=getString(R.string.receiver_not_matching_message)+Constants.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                    errorDialog.show(message);
                }*/
            } else {
                errorDialog.show(getString(R.string.coming_soon));
            }
        });
        rvList.setAdapter(providersListAdapter);

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(optionName);
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