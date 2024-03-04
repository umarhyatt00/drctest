package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.model.beneficiary.NonYeeluserListItem;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class BeneficiaryListAdapter extends ArrayAdapter<NonYeeluserListItem> {
    private Context context;
    CommonFunctions commonFunctions;
    List<NonYeeluserListItem> beneficiary, tempBeneficiary, filteredBeneficiary;
    APICall apiCall;

    public BeneficiaryListAdapter(Context context, List<NonYeeluserListItem> beneficiary) {
        super(context, R.layout.autocomplete_beneficiary_layout, beneficiary);
        this.context = context;
        this.beneficiary = beneficiary;
        tempBeneficiary = new ArrayList<>(beneficiary);
        filteredBeneficiary = new ArrayList<>();
        commonFunctions = new CommonFunctions(context);
        apiCall=new APICall(context, SthiramValues.dismiss);
    }

    @Override
    public int getCount() {
        return filteredBeneficiary.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.autocomplete_beneficiary_layout, parent, false);
            viewHolder=new ViewHolder(view);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }


      /*  String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),beneficiaryList.getCountryCode());
        textMobile.setText(commonFunctions.formatAMobileNumberWithOutCountyCode(beneficiaryList.getMobile(),mobileNumberFormat));*/

        NonYeeluserListItem beneficiaryList = filteredBeneficiary.get(position);
        viewHolder.textMobile.setText(beneficiaryList.getMobile());
        viewHolder.textName.setText(commonFunctions.generateFullName(beneficiaryList.getFirstname(), beneficiaryList.getMiddlename(), beneficiaryList.getLastname()));
        if (position == filteredBeneficiary.size() - 1) {
            viewHolder.viewUnderLine.setVisibility(View.GONE);
        } else {
            viewHolder.viewUnderLine.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public  static class ViewHolder{
        TextView textName;
        TextView textMobile;
        View viewUnderLine;
        public ViewHolder(View view){
            textName = (TextView) view.findViewById(R.id.text_name);
            textMobile = (TextView) view.findViewById(R.id.text_mobile);
            viewUnderLine = (View) view.findViewById(R.id.view_under_line);
        }


    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((NonYeeluserListItem) resultValue).getMobile();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint != null) {
                filteredBeneficiary.clear();
                for (NonYeeluserListItem names : tempBeneficiary) {
                    if (names.getMobile().toLowerCase().trim().contains(constraint.toString().toLowerCase().trim().replace(" ", ""))) {
                        filteredBeneficiary.add(names);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredBeneficiary;
                filterResults.count = filteredBeneficiary.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<NonYeeluserListItem> filterList = (ArrayList<NonYeeluserListItem>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (NonYeeluserListItem names : filterList) {
                    add(names);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
