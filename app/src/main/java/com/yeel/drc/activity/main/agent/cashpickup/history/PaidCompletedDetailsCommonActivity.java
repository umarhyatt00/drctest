package com.yeel.drc.activity.main.agent.cashpickup.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.ViewPagerAdapterImageSlider;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SliderImagesModel;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PaidCompletedDetailsCommonActivity extends AppCompatActivity {
    Context context;
    String ydbRefId;
    String notificationId;

    CommonFunctions commonFunctions;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    boolean getTransactionDetailsRetry = false;
    TransactionDetailsData transactionDetailsData;

    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    TextView tvRequestedDate;
    TextView tvRequestedAmount;
    TextView tvReffNo;
    TextView tvPickupAgentDetails;
    TextView tvStatus;
    TextView tvSenderDetails;
    TextView tvReceiverDetails;
    LinearLayout llSenderDetails;
    LinearLayout llReceiverDetails;


    ViewPager viewPager;
    LinearLayout llSliderDots;
    private int dotsCount;
    private ImageView[] dots;
    List<SliderImagesModel> imagesList;
    ViewPagerAdapterImageSlider viewPagerAdapter;
    Handler handler;
    Runnable Update;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 5000;
    final long PERIOD_MS = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paind_completed_details_common);
        context=PaidCompletedDetailsCommonActivity.this;
        ydbRefId=getIntent().getStringExtra("ydb_ref_id");
        notificationId=getIntent().getStringExtra("notificationId");
        if(notificationId==null){
            notificationId="";
        }
        setToolBar();
        initView();
        setItemListeners();
    }

    private void setItemListeners() {

    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);

        nestedScrollView=findViewById(R.id.nested_scroll);
        nestedScrollView.setVisibility(View.GONE);
        progressBar=findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        tvRequestedDate=findViewById(R.id.txt_request_date);
        tvRequestedAmount=findViewById(R.id.text_request_amount);
        tvReffNo=findViewById(R.id.text_request_ref_number);
        tvPickupAgentDetails=findViewById(R.id.text_request_pickup_details);
        tvStatus=findViewById(R.id.text_request_pickup_status);
        tvSenderDetails=findViewById(R.id.tv_sender_details);
        tvReceiverDetails=findViewById(R.id.tv_receiver_details);
        llSenderDetails=findViewById(R.id.ll_sender_details);
        llReceiverDetails=findViewById(R.id.ll_receiver_details);

        viewPager = findViewById(R.id.viewPager);
        llSliderDots = findViewById(R.id.SliderDots);

        getTransactionDetails();
    }

    private void getTransactionDetails() {
        apiCall.getTransactionDetails(getTransactionDetailsRetry, commonFunctions.getUserId(), ydbRefId, notificationId, false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    TransactionDetailsResponse apiResponse = gson.fromJson(jsonString, TransactionDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        transactionDetailsData = apiResponse.getTransactionDetailsData();
                        setValue();
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("Error",e+"");
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            }

            @Override
            public void retry() {
                getTransactionDetailsRetry = true;
                getTransactionDetails();
            }
        });
    }

    private void setValue() {
        progressBar.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.VISIBLE);

        //set common details details
        String message = getString(R.string.requested) + " " + getString(R.string.on);
        tvRequestedDate.setText(message + " " + commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "date2"));

        tvRequestedAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        tvReffNo.setText(transactionDetailsData.getYdb_ref_id());

        tvStatus.setText(getString(R.string.paid_fund_given_on) + " " + commonFunctions.getGalilioDateFormat(transactionDetailsData.getCollectionData(), "date2"));

        String transactionType=transactionDetailsData.getTransaction_type();

        //set agent details
        AgentData agentData=null;
        if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){
            agentData = transactionDetailsData.getReceiver_agent_detail();
        }else{
            agentData = transactionDetailsData.getAgentDetails();
        }

        if (agentData != null) {
            String fullAgentAddress=commonFunctions.getAgentNameWithFullAddress(agentData);
            String agentMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),agentData.getCountry_code());
            String agentMobileNumber=commonFunctions.formatAMobileNumber(agentData.getMobile(),agentData.getCountry_code(),agentMobileNumberFormat);
            tvPickupAgentDetails.setText(fullAgentAddress+", "+agentMobileNumber);
        } else {
            tvPickupAgentDetails.setText("Agent Details not available");
        }


       if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
           //sender and reciver is same for cash out so hide one
           llSenderDetails.setVisibility(View.GONE);
           llReceiverDetails.setVisibility(View.VISIBLE);

           //receevier details
           UserDetailsData userDetailsData = transactionDetailsData.getSender_detail();
           String receiverName=transactionDetailsData.getSender_name();
           String receiverMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
           String receiverMobileNumber=commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),receiverMobileNumberFormat);
           String receiverEmail=userDetailsData.getEmail();
           String receiverFullAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);
           String fullDetailsOfAYeelUser= commonFunctions.getFullDetailsOfAUser(receiverName,receiverMobileNumber,receiverEmail,receiverFullAddress);
           tvReceiverDetails.setText(fullDetailsOfAYeelUser);


       }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)){
           llSenderDetails.setVisibility(View.VISIBLE);
           llReceiverDetails.setVisibility(View.VISIBLE);


           //sender details
           UserDetailsData senderDetailsData = transactionDetailsData.getSender_detail();
           String senderName=transactionDetailsData.getSender_name();
           String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),senderDetailsData.getCountry_code());
           String senderMobileNumber=commonFunctions.formatAMobileNumber(senderDetailsData.getMobile(),senderDetailsData.getCountry_code(),senderMobileNumberFormat);
           String senderEmail=senderDetailsData.getEmail();
           String senderFullAddress=commonFunctions.getFullAddressOfAUser(senderDetailsData);
           String fullDetailsOfASender= commonFunctions.getFullDetailsOfAUser(senderName,senderMobileNumber,senderEmail,senderFullAddress);
           tvSenderDetails.setText(fullDetailsOfASender);


           //receiver details
           BeneficiaryData receiverDetails = transactionDetailsData.getBeneficiaryDetails();
           String receiverFullName=commonFunctions.generateFullName(receiverDetails.getFirstname(),receiverDetails.getMiddleName(),receiverDetails.getLastname());
           String receiverMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),receiverDetails.getCountry_code());
           String receiverMobileNumber=commonFunctions.formatAMobileNumber(receiverDetails.getMobile(),receiverDetails.getCountry_code(),receiverMobileNumberFormat);
           String receiverEmail=receiverDetails.getEmail();
           String receiverAddress=receiverDetails.getCountry();
           String fullDetailsOfReceiver=commonFunctions.getFullDetailsOfAUser(receiverFullName, receiverMobileNumber,receiverEmail,receiverAddress);
           tvReceiverDetails.setText(fullDetailsOfReceiver);



       }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){
           llSenderDetails.setVisibility(View.VISIBLE);
           llReceiverDetails.setVisibility(View.VISIBLE);

           //sender details
           BeneficiaryData senderDetails = transactionDetailsData.getRemitterDetails();
           String senderFullName=commonFunctions.generateFullName(senderDetails.getFirstname(),senderDetails.getMiddleName(),senderDetails.getLastname());
           String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),senderDetails.getCountry_code());
           String senderMobileNumber=commonFunctions.formatAMobileNumber(senderDetails.getMobile(),senderDetails.getCountry_code(),senderMobileNumberFormat);
           String senderEmail=senderDetails.getEmail();
           String senderAddress=senderDetails.getCountry();
           String fullDetailsOfSender=commonFunctions.getFullDetailsOfAUser(senderFullName, senderMobileNumber,senderEmail,senderAddress);
           tvSenderDetails.setText(fullDetailsOfSender);

           //receiver details

           BeneficiaryData receiverDetails = transactionDetailsData.getBeneficiaryDetails();
           String receiverFullName=commonFunctions.generateFullName(receiverDetails.getFirstname(),receiverDetails.getMiddleName(),receiverDetails.getLastname());
           String receiverMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),receiverDetails.getCountry_code());
           String receiverMobileNumber=commonFunctions.formatAMobileNumber(receiverDetails.getMobile(),receiverDetails.getCountry_code(),receiverMobileNumberFormat);
           String receiverEmail=receiverDetails.getEmail();
           String receiverAddress=receiverDetails.getCountry();
           String fullDetailsOfReceiver=commonFunctions.getFullDetailsOfAUser(receiverFullName, receiverMobileNumber,receiverEmail,receiverAddress);
           tvReceiverDetails.setText(fullDetailsOfReceiver);


       }


        //set doc details
        String docUrl = transactionDetailsData.getBeneficiary_docs_url();
        if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
            String signatureURL = docUrl + transactionDetailsData.getBeneficiary_docs().getSignature_image();
            imagesList = new ArrayList<>();
            imagesList.add(new SliderImagesModel("Signature Image", "Signature Image", signatureURL));
        } else {
            if(transactionDetailsData.getBeneficiary_docs()!=null){
                String signatureURL = docUrl + transactionDetailsData.getBeneficiary_docs().getSignature_image();
                String idURL = docUrl + transactionDetailsData.getBeneficiary_docs().getId_image();
                imagesList = new ArrayList<>();
                imagesList.add(new SliderImagesModel("ID Image", "ID Image", idURL));
                imagesList.add(new SliderImagesModel("Signature Image", "Signature Image", signatureURL));
            }else {

            }
        }

        if(imagesList!=null&&imagesList.size()!=0){
            setSlider();
        }
    }

    private void setSlider() {
        viewPagerAdapter = new ViewPagerAdapterImageSlider(this, imagesList, (v, position) -> {

        });
        viewPager.setAdapter(viewPagerAdapter);

        //auto slider
        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == viewPagerAdapter.getCount() + 1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        if (imagesList.size() != 1) {
            llSliderDots.setVisibility(View.VISIBLE);
            dotsCount = viewPagerAdapter.getCount();
            dots = new ImageView[dotsCount];
            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                llSliderDots.addView(dots[i], params);

            }

            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            llSliderDots.setVisibility(View.INVISIBLE);
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