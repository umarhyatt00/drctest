package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.ArrayList;
import java.util.List;

public class AgentAllTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    List<String> dateList;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlTitleDate;
        TextView tvTitleDate;
        TextView tvName;
        TextView tvAmount;
        TextView tvDescription;
        View viewUnderLine;
        RelativeLayout rlMain;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rlTitleDate=itemView.findViewById(R.id.rl_title_date);
            tvTitleDate=itemView.findViewById(R.id.tv_title_date);
            tvName=itemView.findViewById(R.id.tv_name);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvDescription=itemView.findViewById(R.id.tv_description);
            viewUnderLine=itemView.findViewById(R.id.view_under_line);
            rlMain=itemView.findViewById(R.id.rl_main);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar =v.findViewById(R.id.progressBar1);
        }
    }


    public AgentAllTransactionsAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<TransactionsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.con=con;
        this.loadMore=loadMore;
        this.commonFunctions=commonFunctions;
        dateList=new ArrayList<>();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agent_all_transactions_list_item_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.load_more_style, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            String date = commonFunctions.getGalilioDateFormat(list.get(i).getTransaction_date(), "date");
            if(list.get(i).getShowHeading()){
                myViewHolder.rlTitleDate.setVisibility(View.VISIBLE);
                myViewHolder.tvTitleDate.setText(date);
                myViewHolder.viewUnderLine.setVisibility(View.GONE);
            }else{
                myViewHolder.rlTitleDate.setVisibility(View.GONE);
                myViewHolder.viewUnderLine.setVisibility(View.VISIBLE);
            }


            if (commonFunctions.getUserId().equals(list.get(i).getSender_id())) {
                //sender
                myViewHolder.tvName.setText(commonFunctions.toTitleCase(list.get(i).getReceiver_name()));
                String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;

                myViewHolder.tvAmount.setText("- "+commonFunctions.setAmount(list.get(i).getAmount()) + " "+sign);
                myViewHolder.tvAmount.setTextColor(con.getResources().getColor(R.color.red));
            } else {
                //receiver
                myViewHolder.tvName.setText(commonFunctions.toTitleCase(list.get(i).getSender_name()));
                String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;

                myViewHolder.tvAmount.setText("+ "+commonFunctions.setAmount(list.get(i).getAmount()) + " "+sign);
                myViewHolder.tvAmount.setTextColor(con.getResources().getColor(R.color.green));
            }

            myViewHolder.tvDescription.setText(commonFunctions.getGalilioDateFormat(list.get(i).getTransaction_date(), "date") +
                    ", "+list.get(i).getTransaction_type());
            myViewHolder.rlMain.setOnClickListener(view -> itemClickAdapterListener.itemClick(view,i));





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
        if(loadMore){
            size=list.size()+1;
        }else{
            size= list.size();
        }
        return size;
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
        void sendAgain(View v, int position);
    }




}
