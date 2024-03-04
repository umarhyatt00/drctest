package com.yeel.drc.activity.main.settings;

import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yeel.drc.R;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class GetHelpActivity extends BaseActivity {
    private TextView tvChat;
    private TextView tvEmail;
    private TextView tvCallNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        setToolBar();
        initVies();
        setOnClickListener();
    }

    private void setOnClickListener() {
        tvChat.setOnClickListener(view -> {
            String smsNumber = "211923614400"; //without '+'
            try {
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber)+"@s.whatsapp.net");
                startActivity(sendIntent);
            } catch(Exception e) {
                Toast.makeText(GetHelpActivity.this, "You need to install Whatsapp first", Toast.LENGTH_SHORT).show();
            }
        });
        tvEmail.setOnClickListener(view -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","yeelsouthsudan@yeel.io", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });
        tvCallNow.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+211923614400"));
            startActivity(intent);
        });
    }

    private void initVies() {


        tvChat = findViewById(R.id.tvChat);
        tvEmail = findViewById(R.id.tvEmail);
        tvCallNow =  findViewById(R.id.tvCallNow);


    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.help_support);
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