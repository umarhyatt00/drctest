package com.yeel.drc.activity.common;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.LocaleUtils;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    String type;
    String URL="";
    Boolean LanguageChanged=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageChanged=false;
        if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
            setLocale("en","start");
            LanguageChanged=true;
        }else{
            setContentView(R.layout.activity_web_view_new);
            type=getIntent().getStringExtra("type");
            setToolBar();
            if(type.equals("t_c")){
                URL= SthiramValues.YEEL_AGREEMENT_URL;
            }else if(type.equals("privacy_policy")){
                URL= SthiramValues.PRIVACY_POLICY_URL;
            }else{
                URL= SthiramValues.YEEL_AGREEMENT_URL;
            }
            webView=findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(URL);
            webView.setHorizontalScrollBarEnabled(false);
        }

    }

    private void setLocale(String lang,String from) {
        Log.e("Set to",lang);
        LocaleUtils.initialize(WebViewActivity.this, lang);
        if(from.equals("start")){
            setContentView(R.layout.activity_web_view_new);
            type=getIntent().getStringExtra("type");
            setToolBar();
            if(type.equals("t_c")){
                URL= SthiramValues.YEEL_AGREEMENT_URL;
            }else if(type.equals("privacy_policy")){
                URL= SthiramValues.PRIVACY_POLICY_URL;
            }else{
                URL= SthiramValues.YEEL_AGREEMENT_URL;
            }
            webView=findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(URL);
            webView.setHorizontalScrollBarEnabled(false);
        }
    }
    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        if(type.equals("t_c")){
            tvTitle.setText(R.string.terms_conditions);
        }else{
            tvTitle.setText(R.string.consent_agreement);
        }
    }


    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(LanguageChanged){
            setLocale("ar","end");
        }
    }
}