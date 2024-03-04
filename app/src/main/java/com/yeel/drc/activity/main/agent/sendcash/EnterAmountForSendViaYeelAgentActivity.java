package com.yeel.drc.activity.main.agent.sendcash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.yeel.drc.R;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.model.sendviayeel.SendYeelToYeelModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class EnterAmountForSendViaYeelAgentActivity extends BaseActivity {

    SendYeelToYeelModel sendYeelToYeelData;
    CommonFunctions commonFunctions;
    ErrorDialog errorDialog;
    TextView tvCurrencySymbol;


    private EditText etCashOutAmount, etPurpose;
    private TextView btnContinue;
    TextInputLayout til_additionalNote;
    String remark="";
    private double amount;

    Context context;
    private boolean validAmount = false;
    private boolean validRemark = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);
        context = EnterAmountForSendViaYeelAgentActivity.this;

        sendYeelToYeelData = (SendYeelToYeelModel) getIntent().getSerializableExtra("data");

        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
        btnContinue.setOnClickListener(v -> {
            String message=commonFunctions.checkTransactionIsPossible(amount+"",amount+"", SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL);
            if(message.equals(SthiramValues.SUCCESS)){
                sendYeelToYeelData.setAmount(amount+"");
                sendYeelToYeelData.setPurpose(remark);
                Intent in = new Intent(getBaseContext(), SendViaYeelAgentSideConfirmationActivity.class);
                in.putExtra("data", sendYeelToYeelData);
                startActivity(in);
            }
        });

        etPurpose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                remark = etPurpose.getText().toString().trim();
                if(commonFunctions.validateRemark(remark)){
                    validRemark=true;
                    til_additionalNote.setError(null);
                }else{
                    validRemark=false;
                    if(til_additionalNote.getError()==null||til_additionalNote.getError().equals("")){
                        til_additionalNote.setError(getString(R.string.enter_valid_remark));
                    }
                }
                setContinueBtn();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etCashOutAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence enteredAmount, int start, int before, int count) {
                if(commonFunctions.validAmount(etCashOutAmount.getText().toString())){
                    amount = Double.parseDouble(etCashOutAmount.getText().toString());
                    validAmount = true;
                }else{
                    amount = 0.0;
                    validAmount = false;
                }
                setContinueBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setContinueBtn() {
        if (validAmount&&validRemark) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.bg_button_one);
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundResource(R.drawable.bg_button_three);
        }

    }

    private void initView() {
        commonFunctions=new CommonFunctions(context);
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);

        etCashOutAmount = findViewById(R.id.edtCashOutAmount);
        etPurpose = findViewById(R.id.edtAdditionalNote);
        btnContinue = findViewById(R.id.tv_continue);
        til_additionalNote=findViewById(R.id.til_additionalNote);

        tvCurrencySymbol=findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);

        setContinueBtn();
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.amount_to_send);
    }

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