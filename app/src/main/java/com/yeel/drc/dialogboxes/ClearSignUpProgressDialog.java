package com.yeel.drc.dialogboxes;
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
import com.yeel.drc.activity.WelcomeActivity;
import com.yeel.drc.utils.CommonFunctions;

public class ClearSignUpProgressDialog extends Dialog {
    Context context;
    TextView btnOkay;
    TextView tvMessage;
    TextView tvCancel;
    CommonFunctions commonFunctions;
    public ClearSignUpProgressDialog(@NonNull Context context, CommonFunctions commonFunctions) {
        super(context);
        this.context=context;
        this.commonFunctions=commonFunctions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_clear_sign_up_progress);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvCancel=findViewById(R.id.tv_cancel);
        tvMessage=findViewById(R.id.tv_title);
        btnOkay.setOnClickListener(view -> {
            try {
                dismiss();
                Intent intent = new Intent(context, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (WindowManager.BadTokenException bte) {

            }

        });
        tvCancel.setOnClickListener(view -> dismiss());
    }
}
