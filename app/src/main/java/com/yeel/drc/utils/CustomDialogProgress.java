package com.yeel.drc.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;

import com.yeel.drc.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class CustomDialogProgress extends Dialog {
    private String text;
    private GifImageView gifImageView;
    private Context context;

    public CustomDialogProgress(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CustomDialogProgress(@NonNull Context context, String text) {
        super(context, R.style.full_screen_dialog);
        this.text = text;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.prograss_dialog_custom);
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        gifImageView = findViewById(R.id.gifLoader);
        try {
            GifDrawable gifFromAssets = new GifDrawable(context.getAssets(), "yeel_loader.gif");
            gifImageView.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
         /*   LinearLayout linearLayout = findViewById(R.id.holder);
            linearLayout.removeAllViews();
            ProgressBar progressBar = new ProgressBar(context);
            linearLayout.addView(progressBar);
            e.printStackTrace();*/
        }

        setCancelable(false);
    }
}
