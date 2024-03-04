package com.yeel.drc.activity.main.agent.addfund;

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
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.RectangleCropActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.uploadbankrecipt.UploadBankReciept;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.ExifUtils;

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

public class UploadBankRecieptActivity extends BaseActivity {
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    RelativeLayout rlIdUpload;
    LinearLayout llIdImage;
    ImageView ivId;
    ImageView ivIdClose;
    TextView tvContinue;
    TextView tvSkip;
    LinearLayout llMain;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView ivBottomSheetClose;
    LinearLayout llGallery, llCamera;
    boolean validIdImage =false;
    private String firstImageUrl;
    public static Bitmap bitmap;
    String idImageUrl="";
    String requestId;
    DialogProgress dialogProgress;
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_bank_reciept);
        context=UploadBankRecieptActivity.this;
        requestId=getIntent().getStringExtra("requestId");
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        rlIdUpload.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
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
            idImageUrl="";
            validIdImage=false;
            rlIdUpload.setVisibility(View.VISIBLE);
            llIdImage.setVisibility(View.GONE);
            checkAllValid();
        });
        tvContinue.setOnClickListener(view -> uploadImage(idImageUrl));
        tvSkip.setOnClickListener(view -> {
            Intent in=new Intent(context,AddFundsSuccessActivity.class);
            startActivity(in);
        });
    }

    private void checkAllValid() {
        if(validIdImage){
            tvContinue.setEnabled(true);
            tvContinue.setBackgroundResource(R.drawable.bg_button_one);
        }else{
            tvContinue.setEnabled(false);
            tvContinue.setBackgroundResource(R.drawable.bg_button_three);
        }
    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        dialogProgress=new DialogProgress(context);
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        rlIdUpload=findViewById(R.id.rl_id_upload);
        llIdImage=findViewById(R.id.ll_id_image);
        ivId=findViewById(R.id.iv_id);
        ivIdClose=findViewById(R.id.iv_id_close);
        tvContinue=findViewById(R.id.tv_continue);
        tvContinue.setEnabled(true);
        tvSkip=findViewById(R.id.tv_skip_button);
        llMain=findViewById(R.id.ll_main);
        createBottomSheet();
        checkAllValid();
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
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.upload_bank_reciept);
    }

    @Override
    public void onBackPressed() {
        finish();
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

    private void uploadImage(String url) {
        dialogProgress.show();
        Retrofit retrofit = apiCall.createRetrofitObjectForUploadBankReceipt(commonFunctions.getUserId(),
                SthiramValues.APP_BASE_URL,
                requestId);
        MultipartBody.Part bankRecieptImage = null;
        if (!url.equals("")) {
            File file = new File(url);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            bankRecieptImage = MultipartBody.Part.createFormData("bank_receipt_image", file.getName(), requestFile);
        }
        retrofit.create(APIInterface.class).addbankReciept(bankRecieptImage).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse> call, @NonNull retrofit2.Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    dialogProgress.dismiss();
                    assert response.body() != null;
                    String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                    Gson gson = new Gson();
                    UploadBankReciept apiResponse = gson.fromJson(jsonString, UploadBankReciept.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        Intent in = new Intent(context, AddFundsSuccessActivity.class);
                        startActivity(in);
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                }else {
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

    private void openGallery() {
        Intent in = new Intent(context, ImageBrowse.class);
        requestLauncherGallery.launch(in);
    }

    private void cropImageIntent() {
        Intent in = new Intent(context, RectangleCropActivity.class);
        in.putExtra("from","bank_receipt");
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
            cropImageIntent();
        }
    });

    ActivityResultLauncher<Intent> requestLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            generateBitmapImageFromURL(firstImageUrl);
            cropImageIntent();
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
                validIdImage = true;
                rlIdUpload.setVisibility(View.GONE);
                llIdImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .into(ivId);
                checkAllValid();
            }
        }
    });






}