package com.yeel.drc.activity.main.settings.kyc;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.RectangleCropActivity;
import com.yeel.drc.activity.common.SqureCropActivity;
import com.yeel.drc.activity.login.ReturningUserActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.basicdetails.KYCDetailsData;
import com.yeel.drc.api.kycdetails.KYCDetailsResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.ExifUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewKYCImagesActivity extends BaseActivity {
    Context context;
    RelativeLayout rlIdUpload;
    LinearLayout llIdImage;
    RelativeLayout rlSelfieUplaod;
    LinearLayout llSelfieImage;
    RelativeLayout rlSignatureUpload;
    LinearLayout llSignatureImage;
    LinearLayout llBottom;
    TextView tvTc;
    ImageView ivId;
    ImageView ivSelfie;
    ImageView ivSignature;
    ImageView ivIdClose;
    ImageView ivSelfieClose;
    ImageView ivSignatureClose;
    TextView tvContinue;
    ScrollView scrollView;
    ProgressBar progressBar;
    boolean validIdImage = false;
    boolean validSelfieImage = false;
    boolean validSignatureImage = false;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView ivBottomSheetClose;
    LinearLayout llGallery, llCamera;
    String selectedType = "";
    private String firstImageUrl;
    public static Bitmap bitmap;
    String idImageUrl = "";
    String selfieImageUrl = "";
    String signatureImageUrl = "";
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    KYCDetailsData kycDetails;
    boolean getKYCDetailsRetry = false;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_kycdoc_images);
        setToolBar();
        context = ViewKYCImagesActivity.this;
        initView();
        setItemListeners();
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);
        rlIdUpload = findViewById(R.id.rl_id_upload);
        llIdImage = findViewById(R.id.ll_id_image);
        ivId = findViewById(R.id.iv_id);
        ivIdClose = findViewById(R.id.iv_id_close);
        rlSelfieUplaod = findViewById(R.id.rl_selfie_upload);
        llSelfieImage = findViewById(R.id.ll_selfi_image);
        ivSelfie = findViewById(R.id.iv_selfi);
        ivSelfieClose = findViewById(R.id.iv_selfi_close);
        rlSignatureUpload = findViewById(R.id.rl_signature_upload);
        llSignatureImage = findViewById(R.id.ll_signature_image);
        ivSignature = findViewById(R.id.iv_signature);
        ivSignatureClose = findViewById(R.id.iv_signature_close);
        llBottom = findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);
        tvTc = findViewById(R.id.tv_t_c);
        scrollView = findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String tc = getString(R.string.tc_one) + " <font size='5' color='#5463E8'>" + getString(R.string.tc_two) + "</font>";
        tvTc.setText(Html.fromHtml(tc));
        tvContinue = findViewById(R.id.tv_continue);
        createBottomSheet();
        if (getIntent().hasExtra("isFromNotification")) {
            if (getIntent().getBooleanExtra("isFromNotification", false)) {
                if (!commonFunctions.getLogoutStatus() && commonFunctions.getLoginStatus()) {
                    Intent intent = new Intent(context, ReturningUserActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    getBasicDetails();
                }
            }
        } else {
            getBasicDetails();
        }
    }

    private void setValues() {
        if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)
                && commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_YES)) {
            idImageUrl = kycDetails.getId_image();
            selfieImageUrl = "";
            signatureImageUrl = "";
        } else {
            idImageUrl = kycDetails.getId_image();
            selfieImageUrl = kycDetails.getSelfie_img();
            signatureImageUrl = kycDetails.getSignature_img();
        }
        checkAllValid();
        //ID
        if (idImageUrl != null && !idImageUrl.equals("") && !idImageUrl.equals("0")) {
            validIdImage = true;
            rlIdUpload.setVisibility(View.GONE);
            llIdImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(SthiramValues.COMMON_IMAGE_BASE_URL + idImageUrl)
                    .into(ivId);
        } else {
            validIdImage = false;
            rlIdUpload.setVisibility(View.VISIBLE);
            llIdImage.setVisibility(View.GONE);
        }
        //selfie
        if (selfieImageUrl != null && !selfieImageUrl.equals("") && !selfieImageUrl.equals("0")) {
            validSelfieImage = true;
            rlSelfieUplaod.setVisibility(View.GONE);
            llSelfieImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(SthiramValues.COMMON_IMAGE_BASE_URL + selfieImageUrl)
                    .into(ivSelfie);
        } else {
            validSelfieImage = false;
            rlSelfieUplaod.setVisibility(View.VISIBLE);
            llSelfieImage.setVisibility(View.GONE);
        }
        if (signatureImageUrl != null && !signatureImageUrl.equals("") && !signatureImageUrl.equals("0")) {
            validSignatureImage = true;
            rlSignatureUpload.setVisibility(View.GONE);
            llSignatureImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(SthiramValues.COMMON_IMAGE_BASE_URL + signatureImageUrl)
                    .into(ivSignature);
        } else {
            validSignatureImage = false;
            rlSignatureUpload.setVisibility(View.VISIBLE);
            llSignatureImage.setVisibility(View.GONE);
        }
    }

    @SuppressLint("InflateParams")
    private void createBottomSheet() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.gallery_option, null);
        ivBottomSheetClose = bottomSheetView.findViewById(R.id.iv_close);
        llGallery = bottomSheetView.findViewById(R.id.ll_gallery);
        llCamera = bottomSheetView.findViewById(R.id.ll_camera);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCurved);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
    }

    private void setItemListeners() {
        rlIdUpload.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
            selectedType = "id";
        });
        rlSelfieUplaod.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
            selectedType = "selfie";
        });
        rlSignatureUpload.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
            selectedType = "signature";
        });
        ivBottomSheetClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        llGallery.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkGalleryPermission(requestPermissionLauncherGallery)) {
                openGallery();
            }
        });
        llCamera.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkCameraPermission(requestPermissionLauncherCamera)) {
                dispatchTakePictureIntent();
            }
        });
        ivIdClose.setOnClickListener(view -> {
            idImageUrl = "";
            validIdImage = false;
            rlIdUpload.setVisibility(View.VISIBLE);
            llIdImage.setVisibility(View.GONE);
            checkAllValid();
        });
        ivSelfieClose.setOnClickListener(view -> {
            selfieImageUrl = "";
            validSelfieImage = false;
            rlSelfieUplaod.setVisibility(View.VISIBLE);
            llSelfieImage.setVisibility(View.GONE);
            checkAllValid();
        });
        ivSignatureClose.setOnClickListener(view -> {
            signatureImageUrl = "";
            validSignatureImage = false;
            rlSignatureUpload.setVisibility(View.VISIBLE);
            llSignatureImage.setVisibility(View.GONE);
            checkAllValid();
        });
        tvContinue.setOnClickListener(view -> {
            if (kycDetails == null) {
                kycDetails = new KYCDetailsData();
            }
            kycDetails.setId_image(idImageUrl);
            kycDetails.setSelfie_img(selfieImageUrl);
            kycDetails.setSignature_img(signatureImageUrl);
            Intent in = new Intent(context, ViewKYCDetailsActivity.class);
            in.putExtra("kycDetails", kycDetails);
            requestLauncherKYCUpdate.launch(in);
        });

    }

    private void checkAllValid() {
        if (validIdImage && validSelfieImage && validSignatureImage) {
            llBottom.setVisibility(View.VISIBLE);
        } else {
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
                        "com.yeel.southsudan.drc",
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
        bitmap = ExifUtils.rotateBitmap(imageUrl, bitmapSample);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.upload_your_documents);
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (isTaskRoot()) {
                commonFunctions.callHomeIntent();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }


    private void getBasicDetails() {
        apiCall.getKYCDetails(getKYCDetailsRetry, commonFunctions.getUserId(), false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    KYCDetailsResponse apiResponse = gson.fromJson(jsonString, KYCDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        kycDetails = apiResponse.getKycDetails();
                        progressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        setValues();
                    } else {
                        kycDetails = null;
                        setNoData();
                    }
                } catch (Exception e) {
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getKYCDetailsRetry = true;
                getBasicDetails();

            }
        });
    }

    private void setNoData() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        validIdImage = false;
        validSelfieImage = false;
        validSignatureImage = false;
        idImageUrl = "";
        selfieImageUrl = "";
        signatureImageUrl = "";
        rlIdUpload.setVisibility(View.VISIBLE);
        llIdImage.setVisibility(View.GONE);
        rlSelfieUplaod.setVisibility(View.VISIBLE);
        llSelfieImage.setVisibility(View.GONE);
        rlSignatureUpload.setVisibility(View.VISIBLE);
        llSignatureImage.setVisibility(View.GONE);
        checkAllValid();
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            commonFunctions.callHomeIntent();
        } else {
            finish();
        }
    }

    private void openGallery() {
        Intent in = new Intent(context, ImageBrowse.class);
        requestLauncherGallery.launch(in);
    }

    private void cropIntent(String selectedType) {
        Intent in;
        if (selectedType.equals("id")) {
            in = new Intent(context, RectangleCropActivity.class);
        } else if (selectedType.equals("selfie")) {
            in = new Intent(context, SqureCropActivity.class);
        } else {
            in = new Intent(context, RectangleCropActivity.class);
        }
        in.putExtra("from", "settings");
        in.putExtra("type", "G");
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

    ActivityResultLauncher<Intent> requestLauncherGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String imageUrl = result.getData().getStringExtra("imageURL");
            generateBitmapImageFromURL(imageUrl);
            cropIntent(selectedType);
        }
    });

    ActivityResultLauncher<Intent> requestLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            generateBitmapImageFromURL(firstImageUrl);
            cropIntent(selectedType);
        }
    });

    ActivityResultLauncher<Intent> requestLauncherImageCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String url = result.getData().getStringExtra("imageURL");
            String reTake = result.getData().getStringExtra("ReTake");
            if (reTake.equals("Yes")) {
                dispatchTakePictureIntent();
            } else {
                switch (selectedType) {
                    case "id":
                        idImageUrl = url;
                        validIdImage = true;
                        rlIdUpload.setVisibility(View.GONE);
                        llIdImage.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(url)
                                .into(ivId);
                        break;
                    case "selfie":
                        selfieImageUrl = url;
                        validSelfieImage = true;
                        rlSelfieUplaod.setVisibility(View.GONE);
                        llSelfieImage.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(url)
                                .into(ivSelfie);
                        break;
                    case "signature":
                        signatureImageUrl = url;
                        validSignatureImage = true;
                        rlSignatureUpload.setVisibility(View.GONE);
                        llSignatureImage.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(url)
                                .into(ivSignature);
                        break;
                }
                checkAllValid();
            }
        }
    });

    ActivityResultLauncher<Intent> requestLauncherKYCUpdate = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent in = getIntent();
            setResult(Activity.RESULT_OK, in);
            finish();
        }
    });


}