package com.yeel.drc.activity.main;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.agent.cashpickup.CashPickupSenderReceiverDetailsActivity;
import com.yeel.drc.activity.main.agent.common.AddBeneficiaryDetailsActivity;
import com.yeel.drc.activity.main.agent.sendcash.SenderReceiverDetailsActivity;
import com.yeel.drc.activity.main.cashpickup.CashPickupBeneficiaryDetailsActivity;
import com.yeel.drc.activity.main.homepayandsend.SendViaYeelActivity;
import com.yeel.drc.activity.main.mobilepay.MobilePayEstimateActivity;
import com.yeel.drc.adapter.ModeOfPaymentAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.basicdetails.BasicDetailsResponse;
import com.yeel.drc.api.basicdetails.PaymentMethodsData;
import com.yeel.drc.dialogboxes.AddNewCurrencyDialog;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.BeneficiaryCommonData;
import com.yeel.drc.model.mpesa.MobilePayData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.List;
import java.util.stream.Collectors;

public class ModeOfPaymentActivity extends BaseActivity {
    Context context;
    String userType;
    String agentType;
    APICall apiCall;
    List<PaymentMethodsData> paymentMethodsList,paymentMethodsListFilter;
    RecyclerView rvList;
    LinearLayoutManager linearLayoutManager;
    ModeOfPaymentAdapter modeOfPaymentAdapter;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    AddNewCurrencyDialog addNewCurrencyDialog;
    String selectedMobilePayName="";
    String selectedMobilePayCode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_of_payment);
        context = ModeOfPaymentActivity.this;
        setToolBar();
        initView();
    }

    private void initView() {
        addNewCurrencyDialog=new AddNewCurrencyDialog(context,commonFunctions);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        userType = commonFunctions.getUserType();
        agentType = commonFunctions.getAgentType();
        rvList = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        setList();
    }

    private void setList() {
        if (!commonFunctions.getBasicDetails().equals("")) {
            String jsonString = apiCall.getJsonFromEncryptedString(commonFunctions.getBasicDetails());
            Gson gson = new Gson();
            BasicDetailsResponse apiResponse = gson.fromJson(jsonString, BasicDetailsResponse.class);
            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                paymentMethodsList = apiResponse.getPaymentMethodsList();
            }
        }
        paymentMethodsListFilter= paymentMethodsList.stream().filter(p -> p.getType().equals("Payment")).collect(Collectors.toList());
        if (paymentMethodsListFilter != null) {
            modeOfPaymentAdapter = new ModeOfPaymentAdapter(commonFunctions, context, paymentMethodsListFilter, (v, position) -> {
                    String methodCode = paymentMethodsListFilter.get(position).getMethodCode();
                    String status = paymentMethodsListFilter.get(position).getStatus();
                switch (status) {
                    case "1":
                        //active
                        if (methodCode.equals(SthiramValues.SEND_VIA_YEEL_CODE)) {
                            Intent in;
                            if (userType.equals(ACCOUNT_TYPE_AGENT)) {
                                in = new Intent(context, SenderReceiverDetailsActivity.class);
                            } else {
                                in = new Intent(context, SendViaYeelActivity.class);
                            }
                            startActivity(in);
                        } else if (methodCode.equals(SthiramValues.CASH_PICKUP_CODE)) {
                            Intent in;
                            if (userType.equals(ACCOUNT_TYPE_AGENT)) {
                                in = new Intent(context, CashPickupSenderReceiverDetailsActivity.class);
                            } else {
                                in = new Intent(context, CashPickupBeneficiaryDetailsActivity.class);
                            }
                            startActivity(in);
                        } else if (methodCode.equals(SthiramValues.MPESA_KENYA_CODE) || methodCode.equals(SthiramValues.AIRTAL_UGANDA_CODE)) {
                            if (SthiramValues.SELECTED_CURRENCY_ID.equals("2")) {
                                selectedMobilePayName = paymentMethodsListFilter.get(position).getMethodName();
                                selectedMobilePayCode = paymentMethodsListFilter.get(position).getMethodCode();
                                if (userType.equals(ACCOUNT_TYPE_AGENT)) {
                                    Intent in = new Intent(getBaseContext(), AddBeneficiaryDetailsActivity.class);
                                    in.putExtra("customer_type", "sender");
                                    in.putExtra("from", "mobilePay");
                                    addBeneficiaryListener.launch(in);
                                } else {
                                    MobilePayData mobilePayData = new MobilePayData();
                                    mobilePayData.setMobilePayName(selectedMobilePayName);
                                    mobilePayData.setMobilePayCode(selectedMobilePayCode);
                                    Intent in = new Intent(context, MobilePayEstimateActivity.class);
                                    in.putExtra("data", mobilePayData);
                                    startActivity(in);
                                }
                            } else {
                                addNewCurrencyDialog.show(getString(R.string.mobile_pay_not_available_messages));
                            }
                        } else {
                            errorDialog.show(getString(R.string.coming_soon));
                        }
                        break;
                    case "0":
                        //disabled
                        somethingWentWrongDialog.show();
                        break;
                    case "2":
                        //coming soon
                        errorDialog.show(getString(R.string.coming_soon));
                        break;
                }
            });
            rvList.setAdapter(modeOfPaymentAdapter);
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
    ActivityResultLauncher<Intent> addBeneficiaryListener = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            if (result.getData().hasExtra("customer_data")) {
                BeneficiaryCommonData customerData = (BeneficiaryCommonData) result.getData().getSerializableExtra("customer_data");
                MobilePayData mpesaData = new MobilePayData();
                mpesaData.setSenderDetails(customerData);
                mpesaData.setMobilePayName(selectedMobilePayName);
                mpesaData.setMobilePayCode(selectedMobilePayCode);
                Intent in = new Intent(getBaseContext(), MobilePayEstimateActivity.class);
                in.putExtra("data", mpesaData);
                startActivity(in);
            }
        }
    });

}