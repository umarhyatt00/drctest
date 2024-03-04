package com.yeel.drc.activity.main.billpayments.internal;
import androidx.appcompat.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.billpayments.BillConfirmationActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.billpayments.BillPaymentsData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import java.util.Objects;
public class EducationDetailsConfirmActivity extends BaseActivity {
    BillPaymentsData billPaymentData;
    Context context;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    APICall apiCall;
    TextView tvStudentName;
    TextView tvStudentID;
    TextView tvStudentMobile;
    TextView tvAmount;
    TextView tvRemark;
    LinearLayout llRemark;
    TextView tvNext;
    TextView tvProviderName;
    ImageView ivProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details_confirm);
        context=EducationDetailsConfirmActivity.this;
        billPaymentData =(BillPaymentsData) getIntent().getSerializableExtra("data");
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {
       tvNext.setOnClickListener(view -> {
           Intent intent=new Intent(context, BillConfirmationActivity.class);
           intent.putExtra("data", billPaymentData);
           startActivity(intent);
       });
    }

    private void initView() {
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        tvStudentName=findViewById(R.id.tvStudentName);
        tvStudentID=findViewById(R.id.tvStudentID);
        tvStudentMobile=findViewById(R.id.tvStudentMobile);
        tvAmount=findViewById(R.id.tv_amount);
        tvRemark=findViewById(R.id.tvRemarks);
        llRemark=findViewById(R.id.ll_remark);
        tvNext=findViewById(R.id.tv_button);
        tvProviderName=findViewById(R.id.tv_provider_name);
        ivProvider=findViewById(R.id.iv_provider);

        setValues();
    }

    private void setValues() {
        setProviderDetails();
        tvStudentName.setText(billPaymentData.getStudentName());
        tvStudentID.setText(billPaymentData.getStudentId());

        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),billPaymentData.getStudentMobileCountyCode());
        String number= commonFunctions.formatAMobileNumber(billPaymentData.getStudentMobileNumber(),billPaymentData.getStudentMobileCountyCode(),mobileNumberFormat);
        tvStudentMobile.setText(number);

        tvAmount.setText(commonFunctions.setAmount(billPaymentData.getAmount())+" "+ SthiramValues.SELECTED_CURRENCY_SYMBOL);
        if(billPaymentData.getRemark()!=null&&!billPaymentData.equals("")){
            llRemark.setVisibility(View.VISIBLE);
            tvRemark.setText(billPaymentData.getRemark());
        }else{
            llRemark.setVisibility(View.GONE);
        }
    }
    private void setProviderDetails() {
        ProvidersListData providersListData=billPaymentData.getProvidersListData();
        tvProviderName.setText(billPaymentData.getProvidersListData().getProvider_name());
        Glide.with(ivProvider.getContext()).load(SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL+""+providersListData.getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProvider);
    }


    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.verify_details);
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
}