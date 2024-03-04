package com.yeel.drc;

import android.content.Context;
import android.os.Bundle;
import com.yeel.drc.dialogboxes.CheckInternetConnectionDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.dialogboxes.MultipleLoginDialog;
import com.yeel.drc.dialogboxes.ServerMaintenanceDialog;
import com.yeel.drc.dialogboxes.SessionExpiredDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;

public class MainActivity extends BaseActivity {
    DialogProgress dialogProgress;
    SomethingWentWrongDialog somethingWentWrongDialog;
    CheckInternetConnectionDialog checkInternetConnectionDialog;
    ServerMaintenanceDialog serverMaintenanceDialog;
    SessionExpiredDialog sessionExpiredDialog;
    MultipleLoginDialog multipleLoginDialog;
    ErrorDialog errorDialog;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        dialogProgress=new DialogProgress(context);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        checkInternetConnectionDialog=new CheckInternetConnectionDialog(context, SthiramValues.dismiss);
        serverMaintenanceDialog=new ServerMaintenanceDialog(context, SthiramValues.dismiss);
        sessionExpiredDialog=new SessionExpiredDialog(context, SthiramValues.dismiss);
        multipleLoginDialog=new MultipleLoginDialog(context, SthiramValues.dismiss);
        errorDialog=new ErrorDialog(context, SthiramValues.finish);
      //  errorDialog.show("Value");
        int a=12;
        int b=13;
        calculator(a,b);
    }

    private void calculator(int a, int b) {

    }


}