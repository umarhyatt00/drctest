package com.yeel.drc.activity.main.cashout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import com.yeel.drc.activity.main.homepayandsend.ScanAndPayActivity;
import com.yeel.drc.dialogboxes.AskStoragePermissionDialog;

import java.io.File;
import java.io.FileInputStream;

public class ScanCaptureActivity extends CaptureActivity implements
        DecoratedBarcodeView.TorchListener {
    protected ImageView imageButton;
    ImageView flashLight;
    ImageView ivBrowse;
    protected DecoratedBarcodeView zxingBarcodeScanner;
    protected FrameLayout frame;
    EditText edtQrCode;
    boolean flash=false;
    static final int ID_GALLERY = 102;
    AskStoragePermissionDialog askStoragePermissionDialog;

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_scan_capture);
        initView();
        return findViewById(R.id.zxing_barcode_scanner);

    }

    private void initView() {
        findViewById(R.id.zxing_barcode_scanner).setVisibility(View.VISIBLE);
        imageButton = findViewById(R.id.imageButton);
        flashLight = findViewById(R.id.flash_light);
        ivBrowse = findViewById(R.id.iv_browse);
        flash = false;
        askStoragePermissionDialog=new AskStoragePermissionDialog(ScanCaptureActivity.this);
        zxingBarcodeScanner = findViewById(R.id.zxing_barcode_scanner);
        frame =  findViewById(R.id.frame);


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
            Intent in = new Intent(ScanCaptureActivity.this, ImageBrowse.class);
            startActivityForResult(in, ID_GALLERY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ID_GALLERY) {
                String imageURL=data.getStringExtra("imageURL");
                Bitmap bitmap=getBitmap(imageURL);
                if(bitmap!=null){
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
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (ChecksumException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    }

                    try {
                        String text = result.getText();
                        ScanAndPayActivity.QRCodeNumber = text;
                        finish();
                    }catch (Exception e){
                        Toast.makeText(ScanCaptureActivity.this,"Failed to fetch data",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ScanCaptureActivity.this,"Failed to fetch data",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap=null;
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return  bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        ScanAndPayActivity.QRCodeNumber="";
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