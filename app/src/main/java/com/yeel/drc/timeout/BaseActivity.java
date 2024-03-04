package com.yeel.drc.timeout;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.yeel.drc.utils.CommonFunctions;

public abstract class BaseActivity extends AppCompatActivity implements LogoutListener {
    public CommonFunctions commonFunctions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApp) getApplication()).registerSessionListener(this);
        ((MyApp) getApplication()).startUserSession();
        commonFunctions=new CommonFunctions(getApplicationContext());

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((MyApp) getApplication()).onUserInteracted();
    }
    @Override
    public void onSessonLogout() {
       // Log.e("Time Out","yes");
        if(commonFunctions.getLogoutStatus()){
           // Log.e("APP Status","Do Nothing");
        }else{
           //Log.e("APP Status","Logout the app");
            commonFunctions.callReturningUserIntent("logout");
        }
    }


}