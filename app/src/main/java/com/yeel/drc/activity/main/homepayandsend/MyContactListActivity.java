package com.yeel.drc.activity.main.homepayandsend;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.cashout.CashOutAmountEnteringActivity;
import com.yeel.drc.adapter.contacts.AdapterContactList;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.APIInterface;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import com.yeel.drc.api.synccontact.SyncContactData;
import com.yeel.drc.api.synccontact.SynContactResponse;
import com.yeel.drc.api.userdetails.GetUserDetailsResponse;
import com.yeel.drc.api.userdetails.UserDetailsData;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.phonecontacts.ContactModel;
import com.yeel.drc.model.phonecontacts.ContactPostModel;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SthiramValues;
import com.yeel.drc.utils.DialogProgress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MyContactListActivity extends BaseActivity {
    Context context;
    String permissionContact = Manifest.permission.READ_CONTACTS;
    private ProgressBar mProgressBar;
    private TextView mTvNoItem;
    private RecyclerView mRecyclerContact;
    RecyclerView rvRecentContacts;
    private LinearLayoutManager linearLayoutManager;
    private AdapterContactList mAdapterContactList;
    private List<ContactModel> list;
    List<SyncContactData> syncContactList;
    DialogProgress dialogProgress;
    List<String> mobileNumberList;
    LinearLayout llDefault;
    LinearLayout llRecentContacts;
    LinearLayout llMain;
    LinearLayout llNoPermission;
    TextView tvSettings;
    GridLayoutManager recentLinearLayoutManager;
    FetchContactsAsyncTask fetchContactsAsyncTask;
    FloatingActionButton fab;
    Boolean recentContactRetry=false;
    String from;
    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;
    boolean getUserDetailsUsingMobileTrue=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact_listing);
        setToolBar();
        from=getIntent().getStringExtra("from");
        context= MyContactListActivity.this;
        initView();
        setItemListeners();
    }
    private void setItemListeners() {
        tvSettings.setOnClickListener(view -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 103);
        });
        fab.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_INSERT);
            i.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(i, 125);
        });
    }

    private void initView() {
        dialogProgress = new DialogProgress(context);
        recentContactRetry=false;
        errorDialog = new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog = new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall = new APICall(context, SthiramValues.dismiss);
        tvSettings=findViewById(R.id.tv_settings);
        llMain=findViewById(R.id.ll_main);
        llNoPermission=findViewById(R.id.ll_no_permission);
        mProgressBar = findViewById(R.id.progressBar);
        mTvNoItem = findViewById(R.id.tv_no_item);
        mTvNoItem.setVisibility(View.GONE);
        mRecyclerContact = findViewById(R.id.rvContactList);
        linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerContact.setLayoutManager(linearLayoutManager);
        rvRecentContacts=findViewById(R.id.rv_recent_contacts);
        recentLinearLayoutManager=new GridLayoutManager(MyContactListActivity.this,4);
        rvRecentContacts.setLayoutManager(recentLinearLayoutManager);
        llDefault=findViewById(R.id.ll_default);
        llRecentContacts=findViewById(R.id.ll_recent_contacts);
        llDefault.setVisibility(View.GONE);
        fab=findViewById(R.id.fab);

        if(checkContactPermission()){
            setContact();
        }else{
            llMain.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mTvNoItem.setVisibility(View.GONE);
            llNoPermission.setVisibility(View.VISIBLE);
        }
    }

    private void setContact() {
        llNoPermission.setVisibility(View.GONE);
        llMain.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);

        if(!commonFunctions.getContactList().equals("")){
            try {
                String jsonString=apiCall.getJsonFromEncryptedString(commonFunctions.getContactList());
                Gson gson = new Gson();
                SynContactResponse apiResponse=gson.fromJson(jsonString, SynContactResponse.class);
                if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                    syncContactList=apiResponse.getSyncContactList();
                    if(syncContactList!=null&&syncContactList.size()!=0){
                        setContactList();
                    }else{
                        mTvNoItem.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }catch (Exception e){

            }

        }

        Date d = new Date();
        CharSequence todayDate  = DateFormat.format("MM/dd/yyyy", d.getTime());
        if(commonFunctions.lastSyncDateStatus(todayDate.toString())){
            commonFunctions.logAValue("Fetching Contact","Yes");
            fetchContactsAsyncTask=new FetchContactsAsyncTask();
            fetchContactsAsyncTask.execute();
        }else{
            commonFunctions.logAValue("Fetching Contact","No");
        }
    }


    class FetchContactsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @SuppressLint("Range")
        protected String doInBackground(String... urls) {
            try {
                list = new ArrayList<>();
                mobileNumberList=new ArrayList<>();
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            while (cursorInfo.moveToNext()) {
                                ContactModel info = new ContactModel();
                                String fullMobileNumber=cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                fullMobileNumber=fullMobileNumber.replaceAll("\\D","");
                                if(fullMobileNumber.length()== SthiramValues.MOBILE_NUMBER_DIGIT){
                                   /* if (fullMobileNumber.length() == Constants.MOBILE_NUMBER_DIGIT) {
                                        info.phone=fullMobileNumber;
                                    }else{
                                        info.phone= fullMobileNumber.substring(fullMobileNumber.length() -Constants.MOBILE_NUMBER_DIGIT);
                                    }*/
                                    info.phone=fullMobileNumber;
                                    info.name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    if(!mobileNumberList.contains(info.phone)){
                                        mobileNumberList.add(info.phone);
                                        list.add(info);
                                    }
                                }
                            }
                            cursorInfo.close();
                        }
                    }
                    cursor.close();
                }
                return "";
            } catch (Exception e) {
                Toast.makeText(context,e+"",Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        protected void onPostExecute(String feed) {
            if(list!=null){
                syncContactListAPI(list);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }



    private void syncContactListAPI(List<ContactModel> contacts) {
        try{
            dialogProgress.show();
            Retrofit retrofit = apiCall.createRetrofitObjectForSyncContact(commonFunctions.getUserId(), SthiramValues.USER_URL);
            ContactPostModel contactPostModel=new ContactPostModel(commonFunctions.getUserId(),System.currentTimeMillis()+"",contacts);
            retrofit.create(APIInterface.class).syncContact(contactPostModel).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, retrofit2.Response<CommonResponse> response) {
                    if (response.isSuccessful()) {
                        dialogProgress.dismiss();
                        try {
                            commonFunctions.setContactList(response.body().getKuttoosan());
                            String jsonString = apiCall.getJsonFromEncryptedString(response.body().getKuttoosan());
                            Gson gson = new Gson();
                            SynContactResponse apiResponse=gson.fromJson(jsonString, SynContactResponse.class);

                            if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                                if(apiResponse.getSyncContactList()!=null&&apiResponse.getSyncContactList().size()!=0){
                                    Date d = new Date();
                                    CharSequence todayDate  = DateFormat.format("MM/dd/yyyy", d.getTime());
                                    commonFunctions.setLastContactSyncDate(todayDate.toString());
                                    syncContactList=apiResponse.getSyncContactList();
                                    setContactList();
                                }else{
                                    mTvNoItem.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }else{
                                errorDialog.show(apiResponse.getMessage());
                            }
                        }catch (Exception e){

                        }

                    } else {
                        dialogProgress.dismiss();
                        if (!somethingWentWrongDialog.isShowing()) {
                            somethingWentWrongDialog.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    dialogProgress.dismiss();
                    if (!somethingWentWrongDialog.isShowing()) {
                        somethingWentWrongDialog.show();
                    }
                }
            });
        }catch (Exception e){
            dialogProgress.dismiss();
            Toast.makeText(context,e+"",Toast.LENGTH_SHORT).show();
        }


    }



    private void setContactList() {
        mTvNoItem.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mAdapterContactList = new AdapterContactList(MyContactListActivity.this,syncContactList, new AdapterContactList.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                if(syncContactList.get(position).getYeel_status()==1){
                    if(!commonFunctions.getMobile().equals(syncContactList.get(position).getPhone())){
                       getUserDetailsUsingMobileAPI(syncContactList.get(position).getPhone(),getString(R.string.country_code_for_api));
                    }else{
                        errorDialog.show("You can not sent money to your account");
                    }
                }else{
                   inviteDialog(syncContactList.get(position).getPhone(),syncContactList.get(position).getName());
                }

            }

            @Override
            public void itemMenu(View v, int position) {

            }
        });
        mRecyclerContact.setAdapter(mAdapterContactList);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(fetchContactsAsyncTask!=null){
            fetchContactsAsyncTask.cancel(true);
        }
    }


    private boolean checkContactPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permissionContact)!= PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }
    }

    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow_white);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.my_contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_with_search_refresh, menu);
        return true;
    }

    //back button click
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.id_search:
                if(checkContactPermission()){
                    Intent intent = new Intent(MyContactListActivity.this, ContactSearchActivity.class);
                    intent.putExtra("from",from);
                    startActivity(intent);
                }else{
                    Toast.makeText(MyContactListActivity.this,"Allow your contact permissions to continue",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.id_refresh:
                if(checkContactPermission()){
                    dialogProgress.show();
                    fetchContactsAsyncTask=new FetchContactsAsyncTask();
                    fetchContactsAsyncTask.execute();
                }else{
                    Toast.makeText(MyContactListActivity.this,"Allow your contact permissions to continue",Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }


    private void getUserDetailsUsingMobileAPI(String mobileNumber,String countyCode) {
        apiCall.getUserDetailsUsingMobile(getUserDetailsUsingMobileTrue,mobileNumber,countyCode,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try {
                    String jsonString = apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    GetUserDetailsResponse apiResponse = gson.fromJson(jsonString, GetUserDetailsResponse.class);
                    if (apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {

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
                                UserDetailsData userDetailsData=apiResponse.getUserDetails();
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


                    } else {
                        Intent intent = new Intent(getApplicationContext(), InviteUserActivity.class);
                        intent.putExtra("mobile", mobileNumber);
                        intent.putExtra("country_code", SthiramValues.DEFAULT_COUNTY_MOBILE_CODE);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                getUserDetailsUsingMobileTrue=true;
                getUserDetailsUsingMobileAPI(mobileNumber,countyCode);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 103) {
                setContact();
            }
        //}
    }
}

