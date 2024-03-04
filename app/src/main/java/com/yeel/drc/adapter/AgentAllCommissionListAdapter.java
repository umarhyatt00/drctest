package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.utils.CommonFunctions;

import java.util.ArrayList;
import java.util.List;

public class AgentAllCommissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    List<String> dateList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvHeading;
        TextView tvAmount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvHeading = itemView.findViewById(R.id.tv_heading);
            tvAmount = itemView.findViewById(R.id.tv_amount);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }


    public AgentAllCommissionListAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<TransactionsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.con = con;
        this.loadMore = loadMore;
        this.commonFunctions = commonFunctions;
        dateList = new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agent_all_commission_list_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.load_more_style, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            myViewHolder.tvHeading.setText(list.get(position).getTransaction_description());
            myViewHolder.tvAmount.setText(commonFunctions.setAmount(list.get(position).getAmount()));
            myViewHolder.tvDate.setText(commonFunctions.getGalilioDateFormat(list.get(position).getTransaction_date(), "date"));

        } else if (holder instanceof ProgressViewHolder) {
            final ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            progressViewHolder.progressBar.setIndeterminate(true);
        }


    }

    public int getItemViewType(int position) {
        int a = 0;
        if (position < list.size()) {
            a = 0;
        } else {
            a = 1;
        }
        return a;
    }

    @Override
    public int getItemCount() {
        int size;
        if (loadMore) {
            size = list.size() + 1;
        } else {
            size = list.size();
        }
        return size;
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

        void sendAgain(View v, int position);
    }


}
