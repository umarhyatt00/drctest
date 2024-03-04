package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.APICall;
import com.yeel.drc.api.fullagentlist.AgentData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> {
    List<AgentData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;
    CommonFunctions commonFunctions;
    APICall apiCall;

    public AgentListAdapter(List<AgentData> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
        commonFunctions=new CommonFunctions(context);
        apiCall=new APICall(context, SthiramValues.dismiss);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_list_item_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String agentName=list.get(position).getNameToShow();
        String address = "";
        boolean addressAvailable = false;
        addressAvailable = true;
        if (list.get(position).getAgent_type().equals(SthiramValues.ACCOUNT_TYPE_INDIVIDUAL_AGENT)) {
            address = list.get(position).getAddressline1() + "\n" + list.get(position).getLocality() + ", " + list.get(position).getProvince() + ", " + list.get(position).getDistrict();
        } else {
            address = list.get(position).getLocality() + ", " + list.get(position).getProvince() + ", " + list.get(position).getDistrict();

        }
        holder.tvName.setText(agentName);


        String mobileNumberFormat=apiCall.getCountyCodeFormatFromCountyList(commonFunctions.getCountryLists(),list.get(position).getCountry_code());
        String mobileNumber=commonFunctions.formatAMobileNumber(list.get(position).getMobile(),list.get(position).getCountry_code(),mobileNumberFormat);


        if (addressAvailable) {
            holder.tvLocation.setText(address + "\n" + mobileNumber);
        } else {
            holder.tvLocation.setText(mobileNumber);
        }



        if (list.get(position).getAgent_code() != null && !list.get(position).getAgent_code().equals("")) {
            holder.tvAgentCode.setText("Agent Code: " + list.get(position).getAgent_code());
            holder.tvAgentCode.setVisibility(View.VISIBLE);
        } else {
            holder.tvAgentCode.setVisibility(View.GONE);
        }


        holder.rlMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.itemClick(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llMain;
        TextView tvName;
        TextView tvAgentCode;
        TextView tvLocation;
        RelativeLayout rlMenu;
        public ViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            tvName=itemView.findViewById(R.id.tv_name);
            tvAgentCode=itemView.findViewById(R.id.tv_agent_code);
            tvLocation=itemView.findViewById(R.id.tv_location);
            rlMenu=itemView.findViewById(R.id.menu);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }
}
