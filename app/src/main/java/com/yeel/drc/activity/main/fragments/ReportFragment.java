package com.yeel.drc.activity.main.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yeel.drc.R;
import com.yeel.drc.adapter.MonthListAdapter;
import com.yeel.drc.adapter.YearListAdapter;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.CommonResponse;
import com.yeel.drc.api.CustomCallback;
import com.yeel.drc.api.report.ReportResponse;
import com.yeel.drc.dialogboxes.ErrorDialog;
import com.yeel.drc.dialogboxes.SomethingWentWrongDialog;
import com.yeel.drc.model.MonthModelModel;
import com.yeel.drc.model.YearModelModel;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.Calendar;

public class ReportFragment extends Fragment {

    View view;
    Context context;
    private RecyclerView mRecyclerViewMonth,mRecyclerViewYear;
    private GridLayoutManager mLinearLayoutManagerMonth,mLinearLayoutManagerYear;
    private ArrayList<MonthModelModel> monthList;
    private MonthListAdapter monthListAdapter;
    private ArrayList<YearModelModel> yearList;
    private YearListAdapter yearListAdapter;
    TextView tvDownload;
    CommonFunctions commonFunctions;
    boolean reportRetry=false;

    private String selectedMonth;
    private String selectedYear;


    APICall apiCall;
    ErrorDialog errorDialog;
    SomethingWentWrongDialog somethingWentWrongDialog;

    public ReportFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_report, container, false);
        context=getContext();
        initView();
        setItemListeners();
        return view;
    }

    private void initView() {
        errorDialog=new ErrorDialog(context, SthiramValues.dismiss);
        somethingWentWrongDialog=new SomethingWentWrongDialog(context, SthiramValues.dismiss);
        apiCall=new APICall(context, SthiramValues.dismiss);
        commonFunctions=new CommonFunctions(context);

        mRecyclerViewMonth=view.findViewById(R.id.rv_month);
        mRecyclerViewYear=view.findViewById(R.id.rv_year);
        mLinearLayoutManagerMonth = new GridLayoutManager(getActivity(), 4);
        mLinearLayoutManagerYear = new GridLayoutManager(getActivity(), 4);
        mRecyclerViewMonth.setLayoutManager(mLinearLayoutManagerMonth);
        mRecyclerViewYear.setLayoutManager(mLinearLayoutManagerYear);
        tvDownload=view.findViewById(R.id.btn_download_statement);
        setMonthList();
        setYearList();
    }

    private void setItemListeners() {
        tvDownload.setOnClickListener(view -> {
            downloadReport();
        });
    }

    private void downloadReport() {
        apiCall.getReport(reportRetry,commonFunctions.getUserId(),commonFunctions.getUserAccountNumber(),selectedMonth,selectedYear,true, new CustomCallback() {
            @Override
            public void success(CommonResponse response) {
                try{
                    String jsonString=apiCall.getJsonFromEncryptedString(response.getKuttoosan());
                    Gson gson = new Gson();
                    ReportResponse apiResponse=gson.fromJson(jsonString, ReportResponse.class);
                    if(apiResponse.getStatus().equalsIgnoreCase(SthiramValues.SUCCESS)) {
                        try {
                          /*  Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(apiResponse.getResults()));
                            startActivity(i);*/

                            String url=apiResponse.getResults();
                            //String url=apiResponse.getResults().replaceAll("southsudan","ysspdo");
                            Uri location = Uri.parse(url);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                            startActivity(mapIntent);
                        }catch (ActivityNotFoundException e){
                            if(!somethingWentWrongDialog.isShowing()){
                                somethingWentWrongDialog.show();
                            }
                        }
                    }else{
                        errorDialog.show(apiResponse.getMessage());
                    }
                }catch (Exception e){
                    if(!somethingWentWrongDialog.isShowing()){
                        somethingWentWrongDialog.show();
                    }
                }
            }
            @Override
            public void retry() {
                reportRetry=true;
                downloadReport();
            }
        });
    }

    private void setMonthList() {
        monthList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        String jan=getString(R.string.january);
        String feb=getString(R.string.february);
        String mar=getString(R.string.march);
        String apr=getString(R.string.april);
        String may=getString(R.string.may);
        String jun=getString(R.string.june);
        String jul=getString(R.string.july);
        String aug=getString(R.string.august);
        String sep=getString(R.string.september);
        String oct=getString(R.string.october);
        String nov=getString(R.string.november);
        String dec=getString(R.string.december);
        String[] monthArray = {jan, feb, mar, apr,may,jun,jul,aug,sep,oct,nov,dec};
        for (int i=0;i<monthArray.length;i++){
            if(month==i){
                selectedMonth=i+1+"";
                monthList.add(new MonthModelModel(monthArray[i],true,i+1+""));
            }else {
                monthList.add(new MonthModelModel(monthArray[i],false,i+1+""));
            }
        }

        monthListAdapter=new MonthListAdapter(monthList, getActivity(), new MonthListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                for(int i=0;i<monthList.size();i++){
                    if(i==position){
                        selectedMonth=monthList.get(i).getValue();
                        monthList.get(i).setSelected(true);
                    }else{
                        monthList.get(i).setSelected(false);
                    }
                }
                monthListAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewMonth.setAdapter(monthListAdapter);
    }
    private void setYearList() {
       int currentYear= Calendar.getInstance().get(Calendar.YEAR);
        yearList = new ArrayList<>();
        if(currentYear==2022){
            yearList.add(new YearModelModel(currentYear+"",true));
        }else{
            yearList.add(new YearModelModel(currentYear-1+"",false));
            yearList.add(new YearModelModel(currentYear+"",true));
        }
        selectedYear=currentYear+"";
        yearListAdapter=new YearListAdapter(yearList, getActivity(), new YearListAdapter.ItemClickAdapterListener() {
            @Override
            public void itemClick(View v, int position) {
                for(int i=0;i<yearList.size();i++){
                    if(i==position){
                        selectedYear=yearList.get(i).getYearName();
                        yearList.get(i).setSelected(true);
                    }else{
                        yearList.get(i).setSelected(false);
                    }
                }
                yearListAdapter.notifyDataSetChanged();
            }
        });
        mRecyclerViewYear.setAdapter(yearListAdapter);
    }

}