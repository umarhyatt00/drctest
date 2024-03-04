package com.yeel.drc.activity.main.agent.addfund;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yeel.drc.R;
import com.yeel.drc.adapter.BankSelectionListAdapter;
import com.yeel.drc.api.banklist.BankListData;
import com.yeel.drc.api.banklist.BankListResponse;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BankSelectionActivity extends BaseActivity {
    Context context;
    RecyclerView rvList;
    EditText edtSearch;
    TextView tvNoItem;
    LinearLayoutManager linearLayoutManager;
    List<BankListData> bankList;
    List<BankListData> bankListFilter;
    BankSelectionListAdapter bankListAdapter;
    BankListResponse bankListResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_selection);
        setToolBar();
        context= BankSelectionActivity.this;
        bankListResponse =(BankListResponse)getIntent().getSerializableExtra("data");
        bankList= bankListResponse.getProvinces();
        initView();
        setOnClickListener();
        addTextListener();
    }

    private void addTextListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try{
                    if(bankList.size()!=0){
                        if(!query.toString().toLowerCase().equals("")){
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_search_clear, 0);
                            query = query.toString().toLowerCase();
                            bankListFilter =null;
                            bankListFilter = new ArrayList<>();
                            for (int i = 0; i < bankList.size(); i++) {
                                String name = bankList.get(i).getBankName().toLowerCase();
                                if (name.contains(query)) {
                                    bankListFilter.add(bankList.get(i));
                                }
                            }
                            if(bankListFilter !=null&& bankListFilter.size()!=0){
                                setList(bankListFilter);
                            }else{
                                setNoItem();
                            }
                        }else{
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            setList(bankList);
                        }
                    }else{
                        setNoItem();
                    }
                }catch (Exception e){
                    setNoItem();
                }
            }
        });
    }

    private void setList(List<BankListData> list) {
        rvList.setVisibility(View.VISIBLE);
        tvNoItem.setVisibility(View.GONE);
        bankListAdapter = new BankSelectionListAdapter(list, context,(v, position) ->  {
            Intent in = getIntent();
            in.putExtra("bank_name", list.get(position).getBankName());
            in.putExtra("bank_code", list.get(position).getBankCode());
            setResult(Activity.RESULT_OK, in);
            finish();
        });
        rvList.setAdapter(bankListAdapter);
    }

    private void setNoItem() {
        rvList.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListener() {
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                try {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            edtSearch.setText("");
                            return true;
                        }
                    }
                }catch (Exception e){

                }

                return false;
            }
        });
    }

    private void initView() {
        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        edtSearch=findViewById(R.id.edt_search);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        setList(bankList);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.select_bank);
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