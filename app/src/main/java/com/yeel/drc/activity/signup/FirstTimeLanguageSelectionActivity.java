package com.yeel.drc.activity.signup;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.activity.WelcomeActivity;
import com.yeel.drc.adapter.FirstTimeLanguageSelectionAdapter;
import com.yeel.drc.model.LanguageSelectionModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.LocaleUtils;
import com.yeel.drc.utils.SharedPrefUtil;
import java.util.ArrayList;
import java.util.List;

public class FirstTimeLanguageSelectionActivity extends BaseActivity {
    Context context;
    SharedPrefUtil sharedPrefUtil;
    TextView btnContinue;
    RecyclerView rvLanguageList;
    List<LanguageSelectionModel> languagesList;
    FirstTimeLanguageSelectionAdapter languageSelectionAdapter;
    String selectedLanguageCode = "en";
    String selectedLanguageName = "English";
    String selectedLanguageId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_language_selection);
        context = FirstTimeLanguageSelectionActivity.this;
        initView();
        clickListeners();
    }
    private void setLanguageList() {
        languagesList.add(new LanguageSelectionModel("1", true, getString(R.string.english), getString(R.string.language_code_english), "en"));
        languagesList.add(new LanguageSelectionModel("2", false, getString(R.string.arabic), getString(R.string.language_code_arabice), "fr"));
        languageSelectionAdapter = new FirstTimeLanguageSelectionAdapter(languagesList, (v, position) -> {
            languagesList.forEach((language) -> {
                language.setSelected(false);
            });
            languagesList.get(position).setSelected(true);
            languageSelectionAdapter.notifyDataSetChanged();
            selectedLanguageCode = languagesList.get(position).getLanguageCode();
            selectedLanguageName = languagesList.get(position).getLanguageName();
            selectedLanguageId = languagesList.get(position).getLanguageID();
        });

        rvLanguageList.setAdapter(languageSelectionAdapter);
    }

    private void setLocale(String lang) {
        LocaleUtils.initialize(context, lang);
    }

    private void clickListeners() {
        btnContinue.setOnClickListener(v -> {
            commonFunctions.setShownLanguageSelection(true);
            SthiramValues.SELECTED_LANGUAGE = selectedLanguageCode;
            SthiramValues.SELECTED_LANGUAGE_ID = selectedLanguageId;
            SthiramValues.SELECTED_LANGUAGE_NAME = selectedLanguageName;
            sharedPrefUtil.setEncryptedStringPreference(
                    "LANGUAGE", selectedLanguageCode);
            setLocale(selectedLanguageCode);
            startActivity(new Intent(context, WelcomeActivity.class));
        });
    }

    private void initView() {
        sharedPrefUtil = new SharedPrefUtil(context);
        btnContinue = findViewById(R.id.btn_continue);
        rvLanguageList = findViewById(R.id.rv_language_list);
        languagesList = new ArrayList<>();

        setLanguageList();
    }




}