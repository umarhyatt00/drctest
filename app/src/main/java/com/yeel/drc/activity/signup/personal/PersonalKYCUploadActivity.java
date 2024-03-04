package com.yeel.drc.activity.signup.personal;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.RectangleCropActivity;
import com.yeel.drc.activity.common.SqureCropActivity;
import com.yeel.drc.activity.common.WebViewActivity;
import com.yeel.drc.activity.signup.KYCDetailsActivity;
import com.yeel.drc.dialogboxes.ClearSignUpProgressDialog;
import com.yeel.drc.model.SignUpData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.ExifUtils;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.RegisterFunctions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalKYCUploadActivity extends BaseActivity {
    Context context;
    SignUpData signUpData;
    RegisterFunctions registerFunctions;
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
    ClearSignUpProgressDialog clearSignUpProgressDialog;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_k_y_c_upload);
        setToolBar();
        context = PersonalKYCUploadActivity.this;
        signUpData = (SignUpData) getIntent().getSerializableExtra("signUpData");
        registerFunctions = new RegisterFunctions(context);
        clearSignUpProgressDialog = new ClearSignUpProgressDialog(context, commonFunctions);
        permissionUtils = new PermissionUtils(context);
        if (signUpData == null) {
            signUpData = new SignUpData();
            signUpData.setUserId(registerFunctions.getUserId());
            signUpData.setAccountType(registerFunctions.getAccountType());
            signUpData.setPin(registerFunctions.getPIN());
            signUpData.setCountryCode(registerFunctions.getCountryCode());
            signUpData.setMobileNumber(registerFunctions.getMobileNumber());
            signUpData.setCurrencyId(registerFunctions.getCurrencyID());
            signUpData.setKycId(registerFunctions.getKycID());
        }
        initView();
        setItemListeners();
    }

    private void initView() {
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
        String tc = getString(R.string.tc_one) + " <font size='5' color='#5463E8'>" + getString(R.string.tc_two) + "</font>";
        tvTc.setText(Html.fromHtml(tc));
        tvContinue = findViewById(R.id.tv_continue);
        createBottomSheet();
        setIdImage();
    }

    private void setIdImage() {
        if (signUpData.getIdUrl() != null && !signUpData.getIdUrl().equals("")) {
            idImageUrl = signUpData.getIdUrl();
            validIdImage = true;
            rlIdUpload.setVisibility(View.GONE);
            llIdImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(idImageUrl)
                    .into(ivId);
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
                galleryIntent();
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
            commonFunctions.setPage("kyc_doc");
            signUpData.setIdUrl(idImageUrl);
            signUpData.setSelfieUrl(selfieImageUrl);
            signUpData.setSignatureUrl(signatureImageUrl);
            registerFunctions.saveRegisterDetails(signUpData);
            Intent in = new Intent(context, KYCDetailsActivity.class);
            in.putExtra("signUpData", signUpData);
            startActivity(in);
        });
        tvTc.setOnClickListener(view -> {
            Intent in = new Intent(context, WebViewActivity.class);
            in.putExtra("type", "t_c");
            startActivity(in);
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
        bitmap = ExifUtils.rotateBitmap(imageUrl, bitmapSample);
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
        if (isTaskRoot()) {
            clearSignUpProgressDialog.show();
        } else {
            finish();
        }
    }

    private void galleryIntent() {
        Intent in = new Intent(context, ImageBrowse.class);
        requestLauncherGallery.launch(in);
    }

    private void cropImageIntent(String selectedType) {
        Intent in;
        if (selectedType.equals("id")) {
            in = new Intent(context, RectangleCropActivity.class);
        } else if (selectedType.equals("selfie")) {
            in = new Intent(context, SqureCropActivity.class);
        } else {
            in = new Intent(context, RectangleCropActivity.class);
        }
        in.putExtra("from", "personal");
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
                    galleryIntent();
                }
            });

    ActivityResultLauncher<Intent> requestLauncherGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            String imageUrl = result.getData().getStringExtra("imageURL");
            generateBitmapImageFromURL(imageUrl);
            cropImageIntent(selectedType);
        }
    });

    ActivityResultLauncher<Intent> requestLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            generateBitmapImageFromURL(firstImageUrl);
            cropImageIntent(selectedType);
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


}