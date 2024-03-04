package com.yeel.drc.activity.main.homepayandsend;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class InviteUserActivity extends BaseActivity {
    Context context;
    String mobileNumber;
    String countyCode;
    TextView mTvMsg,mTvInvite;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);
        setToolBar();
        mobileNumber=getIntent().getStringExtra("mobile");
        countyCode=getIntent().getStringExtra("country_code");
        context= InviteUserActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        mTvInvite.setOnClickListener(view -> {
            String shareBody = "Hey, Yeel has made payments easy for me. You should try it too. It’s instant & 100% safe. You can send and receive money instantly. Try now. \n" +
                    "\n" +
                    "Click here & install Yeel - https://play.google.com/store/apps/details?id=com.yeel.digitalbank";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Yeel SouthSudan App");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "share"));
        });
    }

    private void initView() {
        APICall apiCall=new APICall(context, SthiramValues.dismiss);
        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),countyCode);
        String number=commonFunctions.formatAMobileNumber(mobileNumber,countyCode,mobileNumberFormat);


        mTvMsg = findViewById(R.id.tvMsg);
        mTvInvite = findViewById(R.id.tvInvite);
        String message="";
        if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
            mTvMsg.setVisibility(View.GONE);
            TextView tvThree=findViewById(R.id.tv_three);
            tvThree.setText(number+" ");
        }else if(SthiramValues.SELECTED_LANGUAGE_ID.equals("3")){
            mTvMsg.setVisibility(View.VISIBLE);
            message="Yeel'de kayıtlı değil, Yeel Türkiye Uygulamasına Davet Gönder";
            text= "<font size='5' color='#5463E8'><b>"+number+ "</b></font> "+message;
            mTvMsg.setText(Html.fromHtml(text));
        }else{
            mTvMsg.setVisibility(View.VISIBLE);
            message=getString(R.string.invite_message_one);
            text= "<font size='5' color='#5463E8'><b>"+number+ "</b></font> "+message;
            mTvMsg.setText(Html.fromHtml(text));
        }

    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
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
}