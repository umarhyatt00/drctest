package com.yeel.drc.activity.main.payme;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yeel.drc.R;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.PermissionUtils;

import java.util.Objects;

public class PayMeActivity extends BaseActivity {
    private TextView tvName;
    private TextView edtMaskMobile;
    private ImageView ivQrCode;
    PermissionUtils permissionUtils;

    private RelativeLayout rlFirstLetter;
    private TextView tvFirstLetter;

    private RelativeLayout rlProfileImage;
    private ImageView ivProfileImage;

    Context context;
    int deviceWidth;

    Bitmap myBitmapImage = null;
    LinearLayout llShare;
    DialogProgress dialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_me);
        setToolBar();
        context = PayMeActivity.this;
        dialogProgress = new DialogProgress(context);
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            deviceWidth = displayMetrics.widthPixels;
        } catch (Exception e) {

        }
        initView();
        setValues();
    }

    private void setValues() {
        String firstLetter = "";
        if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)) {
            if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                tvName.setText(commonFunctions.getFullName());
                firstLetter = String.valueOf(commonFunctions.getFullName().charAt(0));
            } else {
                tvName.setText(commonFunctions.getBusinessName());
                firstLetter = String.valueOf(commonFunctions.getBusinessName().charAt(0));
            }
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            tvName.setText(commonFunctions.getFullName());
            firstLetter = String.valueOf(commonFunctions.getFullName().charAt(0));
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            tvName.setText(commonFunctions.getBusinessName());
            firstLetter = String.valueOf(commonFunctions.getBusinessName().charAt(0));
        }
        if (commonFunctions.getProfileImage() != null && !commonFunctions.getProfileImage().equals("")) {
            rlProfileImage.setVisibility(View.VISIBLE);
            rlFirstLetter.setVisibility(View.GONE);
            Glide.with(this)
                    .load(commonFunctions.getProfileImage())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(ivProfileImage);
        } else {
            rlProfileImage.setVisibility(View.GONE);
            rlFirstLetter.setVisibility(View.VISIBLE);
            tvFirstLetter.setText(firstLetter);
        }
        String imageUrl = SthiramValues.QR_IMAGE_BASE_URL + commonFunctions.getQRImage();
        ivQrCode.getLayoutParams().height = (deviceWidth / 4) * 3;
        ivQrCode.getLayoutParams().width = (deviceWidth / 4) * 3;
        ivQrCode.requestLayout();
        Glide.with(context).load(imageUrl)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivQrCode);

        String mobileNumber=commonFunctions.formatAMobileNumber(commonFunctions.getMobile(),commonFunctions.getCountryCode(),commonFunctions.getMobileNumberFormat());

        Log.e("Mobile",mobileNumber);
        edtMaskMobile.setText(mobileNumber);
    }





    private void initView() {
        permissionUtils=new PermissionUtils(context);
        tvName = findViewById(R.id.tv_name);
        edtMaskMobile = findViewById(R.id.edt_mask_Mobile);
        ivQrCode = findViewById(R.id.iv_qr_code);
        rlFirstLetter = findViewById(R.id.rl_tv_image);
        tvFirstLetter = findViewById(R.id.tv_image);
        rlProfileImage = findViewById(R.id.rl_iv_image);
        ivProfileImage = findViewById(R.id.tv_logo);
        llShare = findViewById(R.id.ll_share);

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.payme);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_share_icon, menu);
        return true;
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_share:
                if(permissionUtils.checkGalleryPermissionShare(requestPermissionLauncherGallery)){
                    new bitmapAsyncNew().execute();
                }
                return true;
        }
        return false;
    }


    private ActivityResultLauncher<String> requestPermissionLauncherGallery =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });


    @SuppressLint("StaticFieldLeak")
    class bitmapAsyncNew extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress.show();
        }

        protected String doInBackground(String... urls) {
            try {
                myBitmapImage = Bitmap.createBitmap(llShare.getWidth(), llShare.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                llShare.draw(canvas);
                return "";
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            if (myBitmapImage != null) {
                commonFunctions.logAValue("Bitmap status", "bitmap  is present");
                shareImage();
            } else {
                commonFunctions.logAValue("Bitmap status", "bitmap is empty");
            }
            dialogProgress.dismiss();
        }
    }

    public void shareImage() {
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmapImage, "TRANSACTION" + System.currentTimeMillis(), null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra("subject", "");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "text message"));
    }
}