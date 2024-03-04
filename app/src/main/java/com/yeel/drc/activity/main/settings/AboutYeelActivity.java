package com.yeel.drc.activity.main.settings;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yeel.drc.R;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

public class AboutYeelActivity extends BaseActivity {
    Context context;
    String one = "";
    String two = "";
    String three = "";
    String four = "";
    

    TextView tvOne;
    TextView tvTwo;
    TextView tvThree;
    TextView tvFour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_yeel);
        setToolBar();
        context= AboutYeelActivity.this;
        initView();
        setItemListeners();
    }

    private void setItemListeners() {

    }

    private void initView() {
        String scanAndPay=getString(R.string.scan_and_pay);
        String scanAndPayMessage=getString(R.string.scan_and_pay_message);
        one="<font size='5' color='#4E576D'><b>"+scanAndPay+" </b></font>"+scanAndPayMessage;

        String send=getString(R.string.send);
        String sendMessage=getString(R.string.send_message);
        two="<font size='5' color='#4E576D'><b>"+send+": </b></font>"+sendMessage;


        String payMe=getString(R.string.payme);
        String payMeMessage=getString(R.string.payme_message);
        three="<font size='5' color='#4E576D'><b>"+payMe+": </b></font>"+payMeMessage;


        String cashOut=getString(R.string.cash_out);
        String cashOutMessage=getString(R.string.cash_out_message);
        four="<font size='5' color='#4E576D'><b>"+cashOut+": </b></font>"+cashOutMessage;


        tvOne=findViewById(R.id.tv_one);
        tvTwo=findViewById(R.id.tv_two);
        tvThree=findViewById(R.id.tv_three);
        tvFour=findViewById(R.id.tv_four);

        tvOne.setText(Html.fromHtml(one));
        tvTwo.setText(Html.fromHtml(two));
        tvThree.setText(Html.fromHtml(three));
        tvFour.setText(Html.fromHtml(four));
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.about_yeel);
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}