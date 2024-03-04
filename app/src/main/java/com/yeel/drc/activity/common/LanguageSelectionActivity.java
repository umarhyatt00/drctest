package com.yeel.drc.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.adapter.LanguageListAdapter;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.DialogProgress;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.LocaleUtils;
import com.yeel.drc.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LanguageSelectionActivity extends BaseActivity {
    Context context;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    List<String> languagesList;
    LanguageListAdapter languageListAdapter;
    SharedPrefUtil sharedPrefUtil;
    DialogProgress dialogProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        context = LanguageSelectionActivity.this;
        sharedPrefUtil = new SharedPrefUtil(context);
        setToolBar();
        initView();
    }

    private void initView() {
        dialogProgress=new DialogProgress(context);
        rvList = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        languagesList = new ArrayList<>();
        languagesList.add("English");
        languagesList.add("French");
        setList();
    }

    private void setList() {
        languageListAdapter = new LanguageListAdapter(languagesList, context, (v, position) -> {
            if (languagesList.get(position).equals("English")) {
                SthiramValues.SELECTED_LANGUAGE = "en";
                SthiramValues.SELECTED_LANGUAGE_ID = "1";
                SthiramValues.SELECTED_LANGUAGE_NAME = "English";
                sharedPrefUtil.setEncryptedStringPreference("LANGUAGE", "en");
                setLocale("en");
            } else {
                SthiramValues.SELECTED_LANGUAGE = "fr";
                SthiramValues.SELECTED_LANGUAGE_ID = "2";
                SthiramValues.SELECTED_LANGUAGE_NAME = "French";
                sharedPrefUtil.setEncryptedStringPreference("LANGUAGE", "fr");
                setLocale("fr");
            }
        });
        rvList.setAdapter(languageListAdapter);
    }

    private void setLocale(String lang) {
        LocaleUtils.initialize(context, lang);
        Intent in = getIntent();
        setResult(Activity.RESULT_OK, in);
        finish();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.language);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

}