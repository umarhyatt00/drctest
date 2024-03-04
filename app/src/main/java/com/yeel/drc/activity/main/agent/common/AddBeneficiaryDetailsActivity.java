package com.yeel.drc.activity.main.agent.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.adapter.BeneficiaryListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.addbeneficiary.AddBeneficiaryResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.beneficiary.BeneficiaryDataResponse;
import com.yeel.drc.model.beneficiary.NonYeeluserListItem;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBeneficiaryDetailsActivity extends AppCompatActivity {
    Context context;
    CommonFunctions commonFunctions;
    BeneficiaryCommonData customerData;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    BeneficiaryListAdapter beneficiaryListAdapter;
    TextView btnAddDetails;
    TextInputLayout tilFirstName;
    TextInputLayout tilMiddleName;
    TextInputLayout tilLastName;
    TextInputLayout tilEmail;
    TextInputEditText edtFirstName;
    TextInputEditText edtMiddleName;
    TextInputEditText edtLastName;
    AutoCompleteTextView edtMobile;
    TextInputEditText edtEmail;
    NestedScrollView mainScrollView;
    ProgressBar progressBar;

    String customerType;
    boolean addBeneficiaryRetry = false;
    boolean updateBeneficiaryRetry = false;
    boolean getBeneficiaryRetry = false;

    boolean validFirstName = false;
    boolean validMiddleName = true;
    boolean validLastName = false;
    boolean validMobileNumber = false;
    boolean validEmail = true;
    boolean validCountry = true;

    boolean isEditingMode = false;

    String firstName = "";
    String middleName = "";
    String lastName = "";
    String emailAddress = "";
    String country = SthiramValues.DEFAULT_COUNTY_NAME;
    String beneficiaryId = "";
    String beneficiaryIDImage = "";
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
    List<NonYeeluserListItem> nonYeeluserList;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_pickup_add_user_details);
        context = AddBeneficiaryDetailsActivity.this;
        customerType = getIntent().getStringExtra("customer_type");
        from = getIntent().getStringExtra("from");
        initView();
        setItemListeners();
        if (getIntent().hasExtra("customer_data")) {
            isEditingMode = true;
            customerData = (BeneficiaryCommonData) getIntent().getSerializableExtra("customer_data");
            setDataToEdit();
        } else {
            customerData = new BeneficiaryCommonData();
            setMobileNumberDetails(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT, SthiramValues.DEFAULT_COUNTY_MOBILE_CODE, SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH, SthiramValues.DEFAULT_COUNTY_FLAG);
        }
        enableOfDisableButtons(true);
        setToolBar();
    }


    private void setDataToEdit() {
        String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), customerData.getMobileCountryCode());
        String countyFlag = apiCall.getFlagFromCountyList(commonFunctions.getCountryLists(), customerData.getMobileCountryCode());
        setMobileNumberDetails(mobileNumberFormat, customerData.getMobileCountryCode(), customerData.getMobileNumber().length() + "", countyFlag);
        edtMobile.setText(customerData.getMobileNumber());
        edtFirstName.setText(customerData.getFirstName());
        edtMiddleName.setText(customerData.getMiddleName());
        edtLastName.setText(customerData.getLastName());
        edtEmail.setText(customerData.getEmailAddress());
        beneficiaryId = customerData.getBeneficiaryId();
        beneficiaryIDImage = customerData.getBeneficiaryIdImage();
        btnAddDetails.setText(getString(R.string.update));
        oldMobileData = customerData.getMobileNumber();
        oldMobileCountryCode = customerData.getMobileCountryCode();
        String mName = customerData.getMiddleName();
        String email = customerData.getEmailAddress();
        if (mName == null) {
            mName = "";
        }
        if (email == null) {
            email = "";
        }
        oldUserData = customerData.getFirstName() + mName + customerData.getLastName() + email;
    }

    private void setMobileNumberDetails(String format, String code, String length, String flag) {
        setMobileNumberFormat(format);
        countyCode = code;
        tvCountryCode.setText(countyCode);
        mobileNumberLength = length;
        setFlag(flag);
    }

    private void visibleLoader() {
        progressBar.setVisibility(View.VISIBLE);
        mainScrollView.setVisibility(View.GONE);
    }

    private void hideLoader() {
        progressBar.setVisibility(View.GONE);
        mainScrollView.setVisibility(View.VISIBLE);
    }

    private void getAllBeneficiaries() {
        visibleLoader();
        apiCall.getBeneficiariesList(getBeneficiaryRetry, commonFunctions.getUserId() + "", false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    hideLoader();
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    BeneficiaryDataResponse apiResponse = gson.fromJson(jsonString, BeneficiaryDataResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        nonYeeluserList = apiResponse.getNonYeeluserList();
                        setDropDownList();
                    }
                } catch (Exception e) {
                    visibleLoader();
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
        enableOfDisableButtons(false);
        commonFunctions.hideKeyboard(AddBeneficiaryDetailsActivity.this);
        edtMobile.setText(beneficiary.getMobile());
        edtFirstName.setText(beneficiary.getFirstname());
        edtMiddleName.setText(beneficiary.getMiddlename());
        edtLastName.setText(beneficiary.getLastname());
        edtEmail.setText(beneficiary.getEmail());
        oldMobileData = beneficiary.getMobile();
        oldMobileCountryCode = beneficiary.getCountryCode();
        String mName = beneficiary.getMiddlename();
        String email = beneficiary.getEmail();
        if (mName == null) {
            mName = "";
        }
        if (email == null) {
            email = "";
        }
        oldUserData = beneficiary.getFirstname() + mName + beneficiary.getLastname() + email;
        beneficiaryId = beneficiary.getId();
        beneficiaryIDImage = beneficiary.getBeneficiaryIDImage();
    }

    private void enableOfDisableButtons(boolean b) {
        if (b) {
            edtFirstName.setEnabled(true);
            edtLastName.setEnabled(true);
            edtMiddleName.setEnabled(true);
            edtEmail.setEnabled(true);
        } else {
           /* if (!middleName.equals("")) {
                edtMiddleName.setEnabled(false);
            }
            if (!emailAddress.equals("")) {
                edtEmail.setEnabled(false);
            }*/
            edtMiddleName.setEnabled(false);
            edtEmail.setEnabled(false);
            edtFirstName.setEnabled(false);
            edtLastName.setEnabled(false);
        }

    }


    private void setItemListeners() {
        llMobileCountyCode.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type", "onboarding");
            startActivityForResult(in, SELECT_A_COUNTRY_CODE);

        });
        edtMobile.setOnItemClickListener((adapterView, view, position, id) -> {
            NonYeeluserListItem beneficiary = (NonYeeluserListItem) adapterView.getItemAtPosition(position);
            beneficiaryItemClick(beneficiary);
        });
        btnAddDetails.setOnClickListener(v -> {
            if (!oldMobileData.equals("")) {
                String currentUserdata = firstName + middleName + lastName + emailAddress;
                String fullMobileNumberOld = oldMobileCountryCode + oldMobileData;
                String fullMobileNumberNew = countyCode + mobileNumber;
                if (fullMobileNumberOld.equals(fullMobileNumberNew)) { // No change in mobile number
                    if (oldUserData.equals(currentUserdata)) { // No changes in user data. then no need to do anything
                        commonFunctions.logAValue("Button Action", "No need call API");
                        pageRedirection(beneficiaryId);
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


        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                firstName = edtFirstName.getText().toString().trim();
                if (edtFirstName.isFocused()) {
                    if (commonFunctions.validateFistName(firstName)) {
                        validFirstName = true;
                        tilFirstName.setError(null);
                    } else {
                        validFirstName = false;
                        if (tilFirstName.getError() == null || tilFirstName.getError().equals("")) {
                            tilFirstName.setError(getString(R.string.enter_valid_first_name));
                        }
                    }
                } else {
                    if (commonFunctions.validateFistName(firstName)) {
                        validFirstName = true;
                    } else {
                        validFirstName = false;
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
                if (edtMiddleName.isFocused()) {
                    if (commonFunctions.validateMiddleName(middleName)) {
                        validMiddleName = true;
                        tilMiddleName.setError(null);
                    } else {
                        validMiddleName = false;
                        if (tilMiddleName.getError() == null || tilMiddleName.getError().equals("")) {
                            tilMiddleName.setError(getString(R.string.enter_valid_middle_name));
                        }
                    }

                } else {
                    if (commonFunctions.validateMiddleName(middleName)) {
                        validMiddleName = true;
                    } else {
                        validMiddleName = false;
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
                if (edtLastName.isFocused()) {
                    if (commonFunctions.validateLastName(lastName)) {
                        validLastName = true;
                        tilLastName.setError(null);
                    } else {
                        validLastName = false;
                        if (tilLastName.getError() == null || tilLastName.getError().equals("")) {
                            tilLastName.setError(getString(R.string.enter_valid_last_name));
                        }
                    }

                } else {
                    if (commonFunctions.validateLastName(lastName)) {
                        validLastName = true;
                    } else {
                        validLastName = false;
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
                    clearAllFields();
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
                if (edtEmail.isFocused()) {
                    if (commonFunctions.isEmailValidOptional(emailAddress)) {
                        validEmail = true;
                        tilEmail.setError(null);
                    } else {
                        validEmail = false;
                        btnAddDetails.setEnabled(false);
                        if (tilEmail.getError() == null || tilEmail.getError().equals("")) {
                            tilEmail.setError(getString(R.string.enter_valid_email));
                        }
                    }

                } else {
                    if (commonFunctions.isEmailValidOptional(emailAddress)) {
                        validEmail = true;
                    } else {
                        validEmail = false;
                        btnAddDetails.setEnabled(false);
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void clearAllFields() {
        edtFirstName.setText(null);
        edtMiddleName.setText(null);
        edtLastName.setText(null);
        edtEmail.setText(null);
        edtFirstName.setEnabled(true);
        edtMiddleName.setEnabled(true);
        edtLastName.setEnabled(true);
        edtEmail.setEnabled(true);
        tilFirstName.setError(null);
        tilMiddleName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
    }


    private void checkAllValid() {
        if (validFirstName && validMiddleName && validLastName && validMobileNumber && validEmail && validCountry) {
            btnAddDetails.setBackgroundResource(R.drawable.bg_button_one);
            btnAddDetails.setEnabled(true);
        } else {
            btnAddDetails.setBackgroundResource(R.drawable.bg_button_three);
            btnAddDetails.setEnabled(false);
        }
    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        btnAddDetails = findViewById(R.id.tv_add_details);

        tilFirstName = findViewById(R.id.til_first_name);
        edtFirstName = findViewById(R.id.edt_first_name);
        tilMiddleName = findViewById(R.id.til_middle_name);
        edtMiddleName = findViewById(R.id.edt_middle_name);
        tilLastName = findViewById(R.id.til_last_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtMobile = findViewById(R.id.edt_mobile);
        tilEmail = findViewById(R.id.til_email);
        edtEmail = findViewById(R.id.edt_email);
        mainScrollView = findViewById(R.id.main_scrollView);
        progressBar = findViewById(R.id.progress_bar);

        mainScrollView.setVisibility(View.GONE);
        btnAddDetails.setEnabled(false);


        //county code
        tvCountryCode = findViewById(R.id.tv_county_code);
        ivFlag = findViewById(R.id.iv_flag_country_code);
        llMobileCountyCode = findViewById(R.id.ll_mobile_county_code);

        getAllBeneficiaries();

    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE + flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }

    private void setMobileNumberFormat(String format) {
        if (watcher != null) {
            edtMobile.removeTextChangedListener(watcher);
        }
        watcher = new TextFormatter(edtMobile, format);
        edtMobile.addTextChangedListener(watcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_A_COUNTRY_CODE) {
                setMobileNumberDetails(data.getStringExtra("format"), data.getStringExtra("country_code"), data.getStringExtra("mobile_number_length"), data.getStringExtra("flag"));
                setDropDownList();
                clearAll();
            }
        }
    }

    private void clearAll() {
        edtMobile.setText("");
        checkAllValid();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        if (isEditingMode) {
            if (customerType.equals("sender")) {
                tvTitle.setText(R.string.edit_sender_details);
            } else {
                tvTitle.setText(R.string.edit_receiver_details);
            }
        } else {
            if (customerType.equals("sender")) {
                tvTitle.setText(R.string.add_sender_details_title);
            } else {
                tvTitle.setText(R.string.add_receiver_details_title);
            }
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent in = getIntent();
            setResult(Activity.RESULT_CANCELED, in);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent in = getIntent();
        setResult(Activity.RESULT_CANCELED, in);
        finish();
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
                                pageRedirection(apiResponse.getId());
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
                                pageRedirection(apiResponse.getId());
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


    private void pageRedirection(String id) {
        if (customerType.equals("sender")) {
            if (from != null && from.equals("mobilePay") && beneficiaryIDImage != null && !beneficiaryIDImage.equals("")) {
                Intent in = new Intent(context, UploadSenderDocumentsActivity.class);
                in.putExtra("beneficiaryID", id);
                in.putExtra("beneficiaryIDImage", beneficiaryIDImage);
                in.putExtra("from", from);
                viewSenderIdActivityResultLauncher.launch(in);
            } else {
                if (beneficiaryIDImage != null && !beneficiaryIDImage.equals("")) {
                    setDataAndGoBack(id, beneficiaryIDImage);
                } else {
                    Intent in = new Intent(context, UploadSenderDocumentsActivity.class);
                    in.putExtra("beneficiaryID", id);
                    viewSenderIdActivityResultLauncher.launch(in);
                }
            }
        } else {
            setDataAndGoBack(id, beneficiaryIDImage);
        }
    }

    ActivityResultLauncher<Intent> viewSenderIdActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    String beneficiaryID = result.getData().getStringExtra("beneficiaryID");
                    String beneficiaryIDImage = result.getData().getStringExtra("beneficiaryIDImage");
                    setDataAndGoBack(beneficiaryID, beneficiaryIDImage);
                }
            });


    private void setDataAndGoBack(String beneficiaryId, String beneficiaryIDImage) {
        customerData.setFirstName(firstName);
        customerData.setMiddleName(middleName);
        customerData.setLastName(lastName);
        customerData.setMobileNumber(mobileNumber);
        customerData.setMobileCountryCode(countyCode);
        customerData.setEmailAddress(emailAddress);
        customerData.setCountryName(country);
        customerData.setBeneficiaryId(beneficiaryId);
        customerData.setBeneficiaryIdImage(beneficiaryIDImage);
        Intent in = getIntent();
        in.putExtra("customer_data", customerData);
        setResult(Activity.RESULT_OK, in);
        finish();
    }
}