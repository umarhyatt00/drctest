package com.yeel.drc.activity.main.settings.kyc;

import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CountrySelectionActivity;
import com.yeel.drc.adapter.DistrictListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.basicdetails.KYCDetailsData;
import com.yeel.drc.api.idtypes.IDTypeResponse;
import com.yeel.drc.api.kyciploadresponse.KYCUploadResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.TextFormatter;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ViewKYCDetailsActivity extends BaseActivity {
    KYCDetailsData kycDetails;
    Context context;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    ImageView ivFlag;

    RelativeLayout rlCountryName;
    TextInputEditText edtType;
    RelativeLayout rlIdType;
    TextInputLayout tilIdNumber;
    TextInputEditText edtIdNumber;
    TextInputEditText edtIdExpiryDate;
    TextInputLayout tilExpiryDate;
    TextView tvContinue;
    TextView tvCountyName;

    String country = "";
    String countryFlagImage="";
    String idType = "";
    List<String> idTypeList;
    String idNumber;
    String idExpiryDate;

    boolean validCountry = true;
    boolean validIdType = true;
    boolean validIdNumber = true;
    boolean validIdExpiryDate = true;

    final static int SELECT_A_STATE= 102;

    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;
    private ImageView ivClose;
    private RecyclerView mRecyclerIncome;
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayoutManager mLayoutManager;
    private TextView mTvTitle;
    DistrictListAdapter mBottonAdapter;
    boolean getIDTypesRetry=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submited_kycdetails);
        setToolBar();
        context= ViewKYCDetailsActivity.this;
        kycDetails=(KYCDetailsData)getIntent().getSerializableExtra("kycDetails");
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlCountryName.setOnClickListener(view -> {
            Intent in = new Intent(context, CountrySelectionActivity.class);
            in.putExtra("type","kyc");
            startActivityForResult(in, SELECT_A_STATE);
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
                idNumber = edtIdNumber.getText().toString().trim();
                if (commonFunctions.validateKYCIDNumber(idNumber)) {
                    validIdNumber = true;
                    tilIdNumber.setError(null);
                } else {
                    validIdNumber = false;
                    if (tilIdNumber.getError() == null || tilIdNumber.getError().equals("")) {
                        tilIdNumber.setError("Enter valid ID Number");
                    }
                }
                checkAllValid();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        tvContinue.setOnClickListener(view -> {
            saveKYCDetails();
        });

        edtIdExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                idExpiryDate = edtIdExpiryDate.getText().toString().trim();
              //  Log.e("Value",idExpiryDate+"");
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

    private void initView() {
        ivFlag=findViewById(R.id.iv_flag);
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);


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
        tvCountyName=findViewById(R.id.tv_country_name);

        bottomSheetView = getLayoutInflater().inflate(R.layout.custom_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetDialogCurved);
        ivClose = bottomSheetView.findViewById(R.id.iv_close);
        bottomSheetDialog.setContentView(bottomSheetView);
        mRecyclerIncome = bottomSheetView.findViewById(R.id.rvTxnDate);
        mTvTitle = bottomSheetView.findViewById(R.id.income_title);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerIncome.setLayoutManager(mLayoutManager);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        setValues();
        getIDTypeList();
    }

    private void setValues() {
        if(commonFunctions.getKYCStatus().equals(SthiramValues.KYC_STATUS_EXPIRED)
                ||commonFunctions.getKYCStatus().equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)||
                commonFunctions.getKYCStatus().equals(SthiramValues.KYC_STATUS_PRE_APPROVED)
        ){
            validCountry = true;
            validIdType = false;
            validIdNumber = false;
            validIdExpiryDate = false;

            country= SthiramValues.DEFAULT_COUNTY_NAME;
            countryFlagImage= SthiramValues.DEFAULT_COUNTY_FLAG;
            idType="";
            idNumber="";
            idExpiryDate="";

        }else{
            try{
                validCountry = true;
                validIdType = true;
                validIdNumber = true;
                validIdExpiryDate = true;

                //set country details
                country = kycDetails.getIssuing_country();
                countryFlagImage= kycDetails.getIssuing_country_flag();
                idType=kycDetails.getId_type();
                idNumber=kycDetails.getId_number();

                if(idType.equals("Nationality Certificate")){
                    idExpiryDate="03/03/2222";
                    tilExpiryDate.setVisibility(View.GONE);
                }else {
                    idExpiryDate=commonFunctions.dobFormattwo(kycDetails.getExpiryDate());
                    edtIdExpiryDate.setText(idExpiryDate);
                    tilExpiryDate.setVisibility(View.VISIBLE);
                }
                validIdExpiryDate = true;
                edtType.setText(idType);
                edtIdNumber.setText(idNumber);

            }catch (Exception e){
                validCountry = true;
                validIdType = false;
                validIdNumber = false;
                validIdExpiryDate = false;

                country= SthiramValues.DEFAULT_COUNTY_NAME;
                countryFlagImage= SthiramValues.DEFAULT_COUNTY_FLAG;
                idType="";
                idNumber="";
                idExpiryDate="";
            }

        }
        tvCountyName.setText(country);
        setFlag();
        checkAllValid();
    }

    private void setFlag() {
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+countryFlagImage)
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(ivFlag);
    }

    private void saveKYCDetails() {
        dialogProgress.show();
        String previousKYCId="";
        String yeelKYCId="";
        String preApproved="";
        if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)&&(commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_YES))){
            preApproved="2";
        }else if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)&&(commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_RESUBMIT))){
             preApproved="0";
        }else if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)&&
                (commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_REJECTED)||
                commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_RESUBMITTED))){
            preApproved="3";
        }else{
            preApproved="0";
        }

      /*  if(commonFunctions.getUserType().equals(Constants.ACCOUNT_TYPE_INDIVIDUAL)&&
                (commonFunctions.getPreApproved().equals(Constants.PRE_APPROVED_STATUS_YES)||
                commonFunctions.getPreApproved().equals(Constants.PRE_APPROVED_STATUS_REJECTED)||
                commonFunctions.getPreApproved().equals(Constants.PRE_APPROVED_STATUS_RESUBMITTED))){
            preApproved="2";
        }else{
            preApproved="0";
        }*/

        if(commonFunctions.getKYCStatus().equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)){
            previousKYCId="";
            yeelKYCId="";
        }else if(commonFunctions.getKYCStatus().equals(SthiramValues.KYC_STATUS_EXPIRED)){
            yeelKYCId="";
            previousKYCId=commonFunctions.getKYCId();
        }else{
            yeelKYCId=commonFunctions.getKYCId();
            previousKYCId="";
        }


        String date="";
        if(!idExpiryDate.equals("")){
            date=commonFunctions.dobFormat(idExpiryDate);
        }

        
        Retrofit retrofit = apiCall.createRetrofitObjectForUploadImage(commonFunctions.getUserId(),
                idType,
                idNumber,
                country,
                countryFlagImage,
                date,
                previousKYCId+"",
                yeelKYCId+"",
                preApproved+"",
                SthiramValues.USER_URL,
                "0");
        //RequestBody requestUserId = RequestBody.create(MediaType.parse("text/plain"), commonFunctions.getUserId());
        MultipartBody.Part bodyIdImage = null;
        if (!kycDetails.getId_image().equals("") && kycDetails.getId_image() != null) {
            if(kycDetails.getId_image().contains("com.yeel.drc")){
                File file = new File(kycDetails.getId_image());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                bodyIdImage = MultipartBody.Part.createFormData("id_image", file.getName(), requestFile);
            }else{
                commonFunctions.logAValue("ID Image","No");
            }
        }

        MultipartBody.Part bodySelfieImage = null;
        if (!kycDetails.getSelfie_img().equals("") && kycDetails.getSelfie_img()!= null) {
            if(kycDetails.getSelfie_img().contains("com.yeel.drc")){
                File file = new File(kycDetails.getSelfie_img());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                bodySelfieImage = MultipartBody.Part.createFormData("selfie_image", file.getName(), requestFile);
            }else{
                commonFunctions.logAValue("Selfie Image","No");
            }
        }

        MultipartBody.Part bodySignatureImage = null;
        if (!kycDetails.getSignature_img().equals("") &&kycDetails.getSignature_img() != null) {
            if(kycDetails.getSignature_img().contains("com.yeel.drc")){
                File file = new File(kycDetails.getSignature_img());
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                bodySignatureImage = MultipartBody.Part.createFormData("signature_image", file.getName(), requestFile);
            }else{
                commonFunctions.logAValue("Signature Image","No");
            }

        }

        retrofit.create(APIInterface.class).saveKYCDetails(bodyIdImage,
                bodySelfieImage, bodySignatureImage,null)
                .enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                        if (response.isSuccessful()) {
                            try{
                                dialogProgress.dismiss();
                                String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                                Gson gson = new Gson();
                                KYCUploadResponse apiResponse = gson.fromJson(jsonString, KYCUploadResponse.class);
                                if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                    commonFunctions.setKYCStatus(apiResponse.getKyc_status());
                                    commonFunctions.setPreApproved(apiResponse.getUser_details().getPreapproved());
                                 kycSubmittedSuccessDialog();
                                } else {
                                    errorDialog.show(apiResponse.getMessage());
                                }
                            }catch (Exception e){
                                dialogProgress.dismiss();
                                if (!somethingWentWrongDialog.isShowing()) {
                                    somethingWentWrongDialog.show();
                                }
                            }
                        } else {
                            dialogProgress.dismiss();
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        dialogProgress.dismiss();
                        if (!somethingWentWrongDialog.isShowing()) {
                            somethingWentWrongDialog.show();
                        }
                    }
                });
    }

    private void getIDTypeList() {
        String accountType="";
        if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
            accountType=commonFunctions.getAgentType();
        }else{
            accountType=commonFunctions.getUserType();
        }
        apiCall.getIDTypes(getIDTypesRetry,accountType,country,true, new CustomCallback() {
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

    private void kycSubmittedSuccessDialog() {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_submited_success);
        TextView textOK = success.findViewById(R.id.text_ok);

        textOK.setOnClickListener(v -> {
            success.dismiss();
            if(isTaskRoot()){
                commonFunctions.callHomeIntent();
            }else{
                Intent in = getIntent();
                setResult(Activity.RESULT_OK, in);
                finish();
            }
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode==SELECT_A_STATE) {
                country = data.getStringExtra("country_name");
                countryFlagImage= data.getStringExtra("flag");
                validCountry = true;
                checkAllValid();
                setFlag();
                tvCountyName.setText(country);
                getIDTypeList();
            }
        }

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
        tvTitle.setText(R.string.doc_details);
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
            finish();
            return true;
        }
        return false;
    }
}