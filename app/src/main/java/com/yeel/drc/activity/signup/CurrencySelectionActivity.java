package com.yeel.drc.activity.signup;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.yeel.drc.adapter.contacts.CurrencySelectionAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.notaddedcurrencylist.NotAddedCurrencyResponse;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

import java.util.List;

public class CurrencySelectionActivity extends BaseActivity {
    TextView tvButton;
    RecyclerView rvList;
    ProgressBar progressBar;
    RecyclerView.LayoutManager layoutManager;
    Context context;
    private CurrencySelectionAdapter bankCurrencyNotYetListAdapter;
    boolean getCurrencyListRetry = false;
    CurrencyListData selectedCurrency;
    List<CurrencyListData> currencyList;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    APICall apiCall;
    SomethingWentWrongDialog somethingWentWrongDialog;
    ErrorDialog errorDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_selection);
        context = CurrencySelectionActivity.this;
        signUpData=(SignUpData) getIntent().getSerializableExtra("signUpData");
        setToolBar();
        initViews();
        setItemListeners();
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
        }
    }

    private void setItemListeners() {
        tvButton.setOnClickListener(view -> {
            commonFunctions.setPage("currency_selection");
            signUpData.setCurrencyId(selectedCurrency.getCurrency_id());
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in = new Intent(context, SelectAccountTypeActivity.class);
            in.putExtra("signUpData", signUpData);
            startActivity(in);
        });
    }



    private void initViews() {
        apiCall=new APICall(context, SthiramValues.finish);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.finish);
        errorDialog=new ErrorDialog(context, SthiramValues.finish);
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        rvList=findViewById(R.id.rv_list);
        progressBar=findViewById(R.id.progressBar);
        layoutManager=new GridLayoutManager(getApplicationContext(),2);
        rvList.setLayoutManager(layoutManager);
        tvButton=findViewById(R.id.tv_continue);
        getCurrencyList();
    }

    private void getCurrencyList(){
        apiCall.getAllCurrencyList(
                getCurrencyListRetry,
                false,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            NotAddedCurrencyResponse apiResponse = gson.fromJson(jsonString, NotAddedCurrencyResponse.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                currencyList=apiResponse.getCurrencyList();
                                setCurrencyList();
                            }else{
                                errorDialog.show(apiResponse.getStatus());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getCurrencyListRetry = true;
                        getCurrencyList();
                    }
                }

        );


    }

    private void setCurrencyList() {
        //set default currency
   /*     if(currencyList.size()==1){
            currencyList.get(0).setSelectedStatus(1);
            selectedCurrency =currencyList.get(0);
        }else {
            for (int i = 0; i < currencyList.size(); i++) {
                currencyList.get(i).setSelectedStatus(0);
            }
        }*/

        for (int i = 0; i < currencyList.size(); i++) {
            currencyList.get(i).setSelectedStatus(0);
        }

        //set currency list
        bankCurrencyNotYetListAdapter=new CurrencySelectionAdapter(context, currencyList, (v, position) -> {
           /* if(currencyList.size()>1){*/
               /* if(currencyList.get(position).getSelectedStatus()==0) {*/
                    selectedCurrency =currencyList.get(position);
                    for (int i = 0; i < currencyList.size(); i++) {
                        currencyList.get(i).setSelectedStatus(0);
                    }
                    currencyList.get(position).setSelectedStatus(1);
                    bankCurrencyNotYetListAdapter.notifyDataSetChanged();
                    checkValid();
               /* }*/
            /*}*/
        });
        rvList.setAdapter(bankCurrencyNotYetListAdapter);
    }

    private void checkValid() {
        commonFunctions.setPage("currency_selection");
        signUpData.setCurrencyId(selectedCurrency.getCurrency_id());
        registerFunctions.saveRegisterDetails(signUpData);
        Intent in = new Intent(context, SelectAccountTypeActivity.class);
        in.putExtra("signUpData", signUpData);
        startActivity(in);
      /*  if(currencySelected){
            tvButton.setEnabled(true);
            tvButton.setBackgroundResource(R.drawable.bg_button_one);
        }else{
            tvButton.setEnabled(false);
            tvButton.setBackgroundResource(R.drawable.bg_button_three);
        }*/
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if(isTaskRoot()){
                    clearSignUpProgressDialog.show();
                }else{
                    finish();
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            clearSignUpProgressDialog.show();
        }else{
            finish();
        }
    }

}