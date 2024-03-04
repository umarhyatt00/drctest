package com.yeel.drc.activity.main.cashout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.CashOutData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.Objects;

public class CashOutAmountEnteringActivity extends BaseActivity {
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;

    Context context;
    AgentData agentData;
    CashOutData cashOutData;

    private EditText etCashOutAmount, etPurpose;
    TextView tvCurrencySymbol;
    private TextView btnContinue;
    TextInputLayout til_additionalNote;
    String remark="";
    private double amount;
    private boolean validAmount = false;
    private boolean validRemark = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_out_amount_entering);
        context = CashOutAmountEnteringActivity.this;
        agentData = (AgentData) getIntent().getSerializableExtra("data");
        initView();
        setToolBar();
        setItemListeners();
    }

    private void setItemListeners() {
        btnContinue.setOnClickListener(v -> {
            String message=commonFunctions.checkTransactionIsPossible(amount+"",amount+"", SthiramValues.TRANSACTION_TYPE_CASH_OUT);
            if(message.equals(SthiramValues.SUCCESS)){
                cashOutData=new CashOutData();
                cashOutData.setAgentData(agentData);
                cashOutData.setAmount(amount+"");
                cashOutData.setRemark(remark);
                Intent in = new Intent(getBaseContext(), CashOutConfirmationActivity.class);
                in.putExtra("data", cashOutData);
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


    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        apiCall = new APICall(context, SthiramValues.finish);

        etCashOutAmount = findViewById(R.id.edtCashOutAmount);
        etPurpose = findViewById(R.id.edtAdditionalNote);
        btnContinue = findViewById(R.id.tv_continue);
        til_additionalNote=findViewById(R.id.til_additionalNote);

        tvCurrencySymbol=findViewById(R.id.tv_currency_symbol);
        tvCurrencySymbol.setText(SthiramValues.SELECTED_CURRENCY_SYMBOL);

        setContinueBtn();
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


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.cash_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_info_icon, menu);
        return true;
    }


    //back button click
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_info:
                mDialogueCashOut();
                return true;

        }
        return false;
    }


    private void mDialogueCashOut() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_alert_cashout_info);
        TextView description = dialog.findViewById(R.id.description);
        TextView title = dialog.findViewById(R.id.title);
        title.setText(R.string.what_is_cash_out);
        description.setText(R.string.you_can_get_cash_by_visiting_any_of_the_yeel_delegates_agents_worldwide_just_scan_the_qr_code_or_enter_the_delegate_agent_code_and_cash_out);
        TextView dialogButton = dialog.findViewById(R.id.tv_ok);
        dialogButton.setOnClickListener(v -> {
            if (!isFinishing() || !isDestroyed()) {
                dialog.dismiss();
            }

        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        if (!isFinishing()) {
            dialog.show();
        }


    }
}