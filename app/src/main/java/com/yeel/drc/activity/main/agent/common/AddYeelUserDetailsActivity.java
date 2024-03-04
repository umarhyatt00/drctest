package com.yeel.drc.activity.main.agent.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.activity.main.homepayandsend.ScanAndPayActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.TextFormatter;

import java.util.List;
import java.util.Objects;

public class AddYeelUserDetailsActivity extends BaseActivity {

    EditText etMobile;
    TextView btnAddDetails, textCustomerName, textCustomerMobile, textCustomerEmail, textCustomerEmailLabel;
    ConstraintLayout customerDetailLayout;
    ImageView imgScanQr;
    CommonFunctions commonFunctions;
    APICall apiCall;
    UserDetailsData userDetails;
    SomethingWentWrongDialog somethingWentWrongDialog;
    ErrorDialog errorDialog;
    String customerType;
    Context context;
    boolean getUserDetailsUsingMobileRetry = false;
    boolean validMobileNumber = false;
    String mobileNumber = "";
    String mobileNumberLength;
    String countyCode;
    ImageView ivFlag;
    TextView tvCountryCode;
    Group groupCountryCode;
    TextWatcher watcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender_details);
        context = AddYeelUserDetailsActivity.this;
        customerType = getIntent().getStringExtra("customer_type");
        setToolBar();
        initView();
        setItemListeners();
    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        apiCall = new APICall(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        etMobile = findViewById(R.id.edt_mobile);
        btnAddDetails = findViewById(R.id.tv_add_details);
        imgScanQr = findViewById(R.id.tv_logo);
        textCustomerName = findViewById(R.id.text_customer_name);
        textCustomerMobile = findViewById(R.id.text_customer_mobile);
        textCustomerEmail = findViewById(R.id.text_customer_email);
        textCustomerEmailLabel = findViewById(R.id.text_customer_email_label);
        customerDetailLayout = findViewById(R.id.customer_detail_layout);
        groupCountryCode = findViewById(R.id.groupCountryCode);
        setError();
        tvCountryCode = findViewById(R.id.tv_county_code);
        ivFlag = findViewById(R.id.iv_flag_country_code);
        setMobileNumberDetails(SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT, SthiramValues.DEFAULT_COUNTY_MOBILE_CODE, SthiramValues.DEFAULT_MOBILE_NUMBER_LENGTH, SthiramValues.DEFAULT_COUNTY_FLAG);
    }

    private void setMobileNumberDetails(String defaultMobileNumberFormat, String defaultCountyMobileCode, String defaultMobileNumberLength, String defaultCountyFlag) {
        setMobileNumberFormat(defaultMobileNumberFormat);
        countyCode = defaultCountyMobileCode;
        tvCountryCode.setText(countyCode);
        mobileNumberLength = defaultMobileNumberLength;
        setFlag(defaultCountyFlag);
    }

    private void setMobileNumberFormat(String format) {
        if (watcher != null) {
            etMobile.removeTextChangedListener(watcher);
        }
        watcher = new TextFormatter(etMobile, format);
        etMobile.addTextChangedListener(watcher);
    }

    private void setFlag(String flag) {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE + flag)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }


    private void setItemListeners() {
        int[] refIds = groupCountryCode.getReferencedIds();
        for (int id : refIds) {
            findViewById(id).setOnClickListener(view -> {
                Intent in = new Intent(context, CountrySelectionActivity.class);
                in.putExtra("type", "onboarding");
                countrySelectionListener.launch(in);
            });
        }

        imgScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScanAndPayActivity.class);
            intent.putExtra("from", "scan");
            paymentListener.launch(intent);
        });

        btnAddDetails.setOnClickListener(v -> {
            Intent in = getIntent();
            in.putExtra("customer_data", userDetails);
            setResult(Activity.RESULT_OK, in);
            finish();
        });

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String number = charSequence.toString().trim();
                mobileNumber = number.replaceAll("\\D", "");
                if (commonFunctions.validateMobileNumberDynamic(mobileNumber, mobileNumberLength)) {
                    if (!mobileNumber.equals(commonFunctions.getMobile())) {
                        getUserDetailsAPIMobile(mobileNumber);
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.canot_transfer), Toast.LENGTH_SHORT).show();
                        setError();
                    }
                } else {
                    setError();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getUserDetailsAPIMobile(String mobileNumber) {
        apiCall.getUserDetailsUsingMobile(getUserDetailsUsingMobileRetry, mobileNumber, countyCode, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        if (!apiResponse.getUserDetails().getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
                            List<CurrencyListData> currencyList = apiResponse.getCurrencyList();
                            boolean isAccountNumberAvailable = false;
                            String receiverAccountNumber = "";
                            if (currencyList != null) {
                                for (int i = 0; i < currencyList.size(); i++) {
                                    if (commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())) {
                                        receiverAccountNumber = currencyList.get(i).getAccount_number();
                                        isAccountNumberAvailable = true;
                                        break;
                                    }
                                }
                                if (isAccountNumberAvailable) {
                                    userDetails = apiResponse.getUserDetails();
                                    userDetails.setAccount_no(receiverAccountNumber);
                                    validMobileNumber = true;
                                    visibleCustomerDetails(userDetails);
                                    enableAddDetailsButton();
                                } else {
                                    validMobileNumber = false;
                                    setError();
                                    String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                                    errorDialog.show(message);
                                }
                            } else {
                                validMobileNumber = false;
                                setError();
                                String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                            }

                        } else {
                            errorDialog.show(getString(R.string.Rreceiver_cant_be_an_agent));
                        }
                    } else {
                        validMobileNumber = false;
                        setError();
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getUserDetailsUsingMobileRetry = true;
                getUserDetailsAPIMobile(mobileNumber);
            }
        });
    }

    private void getUserDetailsAPIQrCode(String qrCode) {
        apiCall.getUserDetailsUsingQrCode(getUserDetailsUsingMobileRetry, qrCode, true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        if (!apiResponse.getUserDetails().getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
                            List<CurrencyListData> currencyList = apiResponse.getCurrencyList();
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
                                    validMobileNumber = true;
                                    userDetails = apiResponse.getUserDetails();
                                    userDetails.setAccount_no(receiverAccountNumber);
                                    visibleCustomerDetails(userDetails);
                                    enableAddDetailsButton();
                                } else {
                                    validMobileNumber = false;
                                    String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                                    errorDialog.show(message);
                                    setError();
                                }

                            } else {
                                validMobileNumber = false;
                                String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                                setError();
                            }
                        } else {
                            errorDialog.show(getString(R.string.Rreceiver_cant_be_an_agent));
                        }
                    } else {
                        validMobileNumber = false;
                        setError();
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                    setError();
                }
            }
            @Override
            public void retry() {
                getUserDetailsUsingMobileRetry = true;
                getUserDetailsAPIQrCode(mobileNumber);
            }
        });
    }


    private void visibleCustomerDetails(UserDetailsData userDetails) {
        customerDetailLayout.setVisibility(View.VISIBLE);
        String fullName = commonFunctions.generateFullNameFromUserDetails(userDetails);
        textCustomerName.setText(fullName);
        //set mobile number
        String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), userDetails.getCountry_code() + "");
        textCustomerMobile.setText(commonFunctions.formatAMobileNumber(userDetails.getMobile(), userDetails.getCountry_code(), mobileNumberFormat));
        String email = userDetails.getEmail();
        if (email != null && !email.equals("")) {
            textCustomerEmail.setText(userDetails.getEmail());
            textCustomerEmailLabel.setVisibility(View.VISIBLE);
        } else {
            textCustomerEmailLabel.setVisibility(View.GONE);
        }

    }

    private void setError() {
        customerDetailLayout.setVisibility(View.GONE);
        btnAddDetails.setBackgroundResource(R.drawable.bg_button_three);
        btnAddDetails.setEnabled(false);
        validMobileNumber = false;
    }

    private void enableAddDetailsButton() {
        btnAddDetails.setBackgroundResource(R.drawable.bg_button_one);
        btnAddDetails.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Intent in = getIntent();
        setResult(Activity.RESULT_CANCELED, in);
        finish();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        if (customerType.equals("sender")) {
            tvTitle.setText(R.string.add_sender_details_title);
        } else {
            tvTitle.setText(R.string.add_receiver_details_title);
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

    ActivityResultLauncher<Intent> countrySelectionListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            etMobile.setText("");
            setError();
            assert result.getData() != null;
            setMobileNumberDetails(result.getData().getStringExtra("format"), result.getData().getStringExtra("country_code"), result.getData().getStringExtra("mobile_number_length"), result.getData().getStringExtra("flag"));
        }
    });

    ActivityResultLauncher<Intent> paymentListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String receiverQRCode = result.getData().getStringExtra("receiver_qr_code");
            if (receiverQRCode != null && !receiverQRCode.equals("")) {
                if (!receiverQRCode.equals(commonFunctions.getQRCode())) {
                    getUserDetailsAPIQrCode(receiverQRCode);
                } else {
                    errorDialog.show(getString(R.string.canot_transfer));
                }
            }
        }
    });

}