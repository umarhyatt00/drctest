package com.yeel.drc.activity.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.activity.main.fragments.agent.AgentAccountFragment;
import com.yeel.drc.activity.main.fragments.agent.AgentCashPickupFragment;
import com.yeel.drc.activity.main.fragments.user.AccountFragment;
import com.yeel.drc.activity.main.fragments.user.HomeFragment;
import com.yeel.drc.activity.main.fragments.ReportFragment;
import com.yeel.drc.activity.main.fragments.user.TransactionFragment;
import com.yeel.drc.activity.main.fragments.agent.AgentCommissionFragment;
import com.yeel.drc.activity.main.fragments.agent.AgentHomeFragment;
import com.yeel.drc.activity.main.fragments.agent.AgentTransactionFragment;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.countylist.CountryListResponse;
import com.yeel.drc.api.forceupdate.ForceUpdateData;
import com.yeel.drc.api.forceupdate.ForceUpdateResponse;
import com.yeel.drc.dialogboxes.ForceUpdateDialog;
import com.yeel.drc.dialogboxes.ForceUpdateNotMandatoryDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.timeout.BaseActivity;
import com.yeel.drc.utils.SharedPrefUtil;
import com.yeel.drc.utils.SthiramValues;
public class HomeActivity extends BaseActivity {
    public  static AHBottomNavigation bottomNavigation;
    Context con;
    int selectedUserTab=0;
    int selectedAgentTab=0;
    Fragment homeFragment;
    public  static  boolean isFromNotification=false;
    LinearLayout llBottom;
    LinearLayout llHome;
    LinearLayout llCashPickup;
    LinearLayout llAccount;
    LinearLayout llTransactions;
    LinearLayout llReport;
    ImageView ivHome;
    ImageView ivCashPickup;
    ImageView ivAccount;
    ImageView ivTransactions;
    ImageView ivReport;
    ImageView ivNotificationIndication;
    TextView tvHome;
    TextView tvCashPickup;
    TextView tvAccount;
    TextView tvTransactions;
    TextView tvReport;
    APICall apiCallTwo;
    boolean forceUpdateAPIRetry=false;
    boolean countryListRetry=false;
    SomethingWentWrongDialog somethingWentWrongDialogTwo;
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        con= HomeActivity.this;
        isFromNotification=getIntent().getBooleanExtra("isFromNotification",false);
        sharedPrefUtil = new SharedPrefUtil(con);
        initView();
    }

    private void initView() {
        apiCallTwo=new APICall(con, SthiramValues.finish);
        somethingWentWrongDialogTwo=new SomethingWentWrongDialog(con, SthiramValues.finish);
        ivNotificationIndication=findViewById(R.id.tv_notification_indication);
        ivNotificationIndication.setVisibility(View.GONE);

        llBottom=findViewById(R.id.ll_bottom);
        llBottom.setVisibility(View.GONE);

        llHome=findViewById(R.id.ll_home);
        ivHome=findViewById(R.id.iv_home);
        tvHome=findViewById(R.id.tv_home);

        llCashPickup=findViewById(R.id.ll_cash_pickup);
        ivCashPickup=findViewById(R.id.iv_cash_pickup);
        tvCashPickup=findViewById(R.id.tv_cash_pickup);

        llAccount=findViewById(R.id.ll_account);
        ivAccount=findViewById(R.id.iv_account);
        tvAccount=findViewById(R.id.tv_account);

        llTransactions=findViewById(R.id.ll_transactions);
        ivTransactions=findViewById(R.id.iv_transactions);
        tvTransactions=findViewById(R.id.tv_transactions);

        llReport=findViewById(R.id.ll_report);
        ivReport=findViewById(R.id.iv_report);
        tvReport=findViewById(R.id.tv_report);


        bottomNavigation =findViewById(R.id.bottom_navigation);
        if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
            setAgentUser();
        }else{
            setNormalUser();
        }
        callForceUpdateAPI();
        getCountyList();
    }


    private void setNotificationIcon() {
        if(commonFunctions.getAgentNotificationStatus()&&selectedAgentTab!=1){
            ivNotificationIndication.setVisibility(View.VISIBLE);
        }else{
            commonFunctions.setAgentNotificationStatus(false);
            ivNotificationIndication.setVisibility(View.INVISIBLE);
        }
    }

    private void setAgentUser() {
        llBottom.setVisibility(View.VISIBLE);
        bottomNavigation.setVisibility(View.INVISIBLE);

        llHome.setOnClickListener(view -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(selectedAgentTab!=0){
                    openAgentHomeFragment();
                    setHome();
                    selectedAgentTab=0;
                }
            }
        });

        llCashPickup.setOnClickListener(view -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(selectedAgentTab!=1){
                    openAgentCashPickupFragment();
                    setCashPickup();
                    selectedAgentTab=1;
                }
            }
        });

        llAccount.setOnClickListener(view -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(selectedAgentTab!=2){
                    openAgentAccountFragment();
                    setAccount();
                    selectedAgentTab=2;
                }
            }
        });

        llTransactions.setOnClickListener(view -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(selectedAgentTab!=3){
                    openAgentTransactionFragment();
                    setTransactions();
                    selectedAgentTab=3;
                }
            }
        });

        llReport.setOnClickListener(view -> {
            if(!commonFunctions.getBasicDetails().equals("")){
                if(selectedAgentTab!=4){
                    openReportFragment();
                    setReport();
                    selectedAgentTab=4;
                }
            }
        });
        selectedAgentTab=0;
        openAgentHomeFragment();
        setHome();
    }

    private void setReport() {
        ivHome.setImageResource(R.drawable.ic_bottom_home);
        ivCashPickup.setImageResource(R.drawable.ic_bottom_cash_pickup);
        ivAccount.setImageResource(R.drawable.ic_bottom_account);
        ivTransactions.setImageResource(R.drawable.ic_bottom_transactions);
        ivReport.setImageResource(R.drawable.ic_bottom_report_selected);

        tvHome.setTextColor(Color.parseColor("#8C98B1"));
        tvCashPickup.setTextColor(Color.parseColor("#8C98B1"));
        tvAccount.setTextColor(Color.parseColor("#8C98B1"));
        tvTransactions.setTextColor(Color.parseColor("#8C98B1"));
        tvReport.setTextColor(Color.parseColor("#5463E8"));
    }

    private void setTransactions() {
        ivHome.setImageResource(R.drawable.ic_bottom_home);
        ivCashPickup.setImageResource(R.drawable.ic_bottom_cash_pickup);
        ivAccount.setImageResource(R.drawable.ic_bottom_account);
        ivTransactions.setImageResource(R.drawable.ic_bottom_transactions_selected);
        ivReport.setImageResource(R.drawable.ic_bottom_report);

        tvHome.setTextColor(Color.parseColor("#8C98B1"));
        tvCashPickup.setTextColor(Color.parseColor("#8C98B1"));
        tvAccount.setTextColor(Color.parseColor("#8C98B1"));
        tvTransactions.setTextColor(Color.parseColor("#5463E8"));
        tvReport.setTextColor(Color.parseColor("#8C98B1"));
    }

    private void setAccount() {
        ivHome.setImageResource(R.drawable.ic_bottom_home);
        ivCashPickup.setImageResource(R.drawable.ic_bottom_cash_pickup);
        ivAccount.setImageResource(R.drawable.ic_bottom_account_selected);
        ivTransactions.setImageResource(R.drawable.ic_bottom_transactions);
        ivReport.setImageResource(R.drawable.ic_bottom_report);

        tvHome.setTextColor(Color.parseColor("#8C98B1"));
        tvCashPickup.setTextColor(Color.parseColor("#8C98B1"));
        tvAccount.setTextColor(Color.parseColor("#5463E8"));
        tvTransactions.setTextColor(Color.parseColor("#8C98B1"));
        tvReport.setTextColor(Color.parseColor("#8C98B1"));
    }

    private void setCashPickup() {
        ivHome.setImageResource(R.drawable.ic_bottom_home);
        ivCashPickup.setImageResource(R.drawable.ic_bottom_cash_pickup_selected);
        ivAccount.setImageResource(R.drawable.ic_bottom_account);
        ivTransactions.setImageResource(R.drawable.ic_bottom_transactions);
        ivReport.setImageResource(R.drawable.ic_bottom_report);

        tvHome.setTextColor(Color.parseColor("#8C98B1"));
        tvCashPickup.setTextColor(Color.parseColor("#5463E8"));
        tvAccount.setTextColor(Color.parseColor("#8C98B1"));
        tvTransactions.setTextColor(Color.parseColor("#8C98B1"));
        tvReport.setTextColor(Color.parseColor("#8C98B1"));

        commonFunctions.setAgentNotificationStatus(false);
        setNotificationIcon();
    }

    private void setHome() {
        ivHome.setImageResource(R.drawable.ic_bottom_home_selected);
        ivCashPickup.setImageResource(R.drawable.ic_bottom_cash_pickup);
        ivAccount.setImageResource(R.drawable.ic_bottom_account);
        ivTransactions.setImageResource(R.drawable.ic_bottom_transactions);
        ivReport.setImageResource(R.drawable.ic_bottom_report);

        tvHome.setTextColor(Color.parseColor("#5463E8"));
        tvCashPickup.setTextColor(Color.parseColor("#8C98B1"));
        tvAccount.setTextColor(Color.parseColor("#8C98B1"));
        tvTransactions.setTextColor(Color.parseColor("#8C98B1"));
        tvReport.setTextColor(Color.parseColor("#8C98B1"));
    }

    private void setNormalUser() {
        llBottom.setVisibility(View.GONE);
        bottomNavigation.setVisibility(View.VISIBLE);
        final AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.home), R.drawable.ic_home, R.color.colorPrimaryDark);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.account), R.drawable.ic_home_account, R.color.colorPrimaryDark);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.transactions), R.drawable.ic_home_transactions, R.color.colorPrimaryDark);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.report), R.drawable.ic_home_statement, R.color.colorPrimaryDark);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#ffffff"));
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setTitleTextSizeInSp(10,10);
        bottomNavigation.setAccentColor(Color.parseColor("#5463E8"));
        bottomNavigation.setInactiveColor(Color.parseColor("#A1ACC8"));


        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        if(!commonFunctions.getBasicDetails().equals("")){
                            if(selectedUserTab!=0){
                                selectedUserTab=0;
                                openNewMainHomeFragment();
                            }
                        }
                        return true;
                    case 1:
                        if(!commonFunctions.getBasicDetails().equals("")){
                            if(selectedUserTab!=1){
                                selectedUserTab=1;
                                openAccountFragment();
                            }
                        }
                        return true;
                    case 2:
                        if(!commonFunctions.getBasicDetails().equals("")){
                            if(selectedUserTab!=2){
                                selectedUserTab=2;
                                openTransactionFragment();
                            }
                        }
                        return true;
                    case 3:
                        if(!commonFunctions.getBasicDetails().equals("")){
                            if(selectedUserTab!=3){
                                selectedUserTab=3;
                                openReportFragment();
                            }
                        }
                        return true;
                }
                return false;
            }
        });
        selectedUserTab=0;
        openNewMainHomeFragment();
    }



    private void openNewMainHomeFragment() {
        homeFragment= new HomeFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openAgentCashPickupFragment() {
        Fragment fragment;
        fragment= new AgentCashPickupFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void openAgentAccountFragment() {
        Fragment fragment;
        fragment= new AgentAccountFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void openAccountFragment() {
        Fragment fragment;
        fragment= new AccountFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openTransactionFragment() {
        Fragment fragment;
        fragment= new TransactionFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openReportFragment() {
        Fragment fragment;
        fragment= new ReportFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openAgentHomeFragment() {
        Fragment fragment;
        fragment= new AgentHomeFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openAgentTransactionFragment() {
        Fragment fragment;
        fragment= new AgentTransactionFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void openAgentCommissionFragment() {
        Fragment fragment;
        fragment= new AgentCommissionFragment();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        commonFunctions.setLogoutStatus(true);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(con).registerReceiver(mMessageReceiver, new IntentFilter("notification"));
        if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)){
            setNotificationIcon();
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(con).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setNotificationIcon();
        }
    };



    private void callForceUpdateAPI() {
        apiCallTwo.forceUpdate(forceUpdateAPIRetry,false, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCallTwo.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ForceUpdateResponse apiResponse = gson.fromJson(jsonString, ForceUpdateResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        //check force Update
                        PackageInfo pInfo = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        ForceUpdateData forceUpdateData = apiResponse.getData();
                        boolean updateAvailable = commonFunctions.checkUpdateAvailable(forceUpdateData, pInfo.versionCode);
                        if (updateAvailable) {
                            if (forceUpdateData.getForceUpdate().equalsIgnoreCase("y")) {
                                ForceUpdateDialog forceUpdateDialog=new ForceUpdateDialog(con, SthiramValues.finish);
                                forceUpdateDialog.show(getString(R.string.force_update));
                            } else {
                                ForceUpdateNotMandatoryDialog forceUpdateNotMandatoryDialog=new ForceUpdateNotMandatoryDialog(con);
                                forceUpdateNotMandatoryDialog.show();
                            }
                        }


                    }else{
                        if(!somethingWentWrongDialogTwo.isShowing()){
                            somethingWentWrongDialogTwo.show();
                        }
                    }

                }catch (Exception e){
                    commonFunctions.logAValue("Error",e+"");
                    if(!somethingWentWrongDialogTwo.isShowing()){
                        somethingWentWrongDialogTwo.show();
                    }
                }
            }
            @Override
            public void retry() {
                forceUpdateAPIRetry=true;
                callForceUpdateAPI();
            }
        });
    }


    private void getCountyList() {
        if(!commonFunctions.getCountryLists().equals("")){
            String jsonString=apiCallTwo.getJsonFromEncryptedString(commonFunctions.getCountryLists());
            Gson gson = new Gson();
            CountryListResponse apiResponse=gson.fromJson(jsonString, CountryListResponse.class);
            if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){

            }
        }
        apiCallTwo.getCountyList(countryListRetry,false,"onboarding", new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    commonFunctions.setCountryLists(response.getKuttoosan());
                    String jsonString=apiCallTwo.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    CountryListResponse apiResponse=gson.fromJson(jsonString, CountryListResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)){

                    }else{

                    }
                }catch (Exception e){

                }
            }
            @Override
            public void retry() {
                countryListRetry =true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

