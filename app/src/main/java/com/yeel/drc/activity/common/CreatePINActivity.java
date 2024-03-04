package com.yeel.drc.activity.common;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yeel.drc.R;
import com.yeel.drc.adapter.NumberListAdapter;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.RegisterFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreatePINActivity extends BaseActivity {
    Context context;
    RegisterFunctions registerFunctions;
    RecyclerView rvNumbers;
    LinearLayoutManager linearLayoutManager;
    ImageView ivKeyBordBack;
    TextView tvZero;
    NumberListAdapter numberListAdapter;
    List<String> numbersList;
    ImageView ivOne, ivTwo, ivThree, ivFour, ivFive, ivSix;
    String pin = "";
    ErrorDialog errorDialog;
    String from;
    TextView tvHeading;
    TextView tvMessage;
    TextView tvDec;
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_p_i_n);
        context = CreatePINActivity.this;
        setToolBar();
        from = getIntent().getStringExtra("from");
        registerFunctions = new RegisterFunctions(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        tvZero.setOnClickListener(v -> {
            pin = pin + "0";
            setImages(pin.length());
            if (pin.length() == 6) {
                if (validatePIN(pin)) {
                    Intent in = new Intent(context, ConfirmPINActivity.class);
                    in.putExtra("originalPin", pin);
                    in.putExtra("from", from);
                    pinVerificationListener.launch(in);
                }
            }
        });
        ivKeyBordBack.setOnClickListener(v -> {
            if (pin.length() == 0) {
                setImages(0);
            } else {
                pin = pin.substring(0, pin.length() - 1);
                setImages(pin.length());
            }

        });
    }

    private void initView() {
        tvHeading = findViewById(R.id.tv_heading);
        tvMessage = findViewById(R.id.tv_message);
        tvDec = findViewById(R.id.tv_dec);
        tvTime = findViewById(R.id.tv_time);

        if (from.equals("reset")) {
            tvHeading.setText(R.string.reset_pin_heding);
            tvMessage.setText(R.string.add_a_6_digit_pin_to_secure_your_wallet);
            tvDec.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
        } else {
            tvHeading.setText(R.string.create_pin);
            tvMessage.setText(R.string.add_a_6_digit_pin_to_secure_your_wallet);
            tvDec.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
        }

        rvNumbers = findViewById(R.id.rv_numbers);
        linearLayoutManager = new GridLayoutManager(context, 3);
        rvNumbers.setLayoutManager(linearLayoutManager);
        createList();
        ivKeyBordBack = findViewById(R.id.iv_key_bord_back);
        tvZero = findViewById(R.id.iv_zero);
        ivOne = findViewById(R.id.iv_one);
        ivTwo = findViewById(R.id.iv_two);
        ivThree = findViewById(R.id.iv_three);
        ivFour = findViewById(R.id.iv_four);
        ivFive = findViewById(R.id.iv_five);
        ivSix = findViewById(R.id.iv_six);

    }

    private void createList() {
        numbersList = new ArrayList<>();
        numbersList.add("1");
        numbersList.add("2");
        numbersList.add("3");
        numbersList.add("4");
        numbersList.add("5");
        numbersList.add("6");
        numbersList.add("7");
        numbersList.add("8");
        numbersList.add("9");
        numberListAdapter = new NumberListAdapter(numbersList, context, (v, position) -> {
            pin = pin + numbersList.get(position);
            setImages(pin.length());
            if (pin.length() == 6) {
                if (validatePIN(pin)) {
                    Intent in = new Intent(context, ConfirmPINActivity.class);
                    in.putExtra("originalPin", pin);
                    in.putExtra("from", from);
                    pinVerificationListener.launch(in);
                }
            }
        });
        rvNumbers.setAdapter(numberListAdapter);
    }

    private void setImages(int length) {
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
        } else {
            pin = "";
            ivOne.setImageResource(R.drawable.ic_white_round_blue_border);
            ivTwo.setImageResource(R.drawable.ic_white_round_blue_border);
            ivThree.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFour.setImageResource(R.drawable.ic_white_round_blue_border);
            ivFive.setImageResource(R.drawable.ic_white_round_blue_border);
            ivSix.setImageResource(R.drawable.ic_white_round_blue_border);
        }

    }

    private boolean validatePIN(String pin) {
        String reverse = new StringBuilder(pin).reverse().toString();
        if (pin.length() != 6) {
            errorDialog.show(getString(R.string.enter_six_digit_pin));
            return false;
        } else {
            if (checkConsecutive(pin)) {
                errorDialog.show(getString(R.string.please_enter_more_squre_pin));
                setImages(0);
                return false;
            }
            if (!checkSameNumber(pin)) {
                errorDialog.show(getString(R.string.please_enter_more_squre_pin));
                setImages(0);
                return false;
            }
            if (checkConsecutive(reverse)) {
                errorDialog.show(getString(R.string.please_enter_more_squre_pin));
                setImages(0);
                return false;
            }
        }

        return true;
    }

    private boolean checkSameNumber(String pin) {
        String num = pin.substring(0, 1);
        String num1 = pin.substring(1, 2);
        String num2 = pin.substring(2, 3);
        String num3 = pin.substring(3, 4);
        String num4 = pin.substring(4, 5);
        String num5 = pin.substring(5, 6);
        return !num.equals(num1) || !num.equals(num2) || !num.equals(num3) || !num.equals(num4) || !num.equals(num5);
    }

    private boolean checkConsecutive(String str) {
        int length = str.length();
        for (int i = 0; i < length / 2; i++) {
            StringBuilder new_str = new StringBuilder(str.substring(0, i + 1));
            int num = Integer.parseInt(new_str.toString());
            while (new_str.length() < length) {
                num++;
                new_str.append(num);
            }
            if (new_str.toString().equals(str))
                return true;
        }
        return false;
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
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

    ActivityResultLauncher<Intent> pinVerificationListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent in = getIntent();
            in.putExtra("pin", pin);
            setResult(Activity.RESULT_OK, in);
            finish();
        }
    });
}