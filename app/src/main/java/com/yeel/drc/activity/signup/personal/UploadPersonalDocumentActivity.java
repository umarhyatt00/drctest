package com.yeel.drc.activity.signup.personal;
import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_INDIVIDUAL;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.CreatePINActivity;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.RectangleCropActivity;
import com.yeel.drc.activity.common.WebViewActivity;
import com.yeel.drc.activity.signup.SignUpSuccessActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.StandardResponsePojo;
import com.yeel.drc.api.kyciploadresponse.KYCUploadResponse;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.ExifUtils;
import com.yeel.drc.utils.RegisterFunctions;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class UploadPersonalDocumentActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    TextView tvContinue;
    RelativeLayout rlSignatureUpload;
    LinearLayout llSignatureImage;
    LinearLayout llBottom;
    ImageView ivSignature;
    ImageView ivSignatureClose;
    TextView tvTc;
    boolean createPinRetry=false;
    boolean validSignatureImage =false;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView ivBottomSheetClose;
    LinearLayout llGallery, llCamera;
    private String firstImageUrl;
    public static Bitmap bitmap;
    String idImageUrl ="";
    boolean fileUploaded=false;
    PermissionUtils permissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_personal_document);
        context= UploadPersonalDocumentActivity.this;
        signUpData=(SignUpData)getIntent().getSerializableExtra("signUpData");
        setToolBar();
        initView();
        if(signUpData==null){
            signUpData=new SignUpData();
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setFirstName(registerFunctions.getFirstName());
            signUpData.setMiddleName(registerFunctions.getMiddleName());
            signUpData.setLastName(registerFunctions.getLastName());
            signUpData.setUserId(registerFunctions.getUserId());
            signUpData.setPreApprovedLimit(registerFunctions.getSignUpPreApprovedLimitID());
        }
        setItemListeners();
    }

    private void initView() {
        permissionUtils=new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        dialogProgress = new DialogProgress(context);
        registerFunctions=new RegisterFunctions(context);
        clearSignUpProgressDialog=new ClearSignUpProgressDialog(context,commonFunctions);
        tvContinue=findViewById(R.id.tv_continue);
        rlSignatureUpload=findViewById(R.id.rl_signature_upload);
        llSignatureImage=findViewById(R.id.ll_signature_image);
        ivSignature=findViewById(R.id.iv_signature);
        ivSignatureClose=findViewById(R.id.iv_signature_close);
        llBottom=findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);
        tvTc=findViewById(R.id.tv_t_c);
        String tc = getString(R.string.tc_one)+" <font size='5' color='#5463E8'>"+getString(R.string.tc_two)+"</font>";
        tvTc.setText(Html.fromHtml(tc));
        createBottomSheet();
    }

    @SuppressLint("InflateParams")
    private void createBottomSheet() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.gallery_option, null);
        ivBottomSheetClose = bottomSheetView.findViewById(R.id.iv_close);
        llGallery = bottomSheetView.findViewById(R.id.ll_gallery);
        llCamera = bottomSheetView.findViewById(R.id.ll_camera);
        bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetDialogCurved);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
    }

    private void setItemListeners() {
        tvContinue.setOnClickListener(view -> {
            if(fileUploaded){
                Intent in = new Intent(context, CreatePINActivity.class);
                in.putExtra("from", "create");
                requestLauncherPIN.launch(in);
            }else{
                saveKYCDetails("0");
            }
        });
        rlSignatureUpload.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
        });
        ivBottomSheetClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        llGallery.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if(permissionUtils.checkGalleryPermission(requestPermissionLauncherGallery)){
                openGallery();
            }
        });
        llCamera.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if(permissionUtils.checkCameraPermission(requestPermissionLauncherCamera)){
                dispatchTakePictureIntent();
            }
        });

        ivSignatureClose.setOnClickListener(view -> {
            idImageUrl ="";
            validSignatureImage=false;
            rlSignatureUpload.setVisibility(View.VISIBLE);
            llSignatureImage.setVisibility(View.GONE);
            checkAllValid();
        });
        tvTc.setOnClickListener(view -> {
            Intent in=new Intent(context, WebViewActivity.class);
            in.putExtra("type","t_c");
            startActivity(in);
        });

    }

    private void checkAllValid() {
        if(validSignatureImage){
            llBottom.setVisibility(View.VISIBLE);
        }else{
            llBottom.setVisibility(View.GONE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ignored) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.yeel.drc.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                requestLauncherCamera.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        firstImageUrl = image.getAbsolutePath();
        return image;
    }
    private void generateBitmapImageFromURL(String imageUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmapSample = BitmapFactory.decodeFile(imageUrl, options);
        bitmap= ExifUtils.rotateBitmap(imageUrl,bitmapSample);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.upload_your_documents);
        RelativeLayout rlSkip=findViewById(R.id.rl_skip);
        rlSkip.setVisibility(View.VISIBLE);
        rlSkip.setOnClickListener(view -> saveKYCDetails("1"));
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


    private void createPin(String pin) {
        int preApproved = 0;
        if (signUpData.getAccountType().equals(ACCOUNT_TYPE_INDIVIDUAL)) {
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
                        errorDialog.show(apiResponse.getMessage());

                    }
                } catch (Exception e) {
                    errorDialog.show("Something went wrong!!");
                }
            }
            @Override
            public void retry() {
                createPinRetry=true;
                createPin(pin);
            }
        });
    }

    private void saveKYCDetails(String type) {
        try{
            dialogProgress.show();
            Retrofit retrofit = apiCall.createRetrofitObjectForUploadImage(signUpData.getUserId(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "1",
                    SthiramValues.USER_URL,
                    type);
            MultipartBody.Part bodyIdImage = null;
            if(type.equals("0")){
                if (!idImageUrl.equals("")) {
                    File file = new File(idImageUrl);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                    bodyIdImage = MultipartBody.Part.createFormData("id_image", file.getName(), requestFile);
                }
            }
            RequestBody requestUserId = RequestBody.create(MediaType.parse("text/plain"), "0");
            retrofit.create(APIInterface.class).saveKYCDetails(bodyIdImage,
                    null, null,requestUserId)
                    .enqueue(new Callback<CommonResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<CommonResponse> call, @NonNull retrofit2.Response<CommonResponse> response) {
                            if (response.isSuccessful()) {
                                dialogProgress.dismiss();
                                try{
                                    assert response.body() != null;
                                    String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                                    Gson gson = new Gson();
                                    KYCUploadResponse apiResponse = gson.fromJson(jsonString, KYCUploadResponse.class);
                                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                        fileUploaded=true;
                                        signUpData.setIdUrl(apiResponse.getId_image());
                                        signUpData.setKycId(apiResponse.getYeel_kyc_id());
                                        registerFunctions.saveRegisterDetails(signUpData);
                                        commonFunctions.logAValue("Pre KYC ID",signUpData.getKycId());
                                        Intent in = new Intent(context, CreatePINActivity.class);
                                        in.putExtra("from", "create");
                                        requestLauncherPIN.launch(in);
                                    } else {
                                        fileUploaded=false;
                                        errorDialog.show(apiResponse.getMessage());
                                    }
                                }catch (Exception e){
                                    fileUploaded=false;
                                    errorDialog.show("Failed to upload file");
                                }

                            } else {
                                fileUploaded=false;
                                dialogProgress.dismiss();
                                if (!somethingWentWrongDialog.isShowing()) {
                                    somethingWentWrongDialog.show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<CommonResponse> call, Throwable t) {
                            Log.e("Error",t+"");
                            fileUploaded=false;
                            dialogProgress.dismiss();
                            if (!somethingWentWrongDialog.isShowing()) {
                                somethingWentWrongDialog.show();
                            }
                        }
                    });
        }catch (Exception ignored){

        }

    }

    private void openGallery() {
        Intent in = new Intent(context, ImageBrowse.class);
        requestLauncherGallery.launch(in);
    }

    private void callCropIntent() {
        Intent in = new Intent(context, RectangleCropActivity.class);
        in.putExtra("from","upload_personal_id");
        in.putExtra("type","G");
        requestLauncherImageCrop.launch(in);
    }


    private final ActivityResultLauncher<String> requestPermissionLauncherCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    dispatchTakePictureIntent();
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncherGallery =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                }
            });

    ActivityResultLauncher<Intent>  requestLauncherGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String imageUrl = result.getData().getStringExtra("imageURL");
            generateBitmapImageFromURL(imageUrl);
            callCropIntent();
        }
    });



    ActivityResultLauncher<Intent> requestLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            generateBitmapImageFromURL(firstImageUrl);
            callCropIntent();
        }
    });

    ActivityResultLauncher<Intent> requestLauncherImageCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String url=result.getData().getStringExtra("imageURL");
            String reTake=result.getData().getStringExtra("ReTake");
            if(reTake.equals("Yes")){
                dispatchTakePictureIntent();
            }else{
                idImageUrl = url;
                validSignatureImage = true;
                rlSignatureUpload.setVisibility(View.GONE);
                llSignatureImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .into(ivSignature);
                checkAllValid();
            }
        }
    });


    ActivityResultLauncher<Intent> requestLauncherPIN = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String pin = result.getData().getStringExtra("pin");
            createPin(pin);
        }
    });



}