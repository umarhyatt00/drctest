package com.yeel.drc.activity.main.agent.common;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.RectangleCropActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.uploadbenificiaryid.UploadIdResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.ExifUtils;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;

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

public class UploadSenderDocumentsActivity extends BaseActivity {

    RelativeLayout rlUploadButton;
    LinearLayout llImage;
    ImageView ivClose;
    ImageView ivId;
    TextView tvContinue;
    boolean readyToUpload;
    Context context;
    String beneficiaryID;
    String beneficiaryIDImage;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView ivBottomSheetClose;
    LinearLayout llGallery, llCamera;
    PermissionUtils permissionUtils;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    String firstImageUrl;
    public static Bitmap bitmap;
    String from;
    boolean uploadRequired=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_sender_documents);
        context = UploadSenderDocumentsActivity.this;
        beneficiaryID = getIntent().getStringExtra("beneficiaryID");
        beneficiaryIDImage = getIntent().getStringExtra("beneficiaryIDImage");
        from=getIntent().getStringExtra("from");
        setToolBar();
        initView();
        setClicks();
    }

    private void setClicks() {
        rlUploadButton.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
        });
        ivBottomSheetClose.setOnClickListener(view -> bottomSheetDialog.dismiss());
        llGallery.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkGalleryPermission(requestPermissionLauncherGallery)) {
                Intent in = new Intent(context, ImageBrowse.class);
                imageBrowseActivityResultLauncher.launch(in);
            }
        });
        llCamera.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkCameraPermission(requestPermissionLauncherCamera)) {
                dispatchTakePictureIntent();
            }
        });
        ivClose.setOnClickListener(view -> {
            beneficiaryIDImage = "";
            readyToUpload = false;
            uploadRequired=true;
            setView();
        });

        tvContinue.setOnClickListener(view -> {
            if(uploadRequired){
                uploadDocs();
            }else{
                Intent in = getIntent();
                setResult(Activity.RESULT_OK, in);
                in.putExtra("beneficiaryID", beneficiaryID);
                in.putExtra("beneficiaryIDImage", beneficiaryIDImage);
                finish();
            }
        });

    }

    private void uploadDocs() {
        dialogProgress.show();
        Retrofit retrofit = apiCall.createRetrofitObjectForUploadBeneficiaryID(commonFunctions.getUserId(),beneficiaryID, SthiramValues.TRANSACTION_URL);
        MultipartBody.Part imageToUpload = null;
        if (beneficiaryIDImage!= null && !beneficiaryIDImage.equals("")) {
            File file = new File(beneficiaryIDImage);
            RequestBody requestFile = RequestBody.create(file,MediaType.parse("image/*"));
            imageToUpload = MultipartBody.Part.createFormData("id_image", file.getName(), requestFile);
        }
        retrofit.create(APIInterface.class).uploadBeneficiaryIDImage(imageToUpload).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull retrofit2.Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    dialogProgress.dismiss();
                    assert response.body() != null;
                    String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                    Gson gson = new Gson();
                    UploadIdResponse apiResponse = gson.fromJson(jsonString, UploadIdResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent in = getIntent();
                        setResult(Activity.RESULT_OK, in);
                        in.putExtra("beneficiaryID", beneficiaryID);
                        in.putExtra("beneficiaryIDImage", apiResponse.getId_image_name());
                        finish();
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
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        rlUploadButton = findViewById(R.id.rl_upload_button);
        llImage = findViewById(R.id.ll_image);
        ivClose = findViewById(R.id.iv_close);
        ivId = findViewById(R.id.iv_id);
        tvContinue = findViewById(R.id.tv_continue);
        if(from!=null&&from.equals("mobilePay")){
            readyToUpload=true;
            uploadRequired=false;
        }
        setView();
        createBottomSheet();
    }

    private void setView() {
        if (beneficiaryIDImage != null && !beneficiaryIDImage.equals("")) {
            rlUploadButton.setVisibility(View.GONE);
            llImage.setVisibility(View.VISIBLE);
            String fullURL=beneficiaryIDImage;
            if(!beneficiaryIDImage.contains("/")){
                fullURL=SthiramValues.COMMON_IMAGE_BASE_URL+beneficiaryIDImage;
            }
            Glide.with(this)
                    .load(fullURL)
                    .into(ivId);
        } else {
            rlUploadButton.setVisibility(View.VISIBLE);
            llImage.setVisibility(View.GONE);
        }
        setButtonStatus();
    }

    private void setButtonStatus() {
        if (readyToUpload) {
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
            tvContinue.setEnabled(true);
        } else {
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
            tvContinue.setEnabled(false);
        }
    }

    private void createBottomSheet() {
        bottomSheetView = getLayoutInflater().inflate(R.layout.gallery_option, null);
        ivBottomSheetClose = bottomSheetView.findViewById(R.id.iv_close);
        llGallery = bottomSheetView.findViewById(R.id.ll_gallery);
        llCamera = bottomSheetView.findViewById(R.id.ll_camera);
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogCurved);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
    }



    ActivityResultLauncher<Intent> rectangleCropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String url = data.getStringExtra("imageURL");
                        String reTake = data.getStringExtra("ReTake");
                        if (reTake.equals("Yes")) {
                            dispatchTakePictureIntent();
                        } else {
                            beneficiaryIDImage = url;
                            readyToUpload = true;
                            setView();
                        }
                    }
                }
            });
    ActivityResultLauncher<Intent> imageBrowseActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String imageUrl = data.getStringExtra("imageURL");
                        generateBitmapImageFromURL(imageUrl);
                        Intent in = new Intent(context, RectangleCropActivity.class);
                        in.putExtra("from", "upload_beneficiary_id");
                        in.putExtra("type", "G");
                        rectangleCropActivityResultLauncher.launch(in);
                    }
                }
            });

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        generateBitmapImageFromURL(firstImageUrl);
                        Intent in = new Intent(context, RectangleCropActivity.class);
                        in.putExtra("from", "upload_beneficiary_id");
                        in.putExtra("type", "C");
                        rectangleCropActivityResultLauncher.launch(in);
                    }
                }
            });



    private void generateBitmapImageFromURL(String imageUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmapSample = BitmapFactory.decodeFile(imageUrl, options);
        bitmap = ExifUtils.rotateBitmap(imageUrl, bitmapSample);
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
                    Intent in = new Intent(context, ImageBrowse.class);
                    imageBrowseActivityResultLauncher.launch(in);
                }
            });


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.yeel.drc.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraActivityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.sender_documents);
        RelativeLayout rlSkip = findViewById(R.id.rl_skip);
        rlSkip.setVisibility(View.GONE);
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