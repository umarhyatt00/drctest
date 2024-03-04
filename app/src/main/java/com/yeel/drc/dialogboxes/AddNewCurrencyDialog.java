package com.yeel.drc.dialogboxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yeel.drc.R;
import com.yeel.drc.activity.main.settings.SettingsActivity;
import com.yeel.drc.utils.CommonFunctions;

public class AddNewCurrencyDialog extends Dialog {
    Context context;
    TextView btnOkay;
    TextView tvMessage;
    TextView tvCancel;
    CommonFunctions commonFunctions;
    public AddNewCurrencyDialog(@NonNull Context context, CommonFunctions commonFunctions) {
        super(context);
        this.context=context;
        this.commonFunctions=commonFunctions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_add_new_curency);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvCancel=findViewById(R.id.tv_cancel);
        tvMessage=findViewById(R.id.tv_title);
        btnOkay.setOnClickListener(view -> {
            try {
                dismiss();
                ((Activity) context).finish();
                Intent in=new Intent(context, SettingsActivity.class);
                context.startActivity(in);
            } catch (WindowManager.BadTokenException bte) {

            }

        });
        tvCancel.setOnClickListener(view -> dismiss());


    }
    public void show(String s) {
        show();
        tvMessage.setText(s);
    }
}
