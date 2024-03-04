package com.yeel.drc.activity.main.search;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.AllTransactionsAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.alltransactionssearch.TransactionSearchResponse;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.TransactionsRedirections;

import java.util.ArrayList;
import java.util.List;

public class TransactionSearchActivity extends BaseActivity {
    ProgressBar progressBar;
    TextView tvNoItem;
    TextView tvCancel;
    EditText edtSearch;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    Context context;
    DialogProgress dialogProgress;

    List<TransactionsData> allTransactionsList,allTransactionsListFilter;
    AllTransactionsAdapter allTransactionsAdapter;
    boolean getAllTransactionListRetry=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_search);
        context= TransactionSearchActivity.this;
        initViews();
        addTextListener();
        setOnClickListener();
    }
    private void setOnClickListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                try {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            edtSearch.setText("");
                            return true;
                        }
                    }
                }catch (Exception e){

                }

                return false;
            }
        });
    }

    private void addTextListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try{
                    if(allTransactionsList.size()!=0){
                        if(!query.toString().toLowerCase().equals("")){
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_search_clear, 0);
                            query = query.toString().toLowerCase();
                            allTransactionsListFilter=null;
                            allTransactionsListFilter = new ArrayList<>();
                            for (int i = 0; i < allTransactionsList.size(); i++) {
                                String name=allTransactionsList.get(i).getNameToShow().toLowerCase();
                                String amount=allTransactionsList.get(i).getAmount();
                                String formattedDate=allTransactionsList.get(i).getFormattedDate().toLowerCase();
                                if (name.contains(query)||amount.contains(query)||formattedDate.contains(query)) {
                                    allTransactionsListFilter.add(allTransactionsList.get(i));
                                }
                            }
                            if(allTransactionsListFilter!=null&&allTransactionsListFilter.size()!=0){
                                setTransactions(allTransactionsListFilter);
                                setDetailsIfAnyTransaction();
                            }else{
                               setDetailsIfNoTransaction();
                            }
                        }else{
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            setTransactions(allTransactionsList);
                        }
                    }else{
                        setDetailsIfNoTransaction();
                    }
                }catch (Exception e){
                }
            }
        });
    }

    private void initViews() {
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        progressBar=findViewById(R.id.progressBar);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvCancel=findViewById(R.id.tv_cancel);
        edtSearch=findViewById(R.id.edt_search);
        rvList =findViewById(R.id.rvContactList);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);

        setBeforeLoadFullTransactions();
        getTransactions();
    }

    private void getTransactions() {
        apiCall.getAllTransactionList(getAllTransactionListRetry,commonFunctions.getUserAccountNumber(),commonFunctions.getUserId(),false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    TransactionSearchResponse apiResponse=gson.fromJson(jsonString, TransactionSearchResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        progressBar.setVisibility(View.GONE);
                        allTransactionsList=apiResponse.getResult().getTransactionList();
                        if (allTransactionsList != null && allTransactionsList.size() != 0) {
                            for(int i=0;i<allTransactionsList.size();i++){
                                String transactionType=allTransactionsList.get(i).getTransaction_type();
                                String nameToDisplay="";
                                if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)){
                                    BeneficiaryData beneficiaryData=allTransactionsList.get(i).getBeneficiaryDetails();
                                    try {
                                        nameToDisplay=commonFunctions.generateFullName(beneficiaryData.getFirstname(),beneficiaryData.getMiddleName(), beneficiaryData.getLastname());
                                    }catch (Exception err){
                                        nameToDisplay="-";
                                    }
                                }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)){
                                    nameToDisplay=commonFunctions.toTitleCase(allTransactionsList.get(i).getReceiver_name());
                                }else{
                                    if(commonFunctions.getUserId().equals(allTransactionsList.get(i).getSender_id())){
                                        nameToDisplay=allTransactionsList.get(i).getReceiver_name();
                                    }else{
                                        nameToDisplay=allTransactionsList.get(i).getSender_name();
                                    }
                                }
                                allTransactionsList.get(i).setNameToShow(nameToDisplay);
                                allTransactionsList.get(i).setFormattedDate(commonFunctions.getGalilioDateFormat(allTransactionsList.get(i).getTransaction_date(),"time"));
                            }
                           setTransactions(allTransactionsList);
                        } else {
                            tvNoItem.setVisibility(View.VISIBLE);
                        }
                    }else{
                        errorDialog.show(apiResponse.getMessage());
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getAllTransactionListRetry=true;
                getTransactions();
            }
        });
    }

    private void setTransactions(List<TransactionsData> list) {
        setDetailsIfAnyTransaction();
        allTransactionsAdapter=new AllTransactionsAdapter(commonFunctions, false, context, list, new AllTransactionsAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                TransactionsRedirections.openTransactionDetailsScreen(context,commonFunctions,allTransactionsList.get(position));
            }

            @Override
            public void sendAgain(View v, int position) {
                TransactionsRedirections.sendAgainFromTransactionList(context,commonFunctions,errorDialog,allTransactionsList.get(position));
            }
        });
        rvList.setAdapter(allTransactionsAdapter);
    }

    private void setDetailsIfAnyTransaction() {
        progressBar.setVisibility(View.GONE);
        rvList.setVisibility(View.VISIBLE);
        tvNoItem.setVisibility(View.GONE);
        edtSearch.setEnabled(true);
    }

    private void setDetailsIfNoTransaction() {
        progressBar.setVisibility(View.GONE);
        rvList.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.VISIBLE);
        tvNoItem.setText("No search result found");
        edtSearch.setEnabled(true);
    }

    private void setBeforeLoadFullTransactions() {
        progressBar.setVisibility(View.VISIBLE);
        rvList.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.GONE);
        edtSearch.setEnabled(false);
    }

}