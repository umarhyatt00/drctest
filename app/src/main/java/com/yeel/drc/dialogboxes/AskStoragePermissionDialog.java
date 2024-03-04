package com.yeel.drc.dialogboxes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yeel.drc.R;

public class AskStoragePermissionDialog extends Dialog {
    Context context;
    TextView btnOkay;
    TextView tvCancel;
    TextView tvPermissionNote;
    ImageView ivPermission;
    public AskStoragePermissionDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_style_ask_permission);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        btnOkay=findViewById(R.id.tv_ok);
        tvCancel=findViewById(R.id.tv_cancel);
        tvPermissionNote = findViewById(R.id.tv_message);
        ivPermission = findViewById(R.id.iv_permission);
        btnOkay.setOnClickListener(view -> {
            dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void show(String type) {
        show();
        if (type.equals("camera")) {
            tvPermissionNote.setText(context.getResources().getString(R.string.image_persmission_message));
            ivPermission.setImageResource(R.drawable.ic_storage_permission);
        } else if (type.equals("gallery")) {
            tvPermissionNote.setText(context.getResources().getString(R.string.image_persmission_message));
            ivPermission.setImageResource(R.drawable.ic_storage_permission);
        } else if (type.equals("contact")) {
            tvPermissionNote.setText(context.getResources().getString(R.string.yeel_needs_access_to_your_contacts_to_make_money_transafers_easy_enable_yeel_to_aceess_your_contacts_in_settings));
            ivPermission.setImageResource(R.drawable.ic_allow_permission);
        }else if (type.equals("scan")) {
            tvPermissionNote.setText(context.getResources().getString(R.string.scan_permission));
            ivPermission.setImageResource(R.drawable.ic_storage_permission);
        }else if(type.equals("share")){
            tvPermissionNote.setText(context.getResources().getString(R.string.to_share_permission_message));
            ivPermission.setImageResource(R.drawable.ic_storage_permission);
        }
    }
}
