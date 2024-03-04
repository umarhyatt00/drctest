package com.yeel.drc.activity.signup;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_INDIVIDUAL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.activity.common.CreatePINActivity;
import com.yeel.drc.adapter.DistrictListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.idtypes.IDTypeResponse;
import com.yeel.drc.api.kyciploadresponse.KYCUploadResponse;
import com.yeel.drc.api.login.LoginWithPINResponse;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.RegisterFunctions;
import com.yeel.drc.utils.TextFormatter;
import java.io.File;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class KYCDetailsActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    RelativeLayout rlCountryName;
    TextInputEditText edtType;
    RelativeLayout rlIdType;
    TextInputLayout tilIdNumber;
    TextInputEditText edtIdNumber;
    TextInputEditText edtIdExpiryDate;
    TextInputLayout tilExpiryDate;
    TextView tvContinue;
    TextView tvCountryName;
    ImageView ivFlag;
    String country = "";
    String countryFlagImage="";
    String idType = "";
    List<String> idTypeList;
    String idNumber;
    String idExpiryDate;
    boolean validCountry = true;
    boolean validIdType = false;
    boolean validIdNumber = false;
    boolean validIdExpiryDate = false;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView mRecyclerIncome;
    private TextView mTvTitle;
    DistrictListAdapter mBottonAdapter;
    boolean createPinRetry=false;
    boolean getIDTypesRetry=false;
    boolean loginWithPINAPIRetry=false;
    MultipleLoginDialog multipleLoginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_y_c_details);
        context = KYCDetailsActivity.this;
        setToolBar();
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        signUpData = (SignUpData) getIntent().getSerializableExtra("signUpData");
        registerFunctions = new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        if (signUpData == null) {
            signUpData = new SignUpData();
            signUpData.setUserId(registerFunctions.getUserId());
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setIdUrl(registerFunctions.getIdURL());
            signUpData.setSelfieUrl(registerFunctions.getSelfieURL());
            signUpData.setSignatureUrl(registerFunctions.getSignatureURL());
            signUpData.setPin(registerFunctions.getPIN());
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setKycId(registerFunctions.getKycID());
        }
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlCountryName.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type","kyc");
            countrySelectionListener.launch(in);
        });
        rlIdType.setOnClickListener(view -> {
            if(idTypeList!=null&&idTypeList.size()!=0){
                setIdTypeList();
            }
        });
        edtIdNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idNumber = Objects.requireNonNull(edtIdNumber.getText()).toString().trim();
                if (commonFunctions.validateKYCIDNumber(idNumber)) {
                    validIdNumber = true;
                    tilIdNumber.setError(null);
                } else {
                    validIdNumber = false;
                    if (tilIdNumber.getError() == null || tilIdNumber.getError().equals("")) {
                        tilIdNumber.setError(getString(R.string.enter_valid_id_number));
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvContinue.setOnClickListener(view -> saveKYCDetails());

        edtIdExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idExpiryDate = Objects.requireNonNull(edtIdExpiryDate.getText()).toString().trim();
                if (commonFunctions.validateExpiryDate(idExpiryDate)) {
                    validIdExpiryDate = true;
                    tilExpiryDate.setError(null);
                } else {
                    if (tilExpiryDate.getError() == null || tilExpiryDate.getError().equals("")) {
                        tilExpiryDate.setError(getString(R.string.enter_expiry_date));
                    }
                    validIdExpiryDate = false;
                }
                checkAllValid();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void saveKYCDetails() {
        try {
            dialogProgress.show();

            String Preapproved="0";
            if(signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)){
                Preapproved="2";
            }

            Retrofit retrofit = apiCall.createRetrofitObjectForUploadImage(signUpData.getUserId(),
                    idType,
                    idNumber,
                    country,
                    countryFlagImage,
                    commonFunctions.dobFormat(idExpiryDate),
                    "",
                    registerFunctions.getKycID()+"",
                    Preapproved,
                    SthiramValues.USER_URL,
                    "0");
            MultipartBody.Part bodyIdImage = null;
            if (!signUpData.getIdUrl().equals("") && signUpData.getIdUrl() != null) {
                if(!signUpData.getIdUrl().contains("http")){
                    File file = new File(signUpData.getIdUrl());
                    RequestBody requestFile = RequestBody.create(file,MediaType.parse("image/*"));
                    bodyIdImage = MultipartBody.Part.createFormData("id_image", file.getName(), requestFile);
                }else{
                    commonFunctions.logAValue("KYC ID status","No need to upload");
                }
            }

            MultipartBody.Part bodySelfieImage = null;
            if (!signUpData.getSelfieUrl().equals("") && signUpData.getSelfieUrl() != null) {
                File file = new File(signUpData.getSelfieUrl());
                RequestBody requestFile = RequestBody.create(file,MediaType.parse("image/*"));
                bodySelfieImage = MultipartBody.Part.createFormData("selfie_image", file.getName(), requestFile);
            }

            MultipartBody.Part bodySignatureImage = null;
            if (!signUpData.getSignatureUrl().equals("") && signUpData.getSignatureUrl() != null) {
                File file = new File(signUpData.getSignatureUrl());
                RequestBody requestFile = RequestBody.create(file,MediaType.parse("image/*"));
                bodySignatureImage = MultipartBody.Part.createFormData("signature_image", file.getName(), requestFile);
            }

            retrofit.create(APIInterface.class).saveKYCDetails(bodyIdImage,
                    bodySelfieImage, bodySignatureImage,null
            )
                    .enqueue(new Callback<CommonResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CommonResponse> call, @NonNull retrofit2.Response<CommonResponse> response) {
                            if (response.isSuccessful()) {
                                dialogProgress.dismiss();
                                assert response.body() != null;
                                String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                                Gson gson = new Gson();
                                KYCUploadResponse apiResponse = gson.fromJson(jsonString, KYCUploadResponse.class);
                                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                    signUpData.setIssuingCountry(country);
                                    signUpData.setIdType(idType);
                                    signUpData.setIdNumber(idNumber);
                                    signUpData.setIdExpiryDate(idExpiryDate);
                                    registerFunctions.saveRegisterDetails(signUpData);
                                    if(signUpData.getAccountType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)){
                                        commonFunctions.setPage("");
                                        kycSubmittedSuccessDialog();
                                    }else{
                                        commonFunctions.setPage("kyc_details");
                                        Intent in = new Intent(context, CreatePINActivity.class);
                                        in.putExtra("from","create");
                                        pinVerificationListener.launch(in);
                                    }
                                } else {
                                    errorDialog.show(apiResponse.getMessage());
                                }
                            } else {
                                dialogProgress.dismiss();
                                if (!somethingWentWrongDialog.isShowing()) {
                                    somethingWentWrongDialog.show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CommonResponse> call, @NonNull Throwable t) {
                            dialogProgress.dismiss();
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    });
        }catch (Exception ignored){

        }

    }

    private void kycSubmittedSuccessDialog() {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_submited_success);
        TextView textOK = success.findViewById(R.id.text_ok);

        textOK.setOnClickListener(v -> {
            success.dismiss();
            callLoginAPI();
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }


    private void callLoginAPI() {
        apiCall.loginWithPINAPI(loginWithPINAPIRetry, signUpData.getMobileNumber(),signUpData.getCountryCode()+"", signUpData.getPin(), commonFunctions.getFCMToken(), true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    LoginWithPINResponse apiResponse = gson.fromJson(jsonString, LoginWithPINResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.clearLocalStorage();
                        commonFunctions.setUserDetailsAfterLogin(apiResponse.getData().getAccess_token(), apiResponse.getData().getUser_details());
                        commonFunctions.callHomeIntent();
                    } else {
                        if(apiResponse.getError_type().equals(SthiramValues.ERROR_TYPE_MULTIPLE_LOGIN)){
                            if(!multipleLoginDialog.isShowing()){
                                multipleLoginDialog.show();
                            }
                        }else{
                            if (!errorDialog.isShowing()) {
                                errorDialog.show(apiResponse.getMessage());
                            }
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
                loginWithPINAPIRetry = true;
                callLoginAPI();
            }
        });
    }

    private void initView() {
        multipleLoginDialog=new MultipleLoginDialog(context, SthiramValues.finish);
        tvCountryName=findViewById(R.id.tv_country_name);
        ivFlag=findViewById(R.id.iv_flag);
        rlCountryName = findViewById(R.id.rl_country_name);
        edtType = findViewById(R.id.edt_id_type);
        edtType.setEnabled(false);
        rlIdType = findViewById(R.id.rl_id_type);
        tilIdNumber = findViewById(R.id.til_id_number);
        edtIdNumber = findViewById(R.id.edt_id_number);
        tilExpiryDate = findViewById(R.id.til_expiry_date);
        edtIdExpiryDate = findViewById(R.id.edt_expiry_date);
        edtIdExpiryDate.addTextChangedListener(new TextFormatter(edtIdExpiryDate, "XX/XX/XXXX"));
        tvContinue = findViewById(R.id.tv_continue);
        country= SthiramValues.DEFAULT_COUNTY_NAME;
        countryFlagImage= SthiramValues.DEFAULT_COUNTY_FLAG;
        tvCountryName.setText(country);
        setFlag();
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetDialogCurved);
        ImageView ivClose = bottomSheetView.findViewById(R.id.iv_close);
        bottomSheetDialog.setContentView(bottomSheetView);
        mRecyclerIncome = bottomSheetView.findViewById(R.id.rvTxnDate);
        mTvTitle = bottomSheetView.findViewById(R.id.income_title);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerIncome.setLayoutManager(mLayoutManager);
        ivClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        getIDTypeList();
    }
    private void getIDTypeList() {
        apiCall.getIDTypes(getIDTypesRetry,signUpData.getAccountType(),country,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    IDTypeResponse apiResponse=gson.fromJson(jsonString, IDTypeResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        idTypeList=apiResponse.getId_types();
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
                getIDTypesRetry=true;
                getIDTypeList();
            }
        });
    }

    private void setIdTypeList() {
        bottomSheetDialog.show();
        mTvTitle.setText(getString(R.string.id_type));
        mBottonAdapter = new DistrictListAdapter(idTypeList, context,(v, position) ->  {
            bottomSheetDialog.dismiss();
            idType = idTypeList.get(position);
            edtType.setText(idType);
            validIdType = true;
            if(idType.equals("Nationality Certificate")){
                idExpiryDate="03/03/2222";
                validIdExpiryDate = true;
                tilExpiryDate.setVisibility(View.GONE);
            }else {
                validIdExpiryDate = false;
                tilExpiryDate.setVisibility(View.VISIBLE);
            }
            checkAllValid();
        });
        mRecyclerIncome.setAdapter(mBottonAdapter);
    }

    private void checkAllValid() {
        if (validCountry && validIdType && validIdNumber && validIdExpiryDate) {
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.document_detail);
        RelativeLayout rlSkip=findViewById(R.id.rl_skip);
        rlSkip.setVisibility(View.VISIBLE);
        rlSkip.setOnClickListener(view -> {
            idType = "";
            idNumber = "";
            country = "";
            countryFlagImage="";
            idExpiryDate = "";
            saveKYCDetails();
        });
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isTaskRoot()) {
                clearSignUpProgressDialog.show();
            } else {
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

    ActivityResultLauncher<Intent> pinVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String pin = result.getData().getStringExtra("pin");
            createPin(pin);
        }
    });


    ActivityResultLauncher<Intent> countrySelectionListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            country = result.getData().getStringExtra("country_name");
            countryFlagImage= result.getData().getStringExtra("flag");
            validCountry = true;
            checkAllValid();
            setFlag();
            tvCountryName.setText(country);
            getIDTypeList();
        }
    });



    private void setFlag() {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+countryFlagImage)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }

    private void createPin(String pin) {
        int preApproved = 0;
        if ( signUpData.getAccountType().equals(ACCOUNT_TYPE_INDIVIDUAL)) {
            preApproved = 1;
        }
        apiCall.createPin(createPinRetry,signUpData.getCurrencyId(), signUpData.getUserId(), pin, signUpData.getCountryCode() + signUpData.getMobileNumber(), preApproved,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    StandardResponsePojo apiResponse = gson.fromJson(jsonString, StandardResponsePojo.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        commonFunctions.setPage("");
                        signUpData.setPin(pin);
                        registerFunctions.saveRegisterDetails(signUpData);
                        Intent intent = new Intent(context, SignUpSuccessActivity.class);
                        intent.putExtra("signUpData", signUpData);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        mErrorDialogue(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    mErrorDialogue("Something went wrong!!");
                }
            }

            @Override
            public void retry() {
                createPinRetry=true;
                createPin(pin);
            }
        });
    }

    private void mErrorDialogue(String Message) {
       final Dialog mErrorDialogue = new Dialog(this);
        mErrorDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mErrorDialogue.setCancelable(false);
        mErrorDialogue.setContentView(R.layout.dialog_style_error);
        mErrorDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mErrorDialogue.setCanceledOnTouchOutside(true);
        TextView btnOkay = mErrorDialogue.findViewById(R.id.tv_ok);
        TextView tvMessage = mErrorDialogue.findViewById(R.id.tv_message);
        tvMessage.setText(Message);
        btnOkay.setOnClickListener(view -> {
            Intent in = new Intent(context, CreatePINActivity.class);
            in.putExtra("from","create");
            pinVerificationListener.launch(in);
        });

        if (!isFinishing()||!isDestroyed()) {
            mErrorDialogue.show();
        }

    }

}