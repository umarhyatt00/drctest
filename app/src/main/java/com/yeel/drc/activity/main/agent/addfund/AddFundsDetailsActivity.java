package com.yeel.drc.activity.main.agent.addfund;

import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yeel.drc.R;
import com.yeel.drc.api.agentaddfundhistroy.AddFundHistoryListData;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;

import java.util.Objects;

public class AddFundsDetailsActivity extends BaseActivity {
    LinearLayout llMain;
    AddFundHistoryListData addFundHistoryListData;
    Context context;
    TextView tvDate;
    TextView tvAmount;
    TextView tvBankName;
    TextView tvRefNumber;
    TextView tvStatus;
    ImageView ivPhoto;
    LinearLayout llImage;
    LinearLayout llRefNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds_details);
        context=AddFundsDetailsActivity.this;
        addFundHistoryListData=(AddFundHistoryListData) getIntent().getSerializableExtra("data");
        setToolBar();
        initViews();
        setOnClickListener();
    }

    private void setOnClickListener() {
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initViews() {
        llMain=findViewById(R.id.ll_main);
        tvDate=findViewById(R.id.tv_date);
        tvAmount=findViewById(R.id.tv_amount);
        tvBankName=findViewById(R.id.tv_bank_name);
        tvRefNumber=findViewById(R.id.tv_ref_number);
        tvStatus=findViewById(R.id.tv_status);
        ivPhoto=findViewById(R.id.iv_photo);
        llImage=findViewById(R.id.ll_image);
        llRefNumber=findViewById(R.id.ll_ref_number);
        setValues();

    }

    private void setValues() {
        //set to message
        String formattedSubmittedTime=commonFunctions.getGalilioDateFormat(addFundHistoryListData.getDate(),"date");
        String message=context.getString(R.string.submitted)+" "+context.getString(R.string.on)+" "+formattedSubmittedTime;;
        String status = addFundHistoryListData.getStatus();
        if (status.equals("Submitted")) {
            tvStatus.setText(R.string.waiting_under_review);
            tvStatus.setTextColor(context.getColor(R.color.submittedLabelColor));
        } else if (status.equals("Approved")) {
            String formattedApprovedTime=commonFunctions.getGalilioDateFormat(addFundHistoryListData.getDate(),"date");
            tvStatus.setText(getString(R.string.approved_status)+" "+formattedApprovedTime);
            tvStatus.setTextColor(context.getColor(R.color.green));
        } else if (status.equals("Rejected")) {
            tvStatus.setText(R.string.rejected_message);
            tvStatus.setTextColor(context.getColor(R.color.red));
        }else if(status.equals("On-hold")){
            tvStatus.setText(R.string.on_hold_message);
            tvStatus.setTextColor(context.getColor(R.color.holdOnColor));
        }else{
            tvStatus.setText(R.string.waiting_under_review);
            tvStatus.setTextColor(context.getColor(R.color.submittedLabelColor));
        }
        tvDate.setText(message);

        //set amount
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        String amount;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
            amount=sign+" "+commonFunctions.setAmount(addFundHistoryListData.getAmount());
        } else {
            amount=commonFunctions.setAmount(addFundHistoryListData.getAmount()) + " " + sign;
        }
        tvAmount.setText(amount);


        tvBankName.setText(addFundHistoryListData.getBankName());
        if(addFundHistoryListData.getBankReceiptImage()!=null&&!addFundHistoryListData.equals("")){
            llImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(addFundHistoryListData.getCommon_image_url())
                    .placeholder(R.drawable.rectangle)
                    // .apply(new RequestOptions().circleCrop())
                    .into(ivPhoto);
        }else{
            llImage.setVisibility(View.GONE);
        }

        if(addFundHistoryListData.getRefNo()!=null&&!addFundHistoryListData.getRefNo().equals("")){
            llRefNumber.setVisibility(View.VISIBLE);
            tvRefNumber.setText(addFundHistoryListData.getRefNo());
        }else{
            llRefNumber.setVisibility(View.GONE);
        }






    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    //back button click
    @SuppressLint("NonConstantResourceId")
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