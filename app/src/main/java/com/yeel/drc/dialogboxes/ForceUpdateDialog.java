package com.yeel.drc.dialogboxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.yeel.drc.R;
import com.yeel.drc.utils.SthiramValues;

public class ForceUpdateDialog extends Dialog {
    Context context;
    String type;
    TextView btnOkay;
    TextView tvMessage;
    public ForceUpdateDialog(@NonNull Context context, String type) {
        super(context);
        this.context=context;
        this.type=type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_force_update);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvMessage=findViewById(R.id.tv_message);
        btnOkay.setOnClickListener(view -> {
            try {
                 if(type.equals(SthiramValues.finish)){
                     final String appPackageName = "com.yeel.drc";
                     try {
                         context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                     } catch (android.content.ActivityNotFoundException anfe) {
                         context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                     }

                     dismiss();
                     ((Activity) context).finish();

                 }
            } catch (WindowManager.BadTokenException bte) {

            }
        });
    }
    public void show(String s) {
        show();
        tvMessage.setText(s);
    }
}
