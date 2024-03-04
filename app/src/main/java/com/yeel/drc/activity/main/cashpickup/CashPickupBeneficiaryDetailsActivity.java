package com.yeel.drc.activity.main.cashpickup;

import static com.yeel.drc.utils.SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.adapter.BeneficiaryListAdapter;
import com.yeel.drc.adapter.QuickPayListAdapterForCashPickup;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.addbeneficiary.AddBeneficiaryResponse;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.quicklinklist.QuickLinkListResponse;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.beneficiary.BeneficiaryDataResponse;
import com.yeel.drc.model.beneficiary.NonYeeluserListItem;
import com.yeel.drc.model.cashpickup.CashPickupData;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;

import java.util.ArrayList;
import java.util.List;

public class CashPickupBeneficiaryDetailsActivity extends BaseActivity {
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CashPickupData cashPickupData;

    boolean quickPayRetry = false;
    boolean addBeneficiaryRetry = false;
    boolean updateBeneficiaryRetry = false;
    boolean getBeneficiaryRetry = false;

    List<TransactionsData> listMain, list;
    List<String> idList;
    QuickPayListAdapterForCashPickup quickPayListAdapter;
    BeneficiaryListAdapter beneficiaryListAdapter;
    List<NonYeeluserListItem> nonYeeluserList;


    RecyclerView rvList;
    GridLayoutManager gridLayoutManager;
    ProgressBar progressBar;
    LinearLayout llMain;
    LinearLayout llQuickpay;
    LinearLayout llNext;
    TextView tvContinue;
    TextInputLayout tilFirstName;
    TextInputLayout tilMiddleName;
    TextInputLayout tilLastName;
    TextInputLayout tilEmail;
    TextInputEditText edtFirstName;
    TextInputEditText edtMiddleName;
    TextInputEditText edtLastName;
    AutoCompleteTextView edtMobile;
    TextInputEditText edtEmail;


    boolean validFirstName = false;
    boolean validMiddleName = true;
    boolean validLastName = false;
    boolean validMobileNumber = false;
    boolean validEmail = true;
    boolean validCountry = true;

    String firstName = "";
    String middleName = "";
    String lastName = "";
    String emailAddress = "";
    String country = "South Sudan";
    String beneficiaryId = "";

    String oldUserData = "";
    String oldMobileData = "";
    String oldMobileCountryCode = "";


