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

public class ForceUpdateNotMandatoryDialog extends Dialog {
    Context context;
    TextView btnOkay;
    TextView tvCancel;
    public ForceUpdateNotMandatoryDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.force_update_not_mantdatory);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvCancel=findViewById(R.id.tv_cancel);
        btnOkay.setOnClickListener(view -> {
            try {
                final String appPackageName = "com.yeel.drc";
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                dismiss();
                ((Activity) context).finish();
            } catch (WindowManager.BadTokenException bte) {

            }

        });
        tvCancel.setOnClickListener(view -> dismiss());
    }
}
