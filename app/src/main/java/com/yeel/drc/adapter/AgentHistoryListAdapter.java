package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.agentaddfundhistroy.AddFundHistoryListData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class AgentHistoryListAdapter extends RecyclerView.Adapter<AgentHistoryListAdapter.ViewHolder> {
    List<AddFundHistoryListData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;
    CommonFunctions commonFunctions;

    public AgentHistoryListAdapter(List<AddFundHistoryListData> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
        commonFunctions=new CommonFunctions(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_history_list_item_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String sign = SthiramValues.SELECTED_CURRENCY_SYMBOL;
        String amount;
        if (SthiramValues.SELECTED_LANGUAGE_ID.equals("2")) {
           amount=sign+" "+commonFunctions.setAmount(list.get(position).getAmount());
        } else {
            amount=commonFunctions.setAmount(list.get(position).getAmount()) + " " + sign;
        }
        holder.tvMessage.setText(amount+", "+list.get(position).getBankName());
        String formattedTime=commonFunctions.getGalilioDateFormat(list.get(position).getDate(),"date");
        String message = null;
        String status = list.get(position).getStatus();
        if (status.equals("Submitted")) {
            holder.tvButton.setBackgroundResource(R.drawable.bg_submitted_box);
            holder.tvButton.setTextColor(context.getColor(R.color.submittedLabelColor));
            holder.tvButton.setText(R.string.submitted);
            message=context.getString(R.string.submitted)+" "+context.getString(R.string.on)+" "+formattedTime;
        } else if (status.equals("Approved")) {
            holder.tvButton.setBackgroundResource(R.drawable.bg_approved_box);
            holder.tvButton.setTextColor(context.getColor(R.color.approvedLabelColor));
            holder.tvButton.setText(R.string.approved);
            message=context.getString(R.string.approved)+" "+context.getString(R.string.on)+" "+formattedTime;
        } else if (status.equals("Rejected")) {
            holder.tvButton.setBackgroundResource(R.drawable.bg_reject_box);
            holder.tvButton.setTextColor(context.getColor(R.color.rejectLabelColor));
            holder.tvButton.setText(R.string.rejected);
            message=context.getString(R.string.rejected)+" "+context.getString(R.string.on)+" "+formattedTime;
        }else if(status.equals("On-hold")){
            holder.tvButton.setBackgroundResource(R.drawable.bg_hold_on_box);
            holder.tvButton.setTextColor(context.getColor(R.color.holdOnColor));
            holder.tvButton.setText(R.string.on_hold);
            message=context.getString(R.string.on_hold)+" "+context.getString(R.string.on)+" "+formattedTime;
        }else{
            holder.tvButton.setBackgroundResource(R.drawable.bg_submitted_box);
            holder.tvButton.setTextColor(context.getColor(R.color.submittedLabelColor));
            holder.tvButton.setText(R.string.submitted);
            message=context.getString(R.string.submitted)+" "+context.getString(R.string.on)+" "+formattedTime;
        }
        holder.tvDate.setText(message);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.itemClick(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvButton;
        TextView tvDate;
        TextView tvMessage;
        LinearLayout llMain;
        public ViewHolder(View itemView) {
            super(itemView);
            tvButton=itemView.findViewById(R.id.tv_button);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvMessage=itemView.findViewById(R.id.tv_message);
            llMain=itemView.findViewById(R.id.ll_main);

        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
