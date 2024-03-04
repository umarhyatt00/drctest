package com.yeel.drc.activity.main.homepayandsend;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.yeel.drc.R;
import com.yeel.drc.activity.common.ImageBrowse;
import com.yeel.drc.dialogboxes.AskStoragePermissionDialog;
import com.yeel.drc.utils.SthiramValues;
import java.io.File;
import java.io.FileInputStream;

public class SmallCaptureActivity extends CaptureActivity implements
        DecoratedBarcodeView.TorchListener {
    protected ImageView imageButton;
    ImageView flashLight;
    ImageView ivBrowse;
    protected DecoratedBarcodeView zxingBarcodeScanner;
    protected FrameLayout frame;
    boolean flash = false;
    static final int ID_GALLERY = 102;
    RelativeLayout rlBrowseContact;
    LinearLayout llMobile;
    String permissionContact = Manifest.permission.READ_CONTACTS;
    static final int CONTACT_REQUEST_CODE = 103;
    AskStoragePermissionDialog askStoragePermissionDialog;
    TextView tvMobile;
    TextView tvScanCopy;
    RelativeLayout rlHead;
    LinearLayout llBottom;

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_small_capture);
        getTextView();
        return  findViewById(R.id.zxing_barcode_scanner);
    }

    public void getTextView() {
        findViewById(R.id.zxing_barcode_scanner).setVisibility(View.VISIBLE);
        imageButton = findViewById(R.id.imageButton);
        flashLight = findViewById(R.id.flash_light);
        ivBrowse = findViewById(R.id.iv_browse);
        flash = false;
        askStoragePermissionDialog = new AskStoragePermissionDialog(SmallCaptureActivity.this);
        zxingBarcodeScanner =  findViewById(R.id.zxing_barcode_scanner);
        llBottom = findViewById(R.id.ll_bottom);
        if (ScanAndPayActivity.from.equals("scan")) {
            llBottom.setVisibility(View.GONE);
        } else {
            llBottom.setVisibility(View.VISIBLE);
        }
        tvMobile = findViewById(R.id.tv_mobile);
        tvScanCopy = findViewById(R.id.tv_scan_copy);
        rlHead = findViewById(R.id.rl_head);
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            if (ScanAndPayActivity.from.equals("cashOut")) {
                tvMobile.setHint("Enter agent’s mobile number");
            } else {
                tvMobile.setHint("Enter Mobile number");
            }
            tvScanCopy.setText("Scan QR Code to Pay");
        }else {
            if (ScanAndPayActivity.from.equals("cashOut")) {
                tvMobile.setHint("Enter agent’s mobile number");
            } else {
                tvMobile.setHint("Enter Mobile number");
            }
            tvScanCopy.setText("Scan QR Code to Pay");
        }
        frame = (FrameLayout) findViewById(R.id.frame);
        rlBrowseContact = findViewById(R.id.rl_browse_contact);
        llMobile = findViewById(R.id.ll_mobile);
        if (ScanAndPayActivity.from.equals("cashOut")) {
            rlBrowseContact.setVisibility(View.INVISIBLE);
        } else {
            rlBrowseContact.setVisibility(View.VISIBLE);
        }
        llMobile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MobileNumberEnteringActivity.class);
            intent.putExtra("from", ScanAndPayActivity.from);
            startActivity(intent);
        });
        rlBrowseContact.setOnClickListener(v -> {
            if (checkContactPermission()) {
                Intent intent = new Intent(getApplicationContext(), MyContactListActivity.class);
                intent.putExtra("from", ScanAndPayActivity.from);
                startActivity(intent);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SmallCaptureActivity.this, permissionContact)) {
                    Intent intent = new Intent(getApplicationContext(), MyContactListActivity.class);
                    intent.putExtra("from", ScanAndPayActivity.from);
                    startActivity(intent);
                } else {
                    askContactPermission();
                }
            }

        });

        imageButton.setOnClickListener(view -> {
            ScanAndPayActivity.QRCodeNumber = "";
            finish();
        });
        flashLight.setOnClickListener(v -> {
            if (flash) {
                flashLight.setImageResource(R.drawable.ic_flash_off);
                zxingBarcodeScanner.setTorchOff();
                flash = false;
            } else {
                flashLight.setImageResource(R.drawable.ic_flash_off);
                zxingBarcodeScanner.setTorchOn();
                flash = true;
            }

        });
        ivBrowse.setOnClickListener(v -> {
            Intent in = new Intent(SmallCaptureActivity.this, ImageBrowse.class);
            startActivityForResult(in, ID_GALLERY);
        });

    }

    private boolean checkContactPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), permissionContact) == PackageManager.PERMISSION_GRANTED;
    }

    private void askContactPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionContact) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(SmallCaptureActivity.this, permissionContact)) {
                ActivityCompat.requestPermissions(SmallCaptureActivity.this, new String[]{permissionContact}, CONTACT_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONTACT_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getApplicationContext(), MyContactListActivity.class);
                intent.putExtra("from", ScanAndPayActivity.from);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ID_GALLERY) {
                String imageURL = data.getStringExtra("imageURL");
                Bitmap bitmap = getBitmap(imageURL);
                if (bitmap != null) {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    Result result = null;
                    try {
                        result = reader.decode(binaryBitmap);
                    } catch (NotFoundException | ChecksumException | FormatException e) {
                        e.printStackTrace();
                    }

                    try {
                        String text = result.getText();
                        ScanAndPayActivity.QRCodeNumber = text;
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(SmallCaptureActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SmallCaptureActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        ScanAndPayActivity.QRCodeNumber = "";
        finish();
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}