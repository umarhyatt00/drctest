package com.yeel.drc.activity.main.agent.cashpickup.history;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.ViewPagerAdapterImageSlider;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.SliderImagesModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PaidCompetedDetailsActivity extends BaseActivity {

    Context context;
    CommonFunctions commonFunctions;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    TextView txtRequestDate, txtRequestAmount, txtRequestRefNumber, txtRequestPickupDetails, txtRequestStatus, textSenderName, textSenderMobile, textSenderAddress, textReceiverName, textReceiverMobile, textReceiverAddress;


    TextView textCashOutReceiverName, textCashOutReceiverEmail, textCashOutReceiverAddress;

    ConstraintLayout layoutCashPickup, layoutCashOut;
    View hlCashPickup, hlCashOut;

    RelativeLayout rlSlider;
    ViewPager viewPager;
    LinearLayout llSliderDots;
    ProgressBar progressBar;
    NestedScrollView mainLayout;

    TransactionsData transactionsData;
    TransactionDetailsData transactionDetailsData;

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

    boolean getTransactionDetailsRetry = false;
    String ydbRefId;
    String notificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_competed_details);
        context = PaidCompetedDetailsActivity.this;
        transactionsData = (TransactionsData) getIntent().getSerializableExtra("data");
        ydbRefId = transactionsData.getYdb_ref_id();
        notificationId = "";
        setToolBar();
        initView();
    }

    private void initView() {
        commonFunctions = new CommonFunctions(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);

        txtRequestDate = findViewById(R.id.txt_request_date);
        txtRequestAmount = findViewById(R.id.text_request_amount);
        txtRequestRefNumber = findViewById(R.id.text_request_ref_number);
        txtRequestPickupDetails = findViewById(R.id.text_request_pickup_details);
        txtRequestStatus = findViewById(R.id.text_request_pickup_status);
        textSenderName = findViewById(R.id.text_sender_name);
        textSenderMobile = findViewById(R.id.text_sender_mobile);
        textSenderAddress = findViewById(R.id.text_sender_address);
        textReceiverName = findViewById(R.id.text_receiver_name);
        textReceiverMobile = findViewById(R.id.text_receiver_mobile);
        textReceiverAddress = findViewById(R.id.text_receiver_address);

        textReceiverAddress = findViewById(R.id.text_receiver_address);

        textCashOutReceiverName = findViewById(R.id.cash_out_text_receiver_name);
        textCashOutReceiverEmail = findViewById(R.id.cash_out_text_receiver_mobile);
        textCashOutReceiverAddress = findViewById(R.id.cash_out_text_receiver_address);

        rlSlider = findViewById(R.id.view_flipper);
        viewPager = findViewById(R.id.viewPager);
        llSliderDots = findViewById(R.id.SliderDots);


        layoutCashOut = findViewById(R.id.cash_out_receiver_detail_layout);
        layoutCashPickup = findViewById(R.id.addSenderDetailLayout);

        hlCashPickup = findViewById(R.id.hl_cash_pickup);
        hlCashOut = findViewById(R.id.hl_cash_out_receiver);

        mainLayout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressBar);
        getTransactionDetails();

    }

    private void getTransactionDetails() {
        viewLoader();
        apiCall.getTransactionDetails(getTransactionDetailsRetry, commonFunctions.getUserId(), ydbRefId, notificationId, false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    hideLoader();
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

    private void viewLoader() {
        mainLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        mainLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void setValue() {

        String message = getString(R.string.requested) + " " + getString(R.string.on);
        txtRequestDate.setText(message + " " + commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "date2"));

        txtRequestAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
        txtRequestRefNumber.setText(transactionDetailsData.getYdb_ref_id());

        txtRequestStatus.setText(getString(R.string.paid_fund_given_on) + " " + commonFunctions.getGalilioDateFormat(transactionDetailsData.getCollectionData(), "date2"));

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
            txtRequestPickupDetails.setText(fullAgentAddress+", "+agentMobileNumber);
        } else {
            txtRequestPickupDetails.setText("Agent Details not available");
        }


        if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
            //cash out
            layoutCashOut.setVisibility(View.VISIBLE);
            hlCashOut.setVisibility(View.VISIBLE);
            layoutCashPickup.setVisibility(View.GONE);
            hlCashPickup.setVisibility(View.GONE);

            UserDetailsData userDetailsData = transactionDetailsData.getSender_detail();
            textCashOutReceiverName.setText(transactionDetailsData.getSender_name());


            String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
            String mobileNumber=commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),mobileNumberFormat);

            textCashOutReceiverEmail.setText(mobileNumber+ (userDetailsData.getEmail() == null? "" : ", "+userDetailsData.getEmail()));

            String cashOutReceiverAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);


            textCashOutReceiverAddress.setText(cashOutReceiverAddress);



        }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){
            layoutCashOut.setVisibility(View.GONE);
            hlCashOut.setVisibility(View.GONE);
            layoutCashPickup.setVisibility(View.VISIBLE);
            hlCashPickup.setVisibility(View.VISIBLE);

            //sender details
            BeneficiaryData senderDetails = transactionDetailsData.getBeneficiaryDetails();
            String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),senderDetails.getCountry_code());
            String senderMobileNumber=commonFunctions.formatAMobileNumber(senderDetails.getMobile(),senderDetails.getCountry_code(),senderMobileNumberFormat);
         /*   String senderFullDetails=commonFunctions.getBeneficiaryFullDetails(senderDetails,senderMobileNumber);
            if(senderFullDetails!=null&&!senderFullDetails.equals("")){
                textSenderAddress.setText(senderFullDetails);
                textSenderAddress.setVisibility(View.VISIBLE);
            }else{
                textSenderAddress.setVisibility(View.GONE);
            }
*/


      /*      textSenderName.setText(transactionDetailsData.getSender_name());
            textSenderName.setVisibility(View.GONE);

            String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),senderDetails.getCountry_code());
            String senderMobileNumber=commonFunctions.formatAMobileNumber(senderDetails.getMobile(),senderDetails.getCountry_code(),senderMobileNumberFormat);
            textSenderMobile.setText(senderMobileNumber+ (senderDetails.getEmail() == null? "" : ", "+senderDetails.getEmail()));

            String senderFullAddress=commonFunctions.getBeneficiaryFullAddress(senderDetails,senderMobileNumber);
            if(senderFullAddress!=null&&!senderFullAddress.equals("")){
                textSenderAddress.setText(senderFullAddress);
                textSenderAddress.setVisibility(View.VISIBLE);
            }else{
                textSenderAddress.setVisibility(View.GONE);
            }*/


        } else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)) {
            layoutCashOut.setVisibility(View.GONE);
            hlCashOut.setVisibility(View.GONE);
            layoutCashPickup.setVisibility(View.VISIBLE);
            hlCashPickup.setVisibility(View.VISIBLE);



            //sender details
            UserDetailsData userDetailsData = transactionDetailsData.getSender_detail();
            String senderMobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),userDetailsData.getCountry_code());
            String senderMobileNumber=commonFunctions.formatAMobileNumber(userDetailsData.getMobile(),userDetailsData.getCountry_code(),senderMobileNumberFormat);
            String senderName=transactionDetailsData.getSender_name();
            String fullAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);
            textSenderAddress.setVisibility(View.VISIBLE);
            textSenderAddress.setText(senderName+"\n"+senderMobileNumber+", "+fullAddress);


    /*        textSenderName.setText(transactionDetailsData.getSender_name());
            textSenderMobile.setText(senderMobileNumber+ (userDetailsData.getEmail() == null? "" : ", "+userDetailsData.getEmail()));
            String cashOutSenderAddress=commonFunctions.getFullAddressOfAUser(userDetailsData);
            if(cashOutSenderAddress!=null&&!cashOutSenderAddress.equals("")){
                textSenderAddress.setText(cashOutSenderAddress);
                textSenderAddress.setVisibility(View.VISIBLE);
            }else{
                textSenderAddress.setVisibility(View.GONE);
            }
*/

            //receiver details
            BeneficiaryData beneficiaryData = transactionDetailsData.getBeneficiaryDetails();
            String fullName = commonFunctions.generateFullName(beneficiaryData.getFirstname(), beneficiaryData.getMiddleName(), beneficiaryData.getLastname());
            textReceiverName.setText(fullName);

            String receiverNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),beneficiaryData.getCountry_code());
            String receiverMobileNumber=commonFunctions.formatAMobileNumber(beneficiaryData.getMobile(),beneficiaryData.getCountry_code(),receiverNumberFormat);
            textReceiverMobile.setText(receiverMobileNumber+ (beneficiaryData.getEmail() == null? "" : ", "+beneficiaryData.getEmail()));

            String address = "";
            if (beneficiaryData.getCity() != null)
                address = beneficiaryData.getCity() + ", ";
            if (beneficiaryData.getState() != null) {
                address = address + (beneficiaryData.getState() + ", ");
            }
            if (beneficiaryData.getCountry() != null) {
                address = address + (beneficiaryData.getCountry());
            }

            textReceiverAddress.setText(address);
        }


        //set doc details
  /*      String docUrl = transactionDetailsData.getBeneficiary_docs_url();
        if (transactionDetailsData.getTransaction_type().equals(Constants.TRANSACTION_TYPE_CASH_OUT)) {
            String signatureURL = docUrl + transactionDetailsData.getBeneficiary_docs().getSignature_image();
            imagesList = new ArrayList<>();
            imagesList.add(new SliderImagesModel("Signature Image", "Signature Image", signatureURL));
        } else {
            String signatureURL = docUrl + transactionDetailsData.getBeneficiary_docs().getSignature_image();
            String idURL = docUrl + transactionDetailsData.getBeneficiary_docs().getId_image();
            imagesList = new ArrayList<>();
            imagesList.add(new SliderImagesModel("ID Image", "ID Image", idURL));
            imagesList.add(new SliderImagesModel("Signature Image", "Signature Image", signatureURL));
        }*/
       // setSlider();

    }


    private void setSlider() {
        viewPagerAdapter = new ViewPagerAdapterImageSlider(this, imagesList, new ViewPagerAdapterImageSlider.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {

            }
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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