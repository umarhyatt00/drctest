package com.yeel.drc.activity.main.cashpickup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.agent.cashpickup.AgentCashPickupConfirmationActivity;
import com.yeel.drc.adapter.CashPickupPurposeListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.cashpickuppurpose.PurposeListData;
import com.yeel.drc.api.cashpickuppurpose.PurposeListResponse;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.cashpickup.CashPickupData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class CashPickupDetailsActivity extends BaseActivity {
    CashPickupData cashPickupData;
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    RecyclerView rvList;
    StaggeredGridLayoutManager mLayoutManager;
   // List<PurposeListData> purposeList;
    CashPickupPurposeListAdapter cashPickupPurposeListAdapter;
    RelativeLayout llCashPickupLocation;
    RelativeLayout rlAgentDetails;
    LinearLayout llNext;
    TextView tvContinue;
    EditText edtAmount;
    TextView tvAgentName;
    TextView tvAgentDetails;
    EditText edtNote;
    LinearLayout mainView;
    ProgressBar progressBar;
    ShimmerFrameLayout shimmerFrameLayout;
    TextView tvAmountSymbol;
    TextInputLayout til_additionalNote;

    public static final int AGENT_INTENT_ID = 101;
    boolean getPurposeRetry = false;
    boolean validAmount = false;
    boolean validPurpose = false;
    boolean validNote = true;
    boolean validAgent = false;
    AgentData agentData;
    String note;
    String purpose = "";


    private double amount=0.00;

    List<PurposeListData> purposeList;
    List<PurposeListData> purposeListMain;
    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_details);
        context = CashPickupDetailsActivity.this;
        cashPickupData = (CashPickupData) getIntent().getSerializableExtra("data");


        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        edtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                note = edtNote.getText().toString().trim();
                if(commonFunctions.validateRemark(note)){
                    validNote=true;
                    til_additionalNote.setError(null);
                    setReadyToNext();
                }else{
                    validNote=false;
                    if(til_additionalNote.getError()==null||til_additionalNote.getError().equals("")){
                        til_additionalNote.setError(getString(R.string.enter_valid_remark));
                    }
                    setReadyToNext();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtAmount.addTextChangedListener(new TextWatcher() {
            CountDownTimer timer = null;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtAmount.getText().toString().trim().equals("")) {
                    if(commonFunctions.validAmount(edtAmount.getText().toString())){
                        amount = Double.parseDouble(edtAmount.getText().toString());
                        validAmount=true;
                        setReadyToNext();
                    }else{
                        setAmountToZero();
                    }
                } else {
                    setAmountToZero();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        llCashPickupLocation.setOnClickListener(view -> {
            Intent in = new Intent(context, CashPickupLocationActivity.class);
            startActivityForResult(in, AGENT_INTENT_ID);
        });
        rlAgentDetails.setOnClickListener(view -> {
            Intent in = new Intent(context, CashPickupLocationActivity.class);
            startActivityForResult(in, AGENT_INTENT_ID);
        });
        tvContinue.setOnClickListener(view -> {
            if(cashPickupData.getCashPickupType().equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)){
                //user cash pickup
                String message=commonFunctions.checkTransactionIsPossible(String.valueOf(amount),String.valueOf(amount), SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP);
                if(message.equals(SthiramValues.SUCCESS)){
                    cashPickupData.setAmount(amount + "");
                    cashPickupData.setPurpose(purpose);
                    cashPickupData.setAdditionalNotes(note);

                    Intent in = new Intent(context, CashPickupConfirmationActivity.class);
                    in.putExtra("data", cashPickupData);
                    startActivity(in);
                }
            }else{
                String message=commonFunctions.checkTransactionIsPossible(String.valueOf(amount),String.valueOf(amount), SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP);
                if(message.equals(SthiramValues.SUCCESS)) {
                    cashPickupData.setAmount(amount + "");
                    cashPickupData.setPurpose(purpose);
                    cashPickupData.setAdditionalNotes(note);

                    Intent in = new Intent(context, AgentCashPickupConfirmationActivity.class);
                    in.putExtra("data", cashPickupData);
                    startActivity(in);
                }
            }


        });
    }

    private void setAmountToZero() {
        amount = 0.00;
        validAmount=false;
        setReadyToNext();
    }


    private void initView() {
        commonFunctions=new CommonFunctions(context);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        shimmerFrameLayout=findViewById(R.id.shimmer_view_purpose_list);
        shimmerFrameLayout.startShimmer();

        rvList = findViewById(R.id.rv_list);
        mLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.HORIZONTAL);
        rvList.setLayoutManager(mLayoutManager);

        llCashPickupLocation = findViewById(R.id.ll_pickup_location);
        rlAgentDetails = findViewById(R.id.rl_agent_details);
        llNext = findViewById(R.id.ll_next);
        tvContinue = findViewById(R.id.tv_continue);
        edtAmount = findViewById(R.id.edt_amount);
        tvAgentName = findViewById(R.id.tv_agent_name);
        tvAgentDetails = findViewById(R.id.tv_agent_details);
        edtNote = findViewById(R.id.edtAdditionalNote);
        tvAmountSymbol=findViewById(R.id.tv_amount_symbol);
        tvAmountSymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);
        til_additionalNote=findViewById(R.id.til_additionalNote);

        mainView = findViewById(R.id.ll_main);
        progressBar = findViewById(R.id.progress_bar);

        setDefault();
        setReadyToNext();
        getPurposeList();


        /**
         * To set agent data that get from quick pay option navigation
         */

        if(cashPickupData.getCashPickupAgentData()!=null){
            agentData =cashPickupData.getCashPickupAgentData();
            setAgentDetails();
        }



    }

    private void setReadyToNext() {
        if (validAmount && validPurpose && validNote && validAgent) {
            llNext.setVisibility(View.VISIBLE);
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            llNext.setVisibility(View.VISIBLE);
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }


    private void setDefault() {
        rlAgentDetails.setVisibility(View.GONE);
        llCashPickupLocation.setVisibility(View.VISIBLE);
    }

    private void getPurposeList() {
        if(!commonFunctions.getPurposeListCashPickup().equals("")){
            String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getPurposeListCashPickup());
            Gson gson = new Gson();
            PurposeListResponse apiResponse = gson.fromJson(jsonString, PurposeListResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                setPurposeList(apiResponse.getPurposeList());
            }
        }

        apiCall.getPurposeList(getPurposeRetry, "cash_pickup", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    commonFunctions.setPurposeListCashPickup(response.getKuttoosan());
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    PurposeListResponse apiResponse = gson.fromJson(jsonString, PurposeListResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        setPurposeList(apiResponse.getPurposeList());
                    }else{
                        if (!somethingWentWrongDialog.isShowing()) {
                            somethingWentWrongDialog.show();
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
                getPurposeRetry = true;
                getPurposeList();
            }
        });


    }

    private void setPurposeList(List<PurposeListData> list) {
        shimmerFrameLayout.setVisibility(View.GONE);
        if(cashPickupPurposeListAdapter==null){
            purposeList=list;
            purposeList.get(0).setSelectedStatus(1);
            purpose = purposeList.get(0).getName();
            validPurpose=true;

            cashPickupPurposeListAdapter = new CashPickupPurposeListAdapter(purposeList, (v, position) -> {
                validPurpose=true;
                list.forEach((purpose) -> {
                    purpose.setSelectedStatus(0);
                });
                list.get(position).setSelectedStatus(1);
                purpose = list.get(position).getName();
                cashPickupPurposeListAdapter.notifyDataSetChanged();
                setReadyToNext();
            });
            rvList.setAdapter(cashPickupPurposeListAdapter);

        }else{
            purposeList.clear();
            purposeList.addAll(list);
            if(!purposeList.isEmpty()){

                purposeList.get(0).setSelectedStatus(1);
                purpose = purposeList.get(0).getName();
                validPurpose=true;
            }
            cashPickupPurposeListAdapter.notifyDataSetChanged();
        }

    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cash_pickup_details);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AGENT_INTENT_ID) {
                if (data != null) {
                    agentData = (AgentData) data.getSerializableExtra("agent_data");
                    setAgentDetails();
                }
            }
        }
    }

    private void setAgentDetails() {
        String agentName="";
        String address = "";
        boolean addressAvailable = false;
        addressAvailable = true;
        if (agentData.getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
            address = agentData.getAddressline1() + "\n" + agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            agentName=commonFunctions.generateFullName(
                    agentData.getFirstname(),
                    agentData.getMiddlename(),
                    agentData.getLastname()
            );
        } else {
            address = agentData.getLocality() + ", " + agentData.getProvince() + ", " + agentData.getDistrict();
            agentName = agentData.getBusiness_name();
        }
        tvAgentName.setText(agentName);

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
        String mobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),mobileNumberFormat);


        if (addressAvailable) {
            tvAgentDetails.setText(address + "\n" + mobileNumber);
        } else {
            tvAgentDetails.setText(mobileNumber);
        }
        cashPickupData.setCashPickupAgentData(agentData);
        validAgent = true;
        rlAgentDetails.setVisibility(View.VISIBLE);
        llCashPickupLocation.setVisibility(View.GONE);
        setReadyToNext();
    }
}