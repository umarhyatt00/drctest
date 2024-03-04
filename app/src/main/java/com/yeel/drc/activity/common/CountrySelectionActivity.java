package com.yeel.drc.activity.common;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.CountrySelectionListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.countylist.CountryListData;
import com.yeel.drc.api.countylist.CountryListResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.ArrayList;
import java.util.List;

public class CountrySelectionActivity extends BaseActivity {
    Context context;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    List<CountryListData> countryList;
    List<CountryListData> countryListFilter;
    EditText edtSearch;
    TextView tvNoItem;
    LinearLayout llSearch;

    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean countryListRetry =false;
    ProgressBar progressBar;

    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection);
        setToolBar();
        type=getIntent().getStringExtra("type");
        context= CountrySelectionActivity.this;
        initView();
        setOnClickListener();
        addTextListener();
    }


    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);

        rvList=findViewById(R.id.rv_list);
        linearLayoutManager=new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        edtSearch=findViewById(R.id.edt_search);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progress_bar);
        llSearch=findViewById(R.id.ll_search);
        llSearch.setVisibility(View.GONE);
        getList();
    }


    private void setOnClickListener() {
        edtSearch.setOnTouchListener((v, event) -> {
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
                    if(countryList.size()!=0){
                        if(!query.toString().toLowerCase().equals("")){
                            if (!SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
                                edtSearch.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_search_clear, 0);
                            }

                            query = query.toString().toLowerCase();
                            countryListFilter =null;
                            countryListFilter = new ArrayList<>();
                            for (int i = 0; i < countryList.size(); i++) {
                                String name = countryList.get(i).getCountryName().toLowerCase();
                                if (name.contains(query)) {
                                    countryListFilter.add(countryList.get(i));
                                }
                            }
                            if(countryListFilter !=null&& countryListFilter.size()!=0){
                                setList(countryListFilter);
                            }else{
                                setNoItem();
                            }
                        }else{
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            setList(countryList);
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





    private void getList() {
        if(!commonFunctions.getCountryLists().equals("")){
            String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getCountryLists());
            Gson gson = new Gson();
            CountryListResponse apiResponse=gson.fromJson(jsonString, CountryListResponse.class);
            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                countryList=apiResponse.getCountryList();
                setList(countryList);
            }
        }
        apiCall.getCountyList(countryListRetry,false,type, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    commonFunctions.setCountryLists(response.getKuttoosan());
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    CountryListResponse apiResponse=gson.fromJson(jsonString, CountryListResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){
                        countryList=apiResponse.getCountryList();
                        setList(countryList);
                    }else{
                        if(!errorDialog.isShowing()){
                            errorDialog.show(apiResponse.getMessage());
                        }
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                countryListRetry =true;
                getList();
            }
        });
    }

    private void setList(List<CountryListData> list) {
        progressBar.setVisibility(View.GONE);
        rvList.setVisibility(View.VISIBLE);
        tvNoItem.setVisibility(View.GONE);
        llSearch.setVisibility(View.VISIBLE);
        CountrySelectionListAdapter bankListAdapter = new CountrySelectionListAdapter(list,type, context,(v, position) ->  {
            Intent in = getIntent();
            in.putExtra("country_name", list.get(position).getCountryName());
            in.putExtra("country_code", list.get(position).getCountryMobileNumberCode());
            in.putExtra("mobile_number_length", list.get(position).getNo_of_digits());
            in.putExtra("flag", list.get(position).getFlag());
            in.putExtra("format", list.get(position).getFormat());
            setResult(Activity.RESULT_OK, in);
            finish();
        });
        rvList.setAdapter(bankListAdapter);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.country);
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