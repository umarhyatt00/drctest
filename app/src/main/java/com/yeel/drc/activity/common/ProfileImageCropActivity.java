package com.yeel.drc.activity.common;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyft.android.scissors.CropView;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.settings.SettingsActivity;
import com.yeel.drc.timeout.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileImageCropActivity extends BaseActivity {
    CropView crop;
    TextView btn_cancel,btn_save;
    Bitmap croppedBitmap,finalBitmap;
    String final_img_path;
    ImageView ivClose;
    String type;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop_squre);
        type=getIntent().getStringExtra("type");
        crop=findViewById(R.id.crop);
        btn_cancel=findViewById(R.id.btn_cancel);
        btn_save=findViewById(R.id.btn_save);
        ivClose=findViewById(R.id.iv_close);
        try{
            crop.setImageBitmap(SettingsActivity.bitmap);
        }catch (Exception e){

        }
        if(type.equals("G")){
            btn_cancel.setText("Cancel");
        }else{
            btn_cancel.setText("Retake");
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("G")){
                    finish();
                }else{
                    Intent in = getIntent();
                    in.putExtra("imageURL", "");
                    in.putExtra("ReTake", "Yes");
                    setResult(Activity.RESULT_OK, in);
                    finish();
                }

            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                croppedBitmap = crop.crop();
                finalBitmap=getResizedBitmap(croppedBitmap,500,500);
                Calendar calender = Calendar.getInstance();
                Long lo = calender.getTimeInMillis();
                savePhoto(finalBitmap, lo + ".png");
            }
        });


    }
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    public void savePhoto(Bitmap imaginative, String filenameone) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,filenameone);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            imaginative.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final_img_path = directory.getAbsolutePath()+"/"+filenameone;
        Intent in = getIntent();
        in.putExtra("imageURL", final_img_path);
        in.putExtra("ReTake", "No");
        setResult(Activity.RESULT_OK, in);
        finish();
    }
}
