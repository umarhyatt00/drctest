package com.yeel.drc.activity.main.homepayandsend;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity;
import com.yeel.drc.adapter.contacts.AdapterContactList;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.synccontact.SynContactResponse;
import com.yeel.drc.api.synccontact.SyncContactData;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import java.util.ArrayList;
import java.util.List;

public class ContactSearchActivity extends BaseActivity {
    ProgressBar progressBar;
    TextView tvNoItem;
    TextView tvCancel;
    EditText edtSearch;
    RecyclerView rvContactList;
    LinearLayoutManager linearLayoutManager;
    List<SyncContactData> fullContactList;
    List<SyncContactData> contactListFilter;
    AdapterContactList mAdapterContactList;
    TextView tvSearchresult;
    LinearLayout llDefault;
    LinearLayout llInvite;
    TextView tvInviteMessage;
    TextView tvInviteButton;
    RecyclerView rvRecentContacts;
    GridLayoutManager recentLinearLayoutManager;
    LinearLayout llRecentContacts;
    LinearLayout llToolBarSearch;


    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    Context context;
    DialogProgress dialogProgress;
    String from;
    boolean getUserDetailsUsingMobileRetry=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_search);
        context= ContactSearchActivity.this;
        from=getIntent().getStringExtra("from");
        initViews();
        addTextListener();
        setOnClickListener();
    }

    private void setOnClickListener() {
        tvInviteButton.setOnClickListener(view -> {
            // String shareBody = "Hey, Yeel is a fast, simple, and secure mobile payment platform that we use to pay and send money from anywhere, anytime. Use this link to install: Yeel. https://play.google.com/store/apps/details?id=com.yeel.app";
            String shareBody = "Yeel South Sudan is a fast, simple, and secure digital banking platform that we use to pay and send money from anywhere, anytime. Use this link to install Yeel South Sudan:  https://play.google.com/store/apps/details?id=com.yeel.southsudan";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Yeel App");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "share"));
        });
        tvCancel.setOnClickListener(view -> finish());
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                try {
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            edtSearch.setText("");
                            return true;
                        }
                    }
                }catch (Exception e){

                }

                return false;
            }
        });
    }

    private void addTextListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                try{
                    if(fullContactList.size()!=0){
                        if(!query.toString().toLowerCase().equals("")){
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_search_clear, 0);
                            query = query.toString().toLowerCase();
                            contactListFilter=null;
                            contactListFilter = new ArrayList<>();
                            for (int i = 0; i < fullContactList.size(); i++) {
                                String name = fullContactList.get(i).getName().toLowerCase();
                                String phone = fullContactList.get(i).getPhone().toLowerCase();
                                if (name.contains(query)||phone.contains(query)) {
                                    contactListFilter.add(fullContactList.get(i));
                                }
                            }
                            if(contactListFilter!=null&&contactListFilter.size()!=0){
                                setContactList(contactListFilter);
                                llDefault.setVisibility(View.GONE);
                                tvSearchresult.setVisibility(View.VISIBLE);
                            }else{
                                String value=query.toString();
                                if(commonFunctions.validateMobileNumber(value)){
                                    if(value.equals(commonFunctions.getMobile())){
                                        errorDialog.show("You can not sent money to your account");
                                    }else {
                                        rvContactList.setVisibility(View.GONE);
                                        tvNoItem.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.VISIBLE);
                                        getUserDetailsUsingMobileAPI(value,"search",getString(R.string.country_code_for_api));
                                    }
                                }else{
                                    setNoItem();
                                }

                            }
                        }else{
                            edtSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            llDefault.setVisibility(View.VISIBLE);
                            tvSearchresult.setVisibility(View.GONE);
                            setContactList(fullContactList);
                        }
                    }else{
                        String value=query.toString();
                        if(commonFunctions.validateMobileNumber(value)){
                            if(value.equals(commonFunctions.getMobile())){
                                errorDialog.show("You can not sent money to your account");
                            }else {
                                rvContactList.setVisibility(View.GONE);
                                tvNoItem.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                getUserDetailsUsingMobileAPI(value,"search",getString(R.string.country_code_for_api));
                            }
                        }else{
                            setNoItem();
                        }
                    }
                }catch (Exception e){
                }
            }
        });
    }

    private void initViews() {
        dialogProgress = new DialogProgress(context);
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);

        progressBar=findViewById(R.id.progressBar);
        tvNoItem=findViewById(R.id.tv_no_item);
        tvNoItem.setVisibility(View.GONE);
        tvCancel=findViewById(R.id.tv_cancel);
        edtSearch=findViewById(R.id.edt_search);
        edtSearch.setEnabled(false);
        rvContactList=findViewById(R.id.rvContactList);
        linearLayoutManager=new LinearLayoutManager(ContactSearchActivity.this);
        rvContactList.setLayoutManager(linearLayoutManager);
        tvSearchresult=findViewById(R.id.tv_search_result);
        tvSearchresult.setVisibility(View.GONE);
        llDefault=findViewById(R.id.ll_default);
        llInvite=findViewById(R.id.ll_invite);
        llInvite.setVisibility(View.GONE);
        tvInviteMessage=findViewById(R.id.tv_invite_message);
        tvInviteButton=findViewById(R.id.tv_invite_button);
        rvRecentContacts=findViewById(R.id.rv_recent_contacts);
        recentLinearLayoutManager=new GridLayoutManager(ContactSearchActivity.this,4);
        rvRecentContacts.setLayoutManager(recentLinearLayoutManager);
        llRecentContacts=findViewById(R.id.ll_recent_contacts);
        llToolBarSearch=findViewById(R.id.ll_toolbar_search);
        if(!commonFunctions.getContactList().equals("")){
            String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getContactList());
            Gson gson = new Gson();
            SynContactResponse apiResponse=gson.fromJson(jsonString, SynContactResponse.class);
            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                fullContactList=apiResponse.getSyncContactList();
                if(fullContactList!=null&&fullContactList.size()!=0){
                    edtSearch.setEnabled(true);
                    llDefault.setVisibility(View.VISIBLE);
                    tvSearchresult.setVisibility(View.GONE);
                    setContactList(fullContactList);
                }else{
                    edtSearch.setEnabled(true);
                    setNoItem();
                }
            }
        }
    }


    private void setNoItem() {
        rvContactList.setVisibility(View.GONE);
        tvNoItem.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        llDefault.setVisibility(View.GONE);
        tvSearchresult.setVisibility(View.VISIBLE);

        String message="";
        if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
            String one="نحن لا نستطيعُ أَن نجدَ";
            String two="في إتصالاتِكَ. حاول ادخال رقم الهاتف المحمول.";

            message=one+" '"+edtSearch.getText().toString().trim()+"' "+two;
        }else if(SthiramValues.SELECTED_LANGUAGE_ID.equals("3")){
            message="Kişilerinizde '"+edtSearch.getText().toString().trim()+"' bulunamadı. Cep telefonu numarası girmeyi deneyin.";
        }else{
            message="We could not find '"+edtSearch.getText().toString().trim()+"' in your contacts. Try entering a mobile number";
        }
        tvNoItem.setText(message);
        llInvite.setVisibility(View.GONE);
    }

    private void setContactList(List<SyncContactData> list) {
        tvNoItem.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        rvContactList.setVisibility(View.VISIBLE);
        llInvite.setVisibility(View.GONE);
        mAdapterContactList = new AdapterContactList(ContactSearchActivity.this,list, new AdapterContactList.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                if(list.get(position).getYeel_status()==1){
                    if(!commonFunctions.getMobile().equals(list.get(position).getPhone())){
                       getUserDetailsUsingMobileAPI(list.get(position).getPhone(),"click",getString(R.string.country_code_for_api));
                    }else{
                        errorDialog.show("You can not sent money to your account");
                    }

                }else{
                    inviteDialog(list.get(position).getPhone(),list.get(position).getName());
                }
            }
            @Override
            public void itemMenu(View v, int position) {

            }
        });
        rvContactList.setAdapter(mAdapterContactList);
    }

    private void getUserDetailsUsingMobileAPI(String mobileNumber,String route,String countyCode) {
        boolean progress=true;
        if(route.equals("click")){
            progress=true;
        }else{
            progress=false;
        }
        apiCall.getUserDetailsUsingMobile(getUserDetailsUsingMobileRetry,mobileNumber,countyCode,progress, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        UserDetailsData userDetailsData=apiResponse.getUserDetails();
                        if(route.equals("click")){

                            List<CurrencyListData> currencyList=apiResponse.getCurrencyList();
                            boolean isAccountNumberAvailable = false;
                            String receiverAccountNumber="";

                            if(currencyList!=null){
                                //check currency available or not
                                for(int i=0;i<currencyList.size();i++){
                                    if(commonFunctions.getCurrencyID().equals(currencyList.get(i).getCurrency_id())){
                                        receiverAccountNumber=currencyList.get(i).getAccount_number();
                                        isAccountNumberAvailable=true;
                                        break;
                                    }
                                }

                                if(isAccountNumberAvailable){
                                    userDetailsData.setAccount_no(receiverAccountNumber);
                                    if(from.equals("pay")){
                                        Intent in = new Intent(context, AmountEnteringActivity.class);
                                        in.putExtra("data",userDetailsData);
                                        startActivity(in);
                                    }else if(from.equals("cashOut")){
                                        Intent intent= new Intent(context, CashOutAmountEnteringActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Intent in = new Intent(context, AmountEnteringActivity.class);
                                        in.putExtra("data",userDetailsData);
                                        startActivity(in);
                                    }

                                }else{
                                    String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                                    errorDialog.show(message);
                                }

                            }else{
                                String message=getString(R.string.receiver_not_matching_message)+ SthiramValues.SELECTED_CURRENCY_SYMBOL+getString(R.string.receiver_not_matching_message_2);
                                errorDialog.show(message);
                            }




                        }else{
                            contactListFilter=null;
                            contactListFilter = new ArrayList<>();
                            String fullName="";
                            if(userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
                                if(userDetailsData.getAgentType().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)){
                                    fullName=commonFunctions.generateFullName(userDetailsData.getFirstname(),userDetailsData.getMiddlename(),userDetailsData.getLastname());
                                }else{
                                    fullName=userDetailsData.getBusiness_name();
                                }
                            }else if(userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL)){
                                fullName=commonFunctions.generateFullName(userDetailsData.getFirstname(),userDetailsData.getMiddlename(),userDetailsData.getLastname());
                            }else if(userDetailsData.getUser_type().equals(SthiramValues.ACCOUNT_TYPE_BUSINESS)) {
                                fullName=userDetailsData.getBusiness_name();
                            }

                            SyncContactData syncContactData=new SyncContactData(mobileNumber,
                                    fullName,
                                    fullName,
                                    1,
                                    "",
                                    userDetailsData.getId(),
                                    "");

                            contactListFilter.add(syncContactData);
                            setContactList(contactListFilter);
                            llDefault.setVisibility(View.GONE);
                            tvSearchresult.setVisibility(View.VISIBLE);
                        }

                    } else {
                        String inviteMessage= "<font size='5' color='#5463E8'><b>"+countyCode+" "+mobileNumber+"</b></font> is not on the Yeel, Send invite to Yeel Sustenta App";
                        tvInviteMessage.setText(Html.fromHtml(inviteMessage));
                        llInvite.setVisibility(View.VISIBLE);
                        rvContactList.setVisibility(View.GONE);
                        tvNoItem.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        llDefault.setVisibility(View.GONE);
                        tvSearchresult.setVisibility(View.GONE);
                        commonFunctions.hideKeyboard(ContactSearchActivity.this);

                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getUserDetailsUsingMobileRetry=true;
                getUserDetailsUsingMobileAPI(mobileNumber,route,getString(R.string.country_code_for_api));
            }
        });
    }

    private void inviteDialog(String phone,String name) {
        final Dialog success = new Dialog(this);
        success.requestWindowFeature(Window.FEATURE_NO_TITLE);
        success.setCancelable(false);
        success.setContentView(R.layout.invite_dialoge_box);
        TextView tvHeading=success.findViewById(R.id.tv_heading);
        TextView tvFirstLetter=success.findViewById(R.id.tv_first_letter);
        TextView tvName=success.findViewById(R.id.tv_name);
        TextView tvMobileNumber=success.findViewById(R.id.tv_mobile_number);
        TextView tvCancel=success.findViewById(R.id.tv_cancel);
        TextView tvInvite=success.findViewById(R.id.tv_invite);
        String heading="Your contact <font color='#5463E8'><b>"+name+"</b> </font> is not on the Yeel, Send invite to Yeel";
        tvHeading.setText(Html.fromHtml(heading));
        tvMobileNumber.setText(commonFunctions.formatAMobileNumber(phone, SthiramValues.DEFAULT_COUNTY_MOBILE_CODE, SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT));
        tvName.setText(name);
        String firstLetter = String.valueOf(name.charAt(0));
        tvFirstLetter.setText(firstLetter);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success.dismiss();
            }
        });
        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                success.dismiss();
                String shareBody = "Hey, Yeel has made payments easy for me. You should try it too. It’s instant & 100% safe. You can send and receive money instantly. Try now. \n" +
                        "\n" +
                        "Click here & install Yeel - https://play.google.com/store/apps/details?id=com.yeel.digitalbank";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Yeel SouthSudan App");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "share"));
            }
        });

        success.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        success.setCanceledOnTouchOutside(false);
        success.show();
    }
}