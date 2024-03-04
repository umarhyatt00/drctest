package com.yeel.drc.activity.main.receipt.debit;

import static com.yeel.drc.utils.SthiramValues.ACCOUNT_TYPE_AGENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity;
import com.yeel.drc.activity.main.homepayandsend.AmountEnteringActivity;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsData;
import com.yeel.drc.api.transactiondetails.TransactionDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.PermissionUtils;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;

import java.util.List;
import java.util.Objects;

public class MoneySentReceiptActivity extends BaseActivity {
    private ProgressBar progressBar;
    private LinearLayout llMain;
    private LinearLayout llShare;
    private TextView tvReceiverName;
    private TextView tvReceiverMobile;
    private RelativeLayout rlTvImage;
    private TextView tvImage;
    private RelativeLayout rlIvImage;
    private ImageView tvLogo;
    private TextView tvReceivedDate;
    private TextView tvTransactionId;
    private TextView tvDeliveryMethod;
    private TextView tvSenderName;
    private TextView tvRemarks;
    private TextView tvSendAmount;
    private TextView tvFees;
    private TextView tvTotalToPay;
    private TextView tvSendAgain;
    private MoneySentReceiptActivity context;
    private DialogProgress dialogProgress;
    LinearLayout llRemark;
    Bitmap myBitmapImage = null;
    String ydbRefId;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    TransactionDetailsData transactionDetailsData;
    String notificationId;
    boolean getTransactionDetailsRetry = false;
    PermissionUtils permissionUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_sent_receipt);
        context = MoneySentReceiptActivity.this;
        dialogProgress = new DialogProgress(context);
        ydbRefId = getIntent().getStringExtra("ydb_ref_id");
        notificationId = getIntent().getStringExtra("notificationId");
        if (notificationId == null) {
            notificationId = "";
        }
        setToolBar();
        initViews();
        setOnClickListener();
    }

    private void setOnClickListener() {
        tvSendAgain.setOnClickListener(view -> {
            if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
                AgentData agentData = transactionDetailsData.getAgentDetails();
                List<CurrencyListData> currencyList = transactionDetailsData.getCurrencyList();
                boolean isAccountNumberAvailable = false;
                String receiverAccountNumber = "";

                if (currencyList != null) {
                    //check currency available or not
                    for (int i = 0; i < currencyList.size(); i++) {
                        if (commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())) {
                            receiverAccountNumber = currencyList.get(i).getAccount_number();
                            isAccountNumberAvailable = true;
                            break;
                        }
                    }
                    if (isAccountNumberAvailable) {
                        agentData.setAccount_no(receiverAccountNumber);
                        Intent intent = new Intent(context, CashOutAmountEnteringActivity.class);
                        intent.putExtra("data", agentData);
                        startActivity(intent);
                    } else {
                        String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                        errorDialog.show(message);
                    }
                } else {
                    String message = getString(R.string.receiver_not_matching_message) + SthiramValues.SELECTED_CURRENCY_SYMBOL + getString(R.string.receiver_not_matching_message_2);
                    errorDialog.show(message);
                }
            } else {
                String type = "";
                if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)) {
                    type = SthiramValues.ACCOUNT_TYPE_BUSINESS;
                } else {
                    type = SthiramValues.ACCOUNT_TYPE_INDIVIDUAL;
                }

                UserDetailsData userDetails = new UserDetailsData(
                        transactionDetailsData.getReceiver_name(),
                        transactionDetailsData.getProfile_image(),
                        transactionDetailsData.getReceiver_account_no() + "",
                        transactionDetailsData.getReceiver_id() + "",
                        type
                );
                Intent in = new Intent(context, AmountEnteringActivity.class);
                in.putExtra("from", "SendAgain");
                in.putExtra("data", userDetails);
                startActivity(in);
            }
        });
    }

    private void initViews() {
        permissionUtils=new PermissionUtils(context);
        apiCall = new APICall(context, SthiramValues.finish);
        errorDialog = new ErrorDialog(context, SthiramValues.finish);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.finish);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        llMain = findViewById(R.id.ll_main);
        llMain.setVisibility(View.GONE);
        llShare = findViewById(R.id.ll_share);
        tvReceiverName = findViewById(R.id.tvReceiverName);
        tvReceiverMobile = findViewById(R.id.tvReceiverMobile);
        rlTvImage = findViewById(R.id.rl_tv_image);
        tvImage = findViewById(R.id.tv_image);
        rlIvImage = findViewById(R.id.rl_iv_image);
        tvLogo = findViewById(R.id.tv_logo);
        tvReceivedDate = findViewById(R.id.tvReceivedDate);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvDeliveryMethod = findViewById(R.id.tvDeliveryMethod);
        tvSenderName = findViewById(R.id.tv_sender_name);
        tvRemarks = findViewById(R.id.tv_remarks);
        tvSendAmount = findViewById(R.id.tvSendAmount);
        tvFees = findViewById(R.id.tvFees);
        tvTotalToPay = findViewById(R.id.tvTotalToPay);
        tvSendAgain = findViewById(R.id.tvSendAgain);
        llRemark = findViewById(R.id.ll_remark);

        getTransactionDetails();

    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_gray);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_share_gray, menu);
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
            case R.id.id_share:
                if (transactionDetailsData.getStatus().toLowerCase().equals(SthiramValues.SUCCESS)) {
                    if(permissionUtils.checkGalleryPermissionShare(requestPermissionShare)){
                        new bitmapAsyncNew().execute();
                    }
                } else {
                    Toast.makeText(context, "Failed to share", Toast.LENGTH_SHORT).show();
                }

                return true;

        }
        return false;
    }


    @SuppressLint("StaticFieldLeak")
    class bitmapAsyncNew extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogProgress.show();
        }

        protected String doInBackground(String... urls) {
            try {
                myBitmapImage = Bitmap.createBitmap(llShare.getWidth(), llShare.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(myBitmapImage);
                llShare.draw(canvas);
                return "";
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String feed) {
            if (myBitmapImage != null) {
                commonFunctions.logAValue("Bitmap status", "bitmap  is present");
                shareImage();
                dialogProgress.dismiss();
            } else {
                commonFunctions.logAValue("Bitmap status", "bitmap is empty");
                dialogProgress.dismiss();

            }
        }
    }

    public void shareImage() {
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), myBitmapImage, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        shareIntent.putExtra("subject", "Transaction Details");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Transaction Details");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "text message"));
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
                        setValues();
                    } else {
                        errorDialog.show(apiResponse.getMessage());
                    }
                } catch (Exception e) {
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

    private void setValues() {
        llMain.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvReceiverName.setText(transactionDetailsData.getReceiver_name());

        String mobileNumberFormat = apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(), transactionDetailsData.getCountryCode());
        tvReceiverMobile.setText(commonFunctions.formatAMobileNumber(transactionDetailsData.getMobile(), transactionDetailsData.getCountryCode(), mobileNumberFormat));


        tvTransactionId.setText(transactionDetailsData.getYdb_ref_id());
        if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT_MERCHANT)) {
            tvDeliveryMethod.setText(SthiramValues.TRANSACTION_TYPE_YEEL_ACCOUNT);
        } else {
            tvDeliveryMethod.setText(transactionDetailsData.getTransaction_type());
        }

        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        if (transactionDetailsData.getTransaction_type().equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)) {
            if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
                tvSendAmount.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getAmount()));
                tvTotalToPay.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()));
                tvFees.setText(sign + " " + commonFunctions.setAmount(transactionDetailsData.getCommission_amount()));
            } else {
                tvSendAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvTotalToPay.setText(commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvFees.setText(commonFunctions.setAmount(transactionDetailsData.getCommission_amount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
            }

        } else {

            if (transactionDetailsData.getCommission_amount() != null && !transactionDetailsData.getCommission_amount().equals("0")) {
                tvSendAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvTotalToPay.setText(commonFunctions.setAmount(transactionDetailsData.getTotal_transaction_amount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvFees.setText(commonFunctions.setAmount(transactionDetailsData.getCommission_amount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);

            } else {
                tvSendAmount.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvTotalToPay.setText(commonFunctions.setAmount(transactionDetailsData.getAmount()) + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
                tvFees.setText(commonFunctions.setAmount("0") + " " + SthiramValues.SELECTED_CURRENCY_SYMBOL);
            }

        }


        tvReceivedDate.setText(commonFunctions.getGalilioDateFormat(transactionDetailsData.getTransaction_date(), "time"));
        if (commonFunctions.getUserType().equals(ACCOUNT_TYPE_AGENT)) {
            if (commonFunctions.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
                tvSenderName.setText(commonFunctions.getFullName());
            } else {
                tvSenderName.setText(commonFunctions.getBusinessName());
            }
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)) {
            tvSenderName.setText(commonFunctions.getFullName());
        } else if (commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
            tvSenderName.setText(commonFunctions.getBusinessName());
        }

        if (transactionDetailsData.getRemarks() != null && !transactionDetailsData.getRemarks().equals("")) {
            tvRemarks.setText(transactionDetailsData.getRemarks());
            llRemark.setVisibility(View.VISIBLE);
        } else {
            llRemark.setVisibility(View.GONE);
            tvRemarks.setText("----");
        }


        String firstLetter = String.valueOf(transactionDetailsData.getReceiver_name().charAt(0));
        tvImage.setText(firstLetter);

        //profile image
        String profileImage = transactionDetailsData.getProfile_image();
        if (profileImage != null && !profileImage.equals("")) {
            rlIvImage.setVisibility(View.VISIBLE);
            rlTvImage.setVisibility(View.GONE);
            Glide.with(MoneySentReceiptActivity.this)
                    .load(profileImage)
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(tvLogo);
        } else {
            rlIvImage.setVisibility(View.GONE);
            rlTvImage.setVisibility(View.VISIBLE);
        }

        //status color
        if (transactionDetailsData.getStatus().toLowerCase().equals(SthiramValues.SUCCESS)) {
            tvSendAgain.setVisibility(View.VISIBLE);
        } else if (transactionDetailsData.getStatus().toLowerCase().equals(SthiramValues.PENDING)) {
            tvSendAgain.setVisibility(View.GONE);
        } else {
            tvSendAgain.setVisibility(View.GONE);
        }
    }


    private final ActivityResultLauncher<String> requestPermissionShare =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    new bitmapAsyncNew().execute();
                }
            });

}