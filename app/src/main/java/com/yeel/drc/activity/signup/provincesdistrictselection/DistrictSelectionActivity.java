package com.yeel.drc.activity.signup.provincesdistrictselection;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.yeel.drc.adapter.DistrictListAdapter;
import com.yeel.drc.api.provinces.ProvincesData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class DistrictSelectionActivity extends BaseActivity {
    Context context;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    ProvincesData ProvincesData;
    DistrictListAdapter districtListAdapter;
    List<String> districtList;
    List<String> districtListFilter;
    EditText edtSearch;
    TextView tvNoItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_district_selection);
        setToolBar();
        context= DistrictSelectionActivity.this;
        ProvincesData=(ProvincesData)getIntent().getSerializableExtra("data");
        districtList=ProvincesData.getDistrict();
        initView();
        setOnClickListener();
        addTextListener();
    }

    private void initView() {
        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        edtSearch=findViewById(R.id.edt_search);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        setList(districtList);
    }


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

    private void addTextListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try{
                    if(districtList.size()!=0){
                        if(!query.toString().toLowerCase().equals("")){
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_search_clear, 0);
                            query = query.toString().toLowerCase();
                            districtListFilter=null;
                            districtListFilter = new ArrayList<>();
                            for (int i = 0; i < districtList.size(); i++) {
                                String name = districtList.get(i).toLowerCase();
                                if (name.contains(query)) {
                                    districtListFilter.add(districtList.get(i));
                                }
                            }
                            if(districtListFilter!=null&&districtListFilter.size()!=0){
                                setList(districtListFilter);
                            }else{
                                setNoItem();
                            }
                        }else{
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            setList(districtList);
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

    private void setNoItem() {
        rvList.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.VISIBLE);
    }

    private void setList( List<String> list) {
        rvList.setVisibility(View.VISIBLE);
        tvNoItem.setVisibility(View.GONE);
        districtListAdapter = new DistrictListAdapter(list, context,(v, position) ->  {
            Intent in = getIntent();
            in.putExtra("district", list.get(position));
            setResult(Activity.RESULT_OK, in);
            finish();
        });
        rvList.setAdapter(districtListAdapter);
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.state);
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