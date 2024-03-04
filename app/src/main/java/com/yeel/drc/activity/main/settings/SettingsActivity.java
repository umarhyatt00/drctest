package com.yeel.drc.activity.main.settings;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.activity.common.LanguageSelectionActivity;
import com.yeel.drc.activity.common.ProfileImageCropActivity;
import com.yeel.drc.activity.main.agent.addfund.AgentLocatorActivity;
import com.yeel.drc.activity.main.settings.businessaccount.ViewBusinessDetailsActivity;
import com.yeel.drc.activity.main.settings.individual.ViewIndividualDetails;
import com.yeel.drc.activity.main.settings.individualagentaccount.ViewIndividualAgentDetailsActivity;
import com.yeel.drc.activity.main.settings.kyc.ViewKYCImagesActivity;
import com.yeel.drc.activity.main.settings.sequrity.SecuritySettingsActivity;
import com.yeel.drc.adapter.SettingsMenuListAdapter;
import com.yeel.drc.adapter.UserCurrencyListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.changedefaultcurrency.ChangeDefaultCurrencyResponse;
import com.yeel.drc.api.getmycurrencylist.MyCurrencyResponse;
import com.yeel.drc.api.saveprofileimage.SaveProfileImageResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.ExifUtils;
import com.yeel.drc.utils.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SettingsActivity extends BaseActivity {
    Context context;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    List<String> menuList;
    SettingsMenuListAdapter monthListAdapter;
    TextView tvEdit;
    LinearLayout llSecurity;
    RelativeLayout rlLanguage;
    RelativeLayout rlBrowse;
    TextView tvLan;
    LinearLayout llCurrency;
    TextView tvDefaultMessage;
    RelativeLayout rlPreApproved;
    ShimmerFrameLayout shimmerFrameLayoutCurrency;

    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetBehavior bottomSheetBehavior;
    ImageView ivBottomSheetClose;
    LinearLayout llGallery, llCamera;

    static final int CAMERA_REQUEST_CODE = 103;
    static final int GALLERY_REQUEST_CODE = 102;
    static final int CROP_REQUEST_CODE = 104;
    static final int ID_ADD_CURRENCY = 105;
    final static int UPDATE_KYC = 106;


    String firstImageUrl;
    public static Bitmap bitmap;
    ImageView ivProfile;
    TextView tvFirstLetter, tvAddNew;
    TextView tvUserName, tvEmail, tvKYCVerificationStatus;
    private RelativeLayout mRlFaceBook, mRlTwitter, mRlInsta;

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    DialogProgress dialogProgress;
    String KYCStatus = "";
    private List<CurrencyListData> currencyList;
    private UserCurrencyListAdapter userCurrencyListAdapter;
    private GridLayoutManager layoutManager;
    private RecyclerView rvCurrencyList;
    boolean getMyCurrencyListRetry = false;
    boolean changeDefaultCurrencyRetry = false;

    boolean switchedToDefaultCurrency = false;
    PermissionUtils permissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolBar();
        context = SettingsActivity.this;
        initView();
        setItemListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setValues() {
        if (SthiramValues.SELECTED_LANGUAGE_NAME.equals("Arabic")) {
            tvLan.setText(R.string.arabic);
        } else {
            tvLan.setText(SthiramValues.SELECTED_LANGUAGE_NAME);
        }

        KYCStatus = commonFunctions.getKYCStatus();
        commonFunctions.logAValue("KYC STATUS", KYCStatus);
        commonFunctions.logAValue("PRE APPROVED STATUS", commonFunctions.getPreApproved());

        String userType = "";
        if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
            userType = commonFunctions.getAgentType();
        } else {
            userType = commonFunctions.getUserType();
        }

        //tvUserName.setText(commonFunctions.getFullName());
        tvUserName.setText(commonFunctions.getFullName() + "-" + userType);
        String firstLetter = String.valueOf(commonFunctions.getFullName().charAt(0));
        tvEmail.setText(commonFunctions.formatAMobileNumber(commonFunctions.getMobile(), commonFunctions.getCountryCode(), commonFunctions.getMobileNumberFormat()));
        if (!commonFunctions.getProfileImage().equals("") && commonFunctions.getProfileImage() != null) {
            tvFirstLetter.setVisibility(View.GONE);
            Glide.with(this)
                    .load(commonFunctions.getProfileImage())
                    .apply(new RequestOptions().circleCrop())
                    .into(ivProfile);
        } else {
            tvFirstLetter.setVisibility(View.VISIBLE);
            tvFirstLetter.setText(firstLetter);
        }

        if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL) && commonFunctions.getPreApproved().equals(SthiramValues.PRE_APPROVED_STATUS_YES)) {
            rlPreApproved.setVisibility(View.VISIBLE);
            tvKYCVerificationStatus.setVisibility(View.GONE);
        } else {
            rlPreApproved.setVisibility(View.GONE);
            tvKYCVerificationStatus.setVisibility(View.VISIBLE);
            if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
                switch (KYCStatus) {
                    case "Not yet submitted":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kyc_verification_not_done, 0, 0, 0);
                        break;
                    case "Approved":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kyc_success, 0, 0, 0);
                        break;
                    case "Rejected":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kyc_verification_not_done, 0, 0, 0);
                        break;
                    case "Submitted":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kyc_pending, 0, 0, 0);
                        break;
                    case "Deactivated":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_deactivated, 0, 0, 0);
                        break;
                    case "Expired":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_expired, 0, 0, 0);
                        break;
                }
            } else {
                switch (KYCStatus) {
                    case "Not yet submitted":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_kyc_verification_not_done, 0);
                        break;
                    case "Approved":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_kyc_success, 0);
                        break;
                    case "Rejected":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_kyc_verification_not_done, 0);
                        break;
                    case "Submitted":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_kyc_pending, 0);
                        break;
                    case "Deactivated":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_deactivated, 0);
                        break;
                    case "Expired":
                        tvKYCVerificationStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expired, 0);
                        break;
                }
            }
        }


    }

    private void initView() {
        permissionUtils = new PermissionUtils(context);
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        createBottomSheet();
        rvList = findViewById(R.id.rv_list);
        tvAddNew = findViewById(R.id.tv_add_new_currency);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        rvCurrencyList = findViewById(R.id.rv_currency_list);
        layoutManager = new GridLayoutManager(SettingsActivity.this, 2);
        rvCurrencyList.setLayoutManager(layoutManager);
        createMenuList();
        tvEdit = findViewById(R.id.tv_edit);
        llSecurity = findViewById(R.id.ll_security);
        rlLanguage = findViewById(R.id.rl_language);
        rlBrowse = findViewById(R.id.rl_browse);
        ivProfile = findViewById(R.id.iv_profile);
        tvFirstLetter = findViewById(R.id.tv_name_first_letter);
        tvUserName = findViewById(R.id.settings_user_name);
        tvEmail = findViewById(R.id.settings_account_email);
        tvKYCVerificationStatus = findViewById(R.id.tvKYCVerificationStatus);
        mRlFaceBook = (RelativeLayout) findViewById(R.id.rl_fb);
        mRlTwitter = (RelativeLayout) findViewById(R.id.rl_twitter);
        mRlInsta = (RelativeLayout) findViewById(R.id.rl_insta);
        tvLan = findViewById(R.id.tv_lag);
        llCurrency = findViewById(R.id.ll_currency);
        tvDefaultMessage = findViewById(R.id.tvDefaultMessage);
        tvDefaultMessage.setVisibility(View.GONE);
        rlPreApproved = findViewById(R.id.rl_pre_approved);
        shimmerFrameLayoutCurrency = findViewById(R.id.shimmer_view_currency);
        getCurrencyList();
        setValues();
    }

    private void setItemListeners() {
        tvEdit.setOnClickListener(view -> {
            commonFunctions.logAValue("User Type", commonFunctions.getUserType());
            if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
                if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                    Intent in = new Intent(context, ViewIndividualAgentDetailsActivity.class);
                    startActivity(in);
                } else {
                    Intent in = new Intent(context, ViewBusinessDetailsActivity.class);
                    startActivity(in);
                }
            } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
                Intent in = new Intent(context, ViewIndividualDetails.class);
                startActivity(in);
            } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                Intent in = new Intent(context, ViewBusinessDetailsActivity.class);
                startActivity(in);
            }
        });
        llSecurity.setOnClickListener(view -> {
            Intent in = new Intent(context, SecuritySettingsActivity.class);
            startActivity(in);
        });
        rlLanguage.setOnClickListener(view -> {
            Intent in = new Intent(context, LanguageSelectionActivity.class);
            languageChangeListener.launch(in);
        });

        tvAddNew.setOnClickListener(view -> {
            Intent in = new Intent(context, AddNewCurrencyActivity.class);
            startActivityForResult(in, ID_ADD_CURRENCY);
        });
        rlBrowse.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.show();
        });
        llGallery.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkGalleryPermission(requestPermissionLauncherGallery)) {
                Intent in = new Intent(context, ImageBrowse.class);
                startActivityForResult(in, GALLERY_REQUEST_CODE);
            }
        });
        llCamera.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            if (permissionUtils.checkCameraPermission(requestPermissionLauncherCamera)) {
                dispatchTakePictureIntent();
            }
        });

        mRlFaceBook.setOnClickListener(view -> {

            String facebookUrl = "https://www.facebook.com/yeelsouthsudan";
            try {
                int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    Uri uri = Uri.parse("fb://page/106283064056271");
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            } catch (PackageManager.NameNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }

        });
        mRlTwitter.setOnClickListener(view -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/YeelPay"));
            startActivity(browserIntent);
        });
        mRlInsta.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/yeelpay"));
            startActivity(browserIntent);
        });
        rlPreApproved.setOnClickListener(view -> kycVerificationDialog());
        tvKYCVerificationStatus.setOnClickListener(view -> {

            if (KYCStatus.equals(SthiramValues.KYC_STATUS_NOT_YET_SUBMITTED)) {
                Intent in = new Intent(context, ViewKYCImagesActivity.class);
                startActivityForResult(in, UPDATE_KYC);
            } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_APPROVED)) {

            } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_SUBMITTED)) {
                String validationMessage = context.getString(R.string.kyc_submitted_message);
                Toast.makeText(context, validationMessage, Toast.LENGTH_LONG).show();
            } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_REJECTED)) {
                String validationMessage = context.getString(R.string.kyc_rejected_message);
                kycErrorDialogue(validationMessage, context.getString(R.string.submit_again));
            } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)) {
                String validationMessage = context.getString(R.string.kyc_deactivated_message);
                Toast.makeText(context, validationMessage, Toast.LENGTH_LONG).show();
            } else if (KYCStatus.equals(SthiramValues.KYC_STATUS_EXPIRED)) {
                String validationMessage = context.getString(R.string.kyc_expired_message);
                kycErrorDialogue(validationMessage, context.getString(R.string.upload));
            }
        });
    }

    ActivityResultLauncher<Intent> languageChangeListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            commonFunctions.callHomeIntent();
            finish();
        }
    });

    private void kycErrorDialogue(String message, String buttonLabel) {
        final Dialog success = new Dialog(context);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_validation_design);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        String formattedAmount = commonFunctions.setAmount(commonFunctions.getPreApprovedLimit());
        textLimitAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount);
        TextView tvMessage = success.findViewById(R.id.tv_message);
        TextView tvCancel = success.findViewById(R.id.tv_cancel);
        TextView tvOk = success.findViewById(R.id.tv_ok);
        tvMessage.setText(message);
        tvCancel.setText(context.getString(R.string.skip));
        tvOk.setText(buttonLabel);
        tvCancel.setOnClickListener(v -> success.dismiss());
        tvOk.setOnClickListener(view -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            startActivityForResult(in, UPDATE_KYC);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }


    private void kycVerificationDialog() {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.alert_kyc_verification);
        TextView btnNow = success.findViewById(R.id.text_now);
        TextView textLimitAmount = success.findViewById(R.id.text_limit_amount);
        TextView btnLater = success.findViewById(R.id.text_later);
        String formattedAmount = commonFunctions.setAmount(commonFunctions.getPreApprovedLimit());
        textLimitAmount.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL + " " + formattedAmount);
        btnLater.setOnClickListener(v -> success.dismiss());
        btnNow.setOnClickListener(v -> {
            success.dismiss();
            Intent in = new Intent(context, ViewKYCImagesActivity.class);
            startActivityForResult(in, UPDATE_KYC);
        });
        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
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
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                generateBitmapImageFromURL(firstImageUrl);
                Intent in = new Intent(context, ProfileImageCropActivity.class);
                in.putExtra("type", "C");
                startActivityForResult(in, CROP_REQUEST_CODE);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                assert data != null;
                String imageUrl = data.getStringExtra("imageURL");
                generateBitmapImageFromURL(imageUrl);
                Intent in = new Intent(context, ProfileImageCropActivity.class);
                in.putExtra("type", "G");
                startActivityForResult(in, CROP_REQUEST_CODE);
            } else if (requestCode == CROP_REQUEST_CODE) {
                String url = data.getStringExtra("imageURL");
                String reTake = data.getStringExtra("ReTake");
                if (reTake.equals("Yes")) {
                    dispatchTakePictureIntent();
                } else {
                    tvFirstLetter.setVisibility(View.GONE);
                    Glide.with(this)
                            .load(url)
                            .apply(new RequestOptions().circleCrop())
                            .into(ivProfile);
                    saveProfileImage(url);

                }

            } else if (requestCode == ID_ADD_CURRENCY) {
                getCurrencyListTwo();
            } else if (requestCode == UPDATE_KYC) {
                setValues();
            }
        }
    }

    private void saveProfileImage(String url) {
        dialogProgress.show();
        Retrofit retrofit = apiCall.createRetrofitObjectForUploadProfileImage(commonFunctions.getUserId(), SthiramValues.USER_URL);
        MultipartBody.Part profileImage = null;
        if (!url.equals("") && url != null) {
            File file = new File(url);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            profileImage = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);
        }
        retrofit.create(APIInterface.class).saveProfileImage(profileImage).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                if (response.isSuccessful()) {
                    dialogProgress.dismiss();
                    String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                    Gson gson = new Gson();
                    SaveProfileImageResponse apiResponse = gson.fromJson(jsonString, SaveProfileImageResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (apiResponse.getSaved_profile_image().contains("https")) {
                            commonFunctions.setProfileImage(apiResponse.getSaved_profile_image());
                        } else {
                            commonFunctions.setProfileImage(SthiramValues.COMMON_IMAGE_BASE_URL + apiResponse.getSaved_profile_image());
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
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                dialogProgress.dismiss();
                if (!somethingWentWrongDialog.isShowing()) {
                    somethingWentWrongDialog.show();
                }
            }
        });
    }


    private void generateBitmapImageFromURL(String imageUrl) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmapSample = BitmapFactory.decodeFile(imageUrl, options);
        bitmap = ExifUtils.rotateBitmap(imageUrl, bitmapSample);
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


    private void getCurrencyList() {
        shimmerFrameLayoutCurrency.setVisibility(View.VISIBLE);
        shimmerFrameLayoutCurrency.startShimmer();
        if (!commonFunctions.getMyCurrencyList().equals("")) {
            String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getMyCurrencyList());
            Gson gson = new Gson();
            MyCurrencyResponse apiResponse = gson.fromJson(jsonString, MyCurrencyResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                int totalNumberOfCurrency = Integer.parseInt(apiResponse.getTotal_currencies());
                currencyList = apiResponse.getCurrencyList();
                setCurrencyList(totalNumberOfCurrency);
            }
        }
        apiCall.getMyCurrencyList(
                getMyCurrencyListRetry,
                false,
                commonFunctions.getUserId(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            commonFunctions.setMyCurrencyList(commonResponse.getKuttoosan());
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MyCurrencyResponse apiResponse = gson.fromJson(jsonString, MyCurrencyResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                int totalNumberOfCurrency = Integer.parseInt(apiResponse.getTotal_currencies());
                                currencyList = apiResponse.getCurrencyList();
                                setCurrencyList(totalNumberOfCurrency);
                            } else {
                                llCurrency.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getMyCurrencyListRetry = true;
                        getCurrencyList();
                    }
                }

        );
    }

    private void getCurrencyListTwo() {
        apiCall.getMyCurrencyList(
                getMyCurrencyListRetry,
                true,
                commonFunctions.getUserId(),
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            commonFunctions.setMyCurrencyList(commonResponse.getKuttoosan());
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            MyCurrencyResponse apiResponse = gson.fromJson(jsonString, MyCurrencyResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                int totalNumberOfCurrency = Integer.parseInt(apiResponse.getTotal_currencies());
                                currencyList = apiResponse.getCurrencyList();
                                setCurrencyList(totalNumberOfCurrency);
                            } else {
                                llCurrency.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        getMyCurrencyListRetry = true;
                        getCurrencyList();
                    }
                }

        );

    }

    private void setCurrencyList(int totalNumberOfCurrency) {
        shimmerFrameLayoutCurrency.stopShimmer();
        shimmerFrameLayoutCurrency.setVisibility(View.GONE);
        llCurrency.setVisibility(View.VISIBLE);
        //hide add new button
        if (currencyList.size() == totalNumberOfCurrency) {
            tvAddNew.setVisibility(View.GONE);
        } else {
            tvAddNew.setVisibility(View.VISIBLE);
        }

        //set default currency
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getDefault_currency().equals("1")) {
                currencyList.get(i).setSelectedStatus(1);
            } else {
                currencyList.get(i).setSelectedStatus(0);
            }
        }

        //set currency list
        userCurrencyListAdapter = new UserCurrencyListAdapter(SettingsActivity.this, currencyList, new UserCurrencyListAdapter.ItemClickAdapterListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void itemClick(View v, int position) {
                //this action will work only for below conditions
                //1. List size should be grater than 1
                //2. If this item is already selected no need to do anything

                if (currencyList.size() > 1) {
                    if (currencyList.get(position).getSelectedStatus() == 0) {
                        changeDefaultCurrency(currencyList.get(position), position);
                    }
                }
            }
        });
        rvCurrencyList.setAdapter(userCurrencyListAdapter);

    }

    private void changeDefaultCurrency(CurrencyListData currencyListData, int position) {
        apiCall.changeDefaultCurrency(
                changeDefaultCurrencyRetry,
                true,
                commonFunctions.getUserId(),
                currencyListData.getCurrency_id() + "",
                new CustomCallback() {
                    @Override
                    public void success(CommonResponse commonResponse) {
                        try {
                            String jsonString = apiCall.getJsonFromEncryptedString(commonResponse.getKuttoosan());
                            Gson gson = new Gson();
                            ChangeDefaultCurrencyResponse apiResponse = gson.fromJson(jsonString, ChangeDefaultCurrencyResponse.class);
                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                commonFunctions.setCurrencyID(currencyListData.getCurrency_id());
                                if (currencyListData.getCurrency_id().equals("1")) {
                                    //SSP
                                    commonFunctions.setPreApprovedLimit(commonFunctions.getPreApprovedLimitSSP());
                                } else {
                                    //USD
                                    commonFunctions.setPreApprovedLimit(commonFunctions.getPreApprovedLimitUSD());
                                }
                                commonFunctions.setAppCurrency();
                                for (int i = 0; i < currencyList.size(); i++) {
                                    currencyList.get(i).setSelectedStatus(0);
                                }
                                currencyList.get(position).setSelectedStatus(1);
                                userCurrencyListAdapter.notifyDataSetChanged();
                                //show success message
                                tvDefaultMessage.setVisibility(View.VISIBLE);
                                tvDefaultMessage.setText(getString(R.string.default_currency_message) + " " + currencyListData.getCurrency_name());
                                tvDefaultMessage.postDelayed(new Runnable() {
                                    public void run() {
                                        tvDefaultMessage.setVisibility(View.GONE);
                                    }
                                }, 3000);

                                commonFunctions.setUserAccountNumber(currencyListData.getAccount_number());

                                commonFunctions.clearLocalStorage();
                                switchedToDefaultCurrency = true;
                            } else {
                                errorDialog.show(apiResponse.getMessage());
                            }
                        } catch (Exception e) {
                            somethingWentWrongDialog.show();
                        }
                    }

                    @Override
                    public void retry() {
                        changeDefaultCurrencyRetry = true;
                        changeDefaultCurrency(currencyListData, position);
                    }
                }

        );
    }


    private void createMenuList() {
        menuList = new ArrayList<>();
        if (!commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
            menuList.add(getString(R.string.agent_locator));
        }
        menuList.add(getString(R.string.support));
        menuList.add(getString(R.string.about_yeel));
        menuList.add(getString(R.string.invite_friends));
        menuList.add(getString(R.string.legel));
        monthListAdapter = new SettingsMenuListAdapter(menuList, context, (v, position) -> {
            if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
                if (position == 0) {
                    Intent in = new Intent(context, GetHelpActivity.class);
                    startActivity(in);
                } else if (position == 1) {
                    Intent in = new Intent(context, AboutYeelActivity.class);
                    startActivity(in);
                } else if (position == 2) {
                    String shareBody = "Hey - Yeel South Sudan is a fast, simple, and secure digital banking platform that we use to pay and send money from anywhere, anytime. Use this link to install Yeel South Sudan:  https://play.google.com/store/apps/details?id=com.yeel.southsudan";
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Yeel App");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "share"));
                } else if (position == 3) {
                    Intent intent = new Intent(context, LegalDocumentPage.class);
                    startActivity(intent);
                }
            } else {
                if (position == 0) {
                    Intent in = new Intent(context, AgentLocatorActivity.class);
                    startActivity(in);
                } else if (position == 1) {
                    Intent in = new Intent(context, GetHelpActivity.class);
                    startActivity(in);
                } else if (position == 2) {
                    Intent in = new Intent(context, AboutYeelActivity.class);
                    startActivity(in);
                } else if (position == 3) {
                    String shareBody = "Hey - Yeel South Sudan is a fast, simple, and secure digital banking platform that we use to pay and send money from anywhere, anytime. Use this link to install Yeel South Sudan:  https://play.google.com/store/apps/details?id=com.yeel.southsudan";
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Yeel App");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "share"));
                } else if (position == 4) {
                    Intent intent = new Intent(context, LegalDocumentPage.class);
                    startActivity(intent);
                }
            }

        });
        rvList.setAdapter(monthListAdapter);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.account_settings);
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (switchedToDefaultCurrency) {
                commonFunctions.callHomeIntent();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (switchedToDefaultCurrency) {
            commonFunctions.callHomeIntent();
        } else {
            finish();
        }
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
                    startActivityForResult(in, GALLERY_REQUEST_CODE);
                }
            });
}