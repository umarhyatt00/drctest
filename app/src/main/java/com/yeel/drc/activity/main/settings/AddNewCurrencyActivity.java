package com.yeel.drc.activity.main.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.PINVerificationActivity;
import com.yeel.drc.adapter.contacts.CurrencySelectionAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.notaddedcurrencylist.NotAddedCurrencyResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.List;
import java.util.Objects;

public class AddNewCurrencyActivity extends BaseActivity {
    TextView tvButton;
    RecyclerView rvList;
    ProgressBar progressBar;
    DialogProgress dialogProgress;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    RecyclerView.LayoutManager layoutManager;
    Context context;
    private CurrencySelectionAdapter bankCurrencyNotYetListAdapter;

    boolean getCurrencyListRetry = false;
    boolean currencySelected = false;
    CurrencyListData selectedCurrency;

    List<CurrencyListData> currencyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_currency);
        context = AddNewCurrencyActivity.this;
        setToolBar();
        initViews();
        setItemListeners();


    }

    private void setItemListeners() {
        tvButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), PINVerificationActivity.class);
            startActivityForResult(intent, 200);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                 addNewCurrencyAPI();
            }
        }
    }


    private void initViews() {
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        rvList=findViewById(R.id.rv_list);
        progressBar=findViewById(R.id.progressBar);
        layoutManager=new GridLayoutManager(getApplicationContext(),2);
        rvList.setLayoutManager(layoutManager);
        tvButton=findViewById(R.id.tv_continue);
        tvButton.setVisibility(View.GONE);
        checkValid();
        getCurrencyList();
    }


    private void getCurrencyList(){
        apiCall.getNotLinkedCurrencyList(
                getCurrencyListRetry,
                false,
                commonFunctions.getUserId(),
                SthiramValues.SELECTED_CURRENCY_ID,
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
        tvButton.setVisibility(View.VISIBLE);
        //set default currency
        if(currencyList.size()==1){
            currencyList.get(0).setSelectedStatus(1);
            selectedCurrency =currencyList.get(0);
            currencySelected=true;
        }else {
            for (int i = 0; i < currencyList.size(); i++) {
                currencyList.get(i).setSelectedStatus(0);
            }
            currencySelected=false;
        }


        checkValid();

        //set currency list
        bankCurrencyNotYetListAdapter=new CurrencySelectionAdapter(AddNewCurrencyActivity.this, currencyList, (v, position) -> {
            if(currencyList.size()>1){
                if(currencyList.get(position).getSelectedStatus()==0) {
                    selectedCurrency =currencyList.get(position);
                    currencySelected=true;
                    for (int i = 0; i < currencyList.size(); i++) {
                        currencyList.get(i).setSelectedStatus(0);
                    }
                    currencyList.get(position).setSelectedStatus(1);
                    bankCurrencyNotYetListAdapter.notifyDataSetChanged();
                    checkValid();
                }
            }
        });
        rvList.setAdapter(bankCurrencyNotYetListAdapter);
    }

    private void checkValid() {
        if(currencySelected){
            tvButton.setEnabled(true);
            tvButton.setBackgroundResource(R.drawable.bg_button_one);
        }else{
            tvButton.setEnabled(false);
            tvButton.setBackgroundResource(R.drawable.bg_button_three);
        }
    }

    private void addNewCurrencyAPI() {
        apiCall.addANewCurrency(
                getCurrencyListRetry,
                true,
                commonFunctions.getUserId(),
                selectedCurrency.getCurrency_id(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                                successDialogue(apiResponse.getMessage());
                            }else{
                                errorDialog.show(apiResponse.getMessage());
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

    private void successDialogue(String message) {
        final Dialog success = new Dialog(AddNewCurrencyActivity.this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.dialog_style_success);
        TextView mButtonCancel = success.findViewById(R.id.tv_ok);
        TextView tvMessage = success.findViewById(R.id.tv_message);
        tvMessage.setText(message);
        mButtonCancel.setOnClickListener(v -> {
            success.dismiss();
            Intent in = getIntent();
            setResult(Activity.RESULT_OK, in);
            finish();
        });
        Objects.requireNonNull(success.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
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
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}