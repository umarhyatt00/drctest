package com.yeel.drc.activity.common;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeel.drc.R;
import com.yeel.drc.adapter.NumberListAdapter;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

import java.util.ArrayList;
import java.util.List;

public class ConfirmPINActivity extends BaseActivity {
    ConfirmPINActivity context;
    RegisterFunctions registerFunctions;
    RecyclerView rvNumbers;
    LinearLayoutManager linearLayoutManager;
    ImageView ivKeyBordBack;
    TextView tvZero;
    NumberListAdapter numberListAdapter;
    List<String> numbersList;
    ImageView ivOne,ivTwo,ivThree,ivFour,ivFive,ivSix;
    TextView tvFailedMessage;
    TextView tvHeading;
    TextView tvMessage;

    String pin="";
    String originalPin="";
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_p_i_n);
        context=ConfirmPINActivity.this;
        from=getIntent().getStringExtra("from");
        setToolBar();
        originalPin=getIntent().getStringExtra("originalPin");
        registerFunctions=new RegisterFunctions(context);
        initView();
        setItemListeners();
    }

    private void initView() {
        tvHeading=findViewById(R.id.tv_confirm_heading);
        tvMessage=findViewById(R.id.tv_message);
        if(from.equals("reset")){
            tvHeading.setText(R.string.confirm_pin_heading);
            tvMessage.setText(R.string.please_be_sure_to_remember_it);
        }else{
            tvHeading.setText(R.string.confirm_pin);
            tvMessage.setText(R.string.please_be_sure_to_remember_it);
        }
        rvNumbers=findViewById(R.id.rv_numbers);
        linearLayoutManager = new GridLayoutManager(context, 3);
        rvNumbers.setLayoutManager(linearLayoutManager);
        createList();
        ivKeyBordBack=findViewById(R.id.iv_key_bord_back);
        tvZero=findViewById(R.id.iv_zero);
        ivOne=findViewById(R.id.iv_one);
        ivTwo=findViewById(R.id.iv_two);
        ivThree=findViewById(R.id.iv_three);
        ivFour=findViewById(R.id.iv_four);
        ivFive=findViewById(R.id.iv_five);
        ivSix=findViewById(R.id.iv_six);
        tvFailedMessage=findViewById(R.id.tv_failed_message);
    }

    private void setItemListeners() {
        tvZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin=pin+"0";
                setImages(pin.length());
                if(pin.length()==6) {
                    if (originalPin.equals(pin)) {
                        Intent in = getIntent();
                        setResult(Activity.RESULT_OK, in);
                        finish();
                    } else {
                        setImages(0);
                        pin="";
                        tvFailedMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        ivKeyBordBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pin.length()==0){
                    setImages(0);
                }else{
                    pin = pin.substring(0, pin.length() - 1);
                    setImages(pin.length());
                }

            }
        });
    }

    private void createList() {
        numbersList=new ArrayList<>();
        numbersList.add("1");
        numbersList.add("2");
        numbersList.add("3");
        numbersList.add("4");
        numbersList.add("5");
        numbersList.add("6");
        numbersList.add("7");
        numbersList.add("8");
        numbersList.add("9");
        numberListAdapter=new NumberListAdapter(numbersList, context, new NumberListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                pin=pin+numbersList.get(position);
                setImages(pin.length());
                if(pin.length()==6) {
                    if (originalPin.equals(pin)) {
                        Intent in = getIntent();
                        setResult(Activity.RESULT_OK, in);
                        finish();
                    } else {
                        setImages(0);
                        pin="";
                        tvFailedMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        rvNumbers.setAdapter(numberListAdapter);
    }

    private void setImages(int length) {
        tvFailedMessage.setVisibility(View.INVISIBLE);
        if (length == 1) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_white_round_blue_border);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 2) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 3) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 4) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 5) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_blue_round);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        } else if (length == 6) {
            ivOne.setImageResource(R.drawable.ic_blue_round);
            ivTwo.setImageResource(R.drawable.ic_blue_round);
            ivThree.setImageResource(R.drawable.ic_blue_round);
            ivFour.setImageResource(R.drawable.ic_blue_round);
            ivFive.setImageResource(R.drawable.ic_blue_round);
            ivSix.setImageResource(R.drawable.ic_blue_round);
        }else{
            ivOne.setImageResource(R.drawable.ic_white_round_blue_border);
            ivTwo.setImageResource(R.drawable.ic_white_round_blue_border);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
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