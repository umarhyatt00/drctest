package com.yeel.drc.dialogboxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yeel.drc.R;
import com.yeel.drc.activity.main.HomeActivity;

public class SomethingWentWrongDialog extends Dialog {
    Context context;
    String type;
    TextView tvTitle;
    TextView tvMessage;
    TextView btnOkay;
    public SomethingWentWrongDialog(@NonNull Context context,String type) {
        super(context);
        this.context=context;
        this.type=type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_something_went_wrong);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvMessage=findViewById(R.id.tv_message);
        tvTitle=findViewById(R.id.tv_title);
        if(type.equals("Dismiss")){
            btnOkay.setText(context.getString(R.string.ok));
            tvTitle.setText(context.getString(R.string.something_wrong_title));
            tvMessage.setText(context.getString(R.string.something_wrong));
        }else{
            btnOkay.setText(context.getString(R.string.ok_logout));
            tvTitle.setText(context.getString(R.string.something_wrong_title));
            tvMessage.setText(context.getString(R.string.something_wrong_logout));
        }
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(type.equals("Finish")){
                        dismiss();
                        ((Activity) context).finish();
                    }else if(type.equals("Dismiss")){
                        dismiss();
                    }else{
                        Intent intent = new Intent(context, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } catch (WindowManager.BadTokenException bte) {

                }

            }
        });
    }
}