    String mobileNumber = "";
    String mobileNumberLength;
    String countyCode;
    ImageView ivFlag;
    TextView tvCountryCode;
    LinearLayout llMobileCountyCode;
    final static int SELECT_A_COUNTRY_CODE = 102;
    TextWatcher watcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benificiary_details_for_cash_pickup);
        context = CashPickupBeneficiaryDetailsActivity.this;
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        llMobileCountyCode.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type", "onboarding");
            startActivityForResult(in, SELECT_A_COUNTRY_CODE);
        });
        //validation first name
        edtMobile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edtFirstName.requestFocus();
                    return true;
                }
                return false;
            }
        });
        edtMobile.setOnItemClickListener((adapterView, view, position, id) -> {
            NonYeeluserListItem beneficiary = (NonYeeluserListItem) adapterView.getItemAtPosition(position);
            beneficiaryItemClick(beneficiary);
            commonFunctions.hideKeyboard(CashPickupBeneficiaryDetailsActivity.this);
        });
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firstName = edtFirstName.getText().toString().trim();
                if (commonFunctions.validateFistName(firstName)) {
                    validFirstName = true;
                    tilFirstName.setError(null);
                } else {
                    validFirstName = false;
                    if (tilFirstName.getError() == null || tilFirstName.getError().equals("")) {
                        tilFirstName.setError(getString(R.string.enter_valid_first_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate middle name
        edtMiddleName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                middleName = edtMiddleName.getText().toString().trim();
                if (commonFunctions.validateMiddleName(middleName)) {
                    validMiddleName = true;
                    tilMiddleName.setError(null);
                } else {
                    validMiddleName = false;
                    if (tilMiddleName.getError() == null || tilMiddleName.getError().equals("")) {
                        tilMiddleName.setError(getString(R.string.enter_valid_middle_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //validate last name
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                lastName = edtLastName.getText().toString().trim();
                if (commonFunctions.validateLastName(lastName)) {
                    validLastName = true;
                    tilLastName.setError(null);
                } else {
                    validLastName = false;
                    if (tilLastName.getError() == null || tilLastName.getError().equals("")) {
                        tilLastName.setError(getString(R.string.enter_valid_last_name));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = edtMobile.getText().toString().trim();
                mobileNumber = number.replaceAll("\\D", "");
                if (commonFunctions.validateMobileNumberDynamic(mobileNumber, mobileNumberLength)) {
                    validMobileNumber = true;
                } else {
                    validMobileNumber = false;
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailAddress = edtEmail.getText().toString().trim();
                if (commonFunctions.isEmailValidOptional(emailAddress)) {
                    validEmail = true;
                    tilEmail.setError(null);
                } else {
                    validEmail = false;
                    tvContinue.setEnabled(false);
                    if (tilEmail.getError() == null || tilEmail.getError().equals("")) {
                        tilEmail.setError(getString(R.string.enter_valid_email));
                    }
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvContinue.setOnClickListener(view -> {
            if (!oldMobileData.equals("")) {
                String currentUserdata = firstName + middleName + lastName + emailAddress;
                String fullMobileNumberOld = oldMobileCountryCode + oldMobileData;
                String fullMobileNumberNew = countyCode + mobileNumber;
                if (fullMobileNumberOld.equals(fullMobileNumberNew)) { // No change in mobile number
                    if (oldUserData.equals(currentUserdata)) { // No changes in user data. then no need to do anything
                        commonFunctions.logAValue("Button Action", "No need call API");
                        setDataAndGoNext(beneficiaryId);
                    } else { // if user data changed. then should updated beneficiary data.
                        commonFunctions.logAValue("Button Action", "Update Beneficiary");
                        updateBeneficiary();
                    }
                } else {
                    commonFunctions.logAValue("Button Action", "Add New Beneficiary");
                    addBeneficiary();
                }
            } else {
                boolean mobileNumberContains = false;
                if (nonYeeluserList != null && nonYeeluserList.size() != 0) {
                    for (int i = 0; i < nonYeeluserList.size(); i++) {
                        if (nonYeeluserList.get(i).getMobile().equals(mobileNumber)) {
                            beneficiaryId = nonYeeluserList.get(i).getId();
                            mobileNumberContains = true;
                        }
                    }
                }
                if (mobileNumberContains) {
                    commonFunctions.logAValue("Button Action", "Update Beneficiary");
                    updateBeneficiary();
                } else {
                    commonFunctions.logAValue("Button Action", "Add New Beneficiary");
                    addBeneficiary();
                }
            }

        });

    }

    private void setDataAndGoNext(String beneficiaryId) {
        BeneficiaryCommonData cashPickupReceiverData = new BeneficiaryCommonData();
        cashPickupReceiverData.setFirstName(firstName);
        cashPickupReceiverData.setMiddleName(middleName);
        cashPickupReceiverData.setLastName(lastName);
        cashPickupReceiverData.setMobileNumber(mobileNumber);
        cashPickupReceiverData.setMobileCountryCode(countyCode);
        cashPickupReceiverData.setEmailAddress(emailAddress);
        cashPickupReceiverData.setCountryName(country);
        cashPickupReceiverData.setBeneficiaryId(beneficiaryId);
        cashPickupData.setCashPickupReceiverData(cashPickupReceiverData);


        cashPickupData.setCashPickupType(TRANSACTION_TYPE_USER_CASH_PICKUP);
        Intent in = new Intent(context, CashPickupDetailsActivity.class);
        in.putExtra("data", cashPickupData);
        startActivity(in);
    }

    private void updateBeneficiary() {
        apiCall.updateBeneficiary(addBeneficiaryRetry,
                beneficiaryId + "",
                firstName + "",
                middleName + "",
                lastName + "",
                mobileNumber + "",
                countyCode + "",
                emailAddress + "",
                true,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            AddBeneficiaryResponse apiResponse = gson.fromJson(jsonString, AddBeneficiaryResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                setDataAndGoNext(beneficiaryId);
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    }

                    @Override
                    public void retry() {
                        updateBeneficiaryRetry = true;
                        updateBeneficiary();
                    }
                });
    }


    private void addBeneficiary() {
        apiCall.addBeneficiary(addBeneficiaryRetry,
                commonFunctions.getUserId() + "",
                firstName + "",
                middleName + "",
                lastName + "",
                mobileNumber + "",
                countyCode + "",
                emailAddress + "",
                country + "",
                true,
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse response) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                            Gson gson = new Gson();
                            AddBeneficiaryResponse apiResponse = gson.fromJson(jsonString, AddBeneficiaryResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

                                CashPickupData cashPickupData = new CashPickupData();
                                BeneficiaryCommonData cashPickupReceiverData = new BeneficiaryCommonData();
                                cashPickupReceiverData.setFirstName(firstName);
                                cashPickupReceiverData.setMiddleName(middleName);
                                cashPickupReceiverData.setLastName(lastName);
                                cashPickupReceiverData.setMobileNumber(mobileNumber);
                                cashPickupReceiverData.setMobileCountryCode(countyCode);
                                cashPickupReceiverData.setEmailAddress(emailAddress);
                                cashPickupReceiverData.setCountryName(country);

                                cashPickupReceiverData.setBeneficiaryId(apiResponse.getId());

                                cashPickupData.setCashPickupReceiverData(cashPickupReceiverData);
                                cashPickupData.setCashPickupType(TRANSACTION_TYPE_USER_CASH_PICKUP);
                                Intent in = new Intent(context, CashPickupDetailsActivity.class);
                                in.putExtra("data", cashPickupData);
                                startActivity(in);
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    }

                    @Override
                    public void retry() {
                        addBeneficiaryRetry = true;
                        addBeneficiary();
                    }
                });
    }


    private void checkAllValid() {
        if (validFirstName && validMiddleName && validLastName && validMobileNumber && validEmail && validCountry) {
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        cashPickupData = new CashPickupData();

        llMain = findViewById(R.id.ll_main);
        llMain.setVisibility(View.GONE);
        llQuickpay = findViewById(R.id.ll_quick_pay);
        llQuickpay.setVisibility(View.GONE);
        rvList = findViewById(R.id.rv_list);
        gridLayoutManager = new GridLayoutManager(context, 4);
        rvList.setLayoutManager(gridLayoutManager);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        llNext = findViewById(R.id.ll_next);
        llNext.setVisibility(View.GONE);
        tvContinue = findViewById(R.id.tv_continue);

        tilFirstName = findViewById(R.id.til_first_name);
        edtFirstName = findViewById(R.id.edt_first_name);
        tilMiddleName = findViewById(R.id.til_middle_name);
        edtMiddleName = findViewById(R.id.edt_middle_name);
        tilLastName = findViewById(R.id.til_last_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtMobile = findViewById(R.id.edt_mobile);
        // edtMobile.addTextChangedListener(new TextFormatter(edtMobile, getString(R.string.mobile_number_formating_code)));
        tilEmail = findViewById(R.id.til_email);
        edtEmail = findViewById(R.id.edt_email);


        //county code
        tvCountryCode = findViewById(R.id.tv_county_code);
        ivFlag = findViewById(R.id.iv_flag_country_code);
        llMobileCountyCode = findViewById(R.id.ll_mobile_county_code);
        countyCode = SthiramValues.DEFAULT_COUNTY_MOBILE_CODE;
        mobileNumberLength = SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH;
        setFlag(SthiramValues.DEFAULT_COUNTY_FLAG);
        setMobileNumberFormat(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT);

        checkAllValid();
        getQuickPayList();
        getAllBeneficiaries();
    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE + flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }

    private void getAllBeneficiaries() {
        apiCall.getBeneficiariesList(getBeneficiaryRetry, commonFunctions.getUserId() + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BeneficiaryDataResponse apiResponse = gson.fromJson(jsonString, BeneficiaryDataResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        nonYeeluserList = apiResponse.getNonYeeluserList();
                        setDropDownList();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getBeneficiaryRetry = true;
                getAllBeneficiaries();
            }
        });
    }

    private void setDropDownList() {
        List<NonYeeluserListItem> listDummy = new ArrayList<>();
        if (nonYeeluserList != null && nonYeeluserList.size() != 0) {
            for (int i = 0; i < nonYeeluserList.size(); i++) {
                if (nonYeeluserList.get(i).getCountryCode().equals(countyCode)) {
                    listDummy.add(nonYeeluserList.get(i));
                }
            }
            edtMobile.setThreshold(1);
            beneficiaryListAdapter = new BeneficiaryListAdapter(context, listDummy);
            edtMobile.setAdapter(beneficiaryListAdapter);
        }

    }

    private void beneficiaryItemClick(NonYeeluserListItem beneficiary) {
        edtMobile.setText(beneficiary.getMobile());
        edtFirstName.setText(beneficiary.getFirstname());
        edtMiddleName.setText(beneficiary.getMiddlename());
        edtLastName.setText(beneficiary.getLastname());
        edtEmail.setText(beneficiary.getEmail());
        oldMobileData = beneficiary.getMobile();
        oldMobileCountryCode = beneficiary.getCountryCode();
        oldUserData = beneficiary.getFirstname() + beneficiary.getMiddlename() + beneficiary.getLastname() + beneficiary.getEmail();
        beneficiaryId = beneficiary.getId();
    }


    private void getQuickPayList() {
        apiCall.quickLinkTransList(quickPayRetry, commonFunctions.getUserAccountNumber(), commonFunctions.getUserId(), TRANSACTION_TYPE_USER_CASH_PICKUP, false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    QuickLinkListResponse apiResponse = gson.fromJson(jsonString, QuickLinkListResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        listMain = apiResponse.getResult().getQuickLinkList();
                        idList = new ArrayList<>();
                        list = new ArrayList<>();
                        for (int i = 0; i < listMain.size(); i++) {
                            String receiverId = listMain.get(i).getReceiver_id();
                            if (!idList.contains(receiverId)) {
                                idList.add(receiverId);
                                list.add(listMain.get(i));
                            }
                        }
                        setList();
                    } else {
                        list = new ArrayList<>();
                        setList();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                quickPayRetry = true;
                getQuickPayList();
            }
        });
    }

    private void setList() {
        progressBar.setVisibility(View.GONE);
        llMain.setVisibility(View.VISIBLE);
        llNext.setVisibility(View.VISIBLE);
        if (list.size() > 4) {
            list = new ArrayList<>(list.subList(0, 3));
        } else if (list.size() == 0) {
        /*    TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            TransactionsData four = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
            list.add(four);*/
        } else if (list.size() == 1) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            TransactionsData three = new TransactionsData("");
            list.add(one);
            list.add(two);
            list.add(three);
        } else if (list.size() == 2) {
            TransactionsData one = new TransactionsData("");
            TransactionsData two = new TransactionsData("");
            list.add(one);
            list.add(two);
        } else if (list.size() == 3) {
            TransactionsData one = new TransactionsData("");
            list.add(one);
        }
        if (list.size() != 0) {
            llQuickpay.setVisibility(View.VISIBLE);
            quickPayListAdapter = new QuickPayListAdapterForCashPickup(list, context, (v, position) -> {

                AgentData agentData = list.get(position).getAgentDetails();
                List<CurrencyListData> currencyList = list.get(position).getCurrencyList();
                boolean isAccountNumberAvailable = false;
                String receiverAccountNumber = "";

                if (currencyList != null) {
                    //check currency available or not
                    for (int i = 0; i < currencyList.size(); i++) {
                        if (commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())) {
                            receiverAccountNumber = currencyList.get(i).getAccount_number();
                            isAccountNumberAvailable = true;
                            break;
                        }
                    }
                    if (isAccountNumberAvailable) {
                        agentData.setAccount_no(receiverAccountNumber);
                        TransactionsData selectedBeneficiary = list.get(position);
                        firstName = selectedBeneficiary.getBeneficiaryDetails().getFirstname();
                        middleName = selectedBeneficiary.getBeneficiaryDetails().getMiddleName();
                        lastName = selectedBeneficiary.getBeneficiaryDetails().getLastname();
                        mobileNumber = selectedBeneficiary.getBeneficiaryDetails().getMobile();
                        emailAddress = selectedBeneficiary.getBeneficiaryDetails().getEmail();
                        beneficiaryId = selectedBeneficiary.getBeneficiaryDetails().getId();
                        cashPickupData.setCashPickupAgentData(agentData);
                        setDataAndGoNext(beneficiaryId);

                    } else {
                        String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                        errorDialog.show(message);
                    }
                } else {
                    String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                    errorDialog.show(message);
                }
            });
            rvList.setAdapter(quickPayListAdapter);
        } else {
            llQuickpay.setVisibility(View.GONE);
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
        tvTitle.setText(R.string.beneficiary_details);
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_A_COUNTRY_CODE) {
                countyCode = data.getStringExtra("country_code");
                mobileNumberLength = data.getStringExtra("mobile_number_length");
                String flag = data.getStringExtra("flag");
                setMobileNumberFormat(data.getStringExtra("format"));
                tvCountryCode.setText(countyCode);
                setFlag(flag);
                setDropDownList();
                edtMobile.setText("");
            }
        }

    }

    private void setMobileNumberFormat(String format) {
        if (watcher != null) {
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher = new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

}