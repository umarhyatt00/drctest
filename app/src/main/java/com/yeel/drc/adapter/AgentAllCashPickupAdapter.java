package com.yeel.drc.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yeel.drc.R;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class AgentAllCashPickupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvButton;
        TextView tvDate;
        TextView tvMessage;
        LinearLayout llMain;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvButton = itemView.findViewById(R.id.tv_button);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvMessage = itemView.findViewById(R.id.tv_message);
            llMain = itemView.findViewById(R.id.ll_main);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar =v.findViewById(R.id.progressBar1);
        }
    }


    public AgentAllCashPickupAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<TransactionsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.con=con;
        this.loadMore=loadMore;
        this.commonFunctions=commonFunctions;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.agent_history_list_item_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.load_more_style, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder =(MyViewHolder) holder;
            String formattedTime=commonFunctions.getGalilioDateFormat(list.get(position).getTransaction_date(),"date2");
            String message="";
            String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
            String amountToShow=commonFunctions.setAmount(list.get(position).getAmount());
            String status = list.get(position).getCollection_status();
            if (status.equals("NotCollected")) {
                myViewHolder.tvButton.setBackgroundResource(R.drawable.bg_submitted_box);
                myViewHolder.tvButton.setTextColor(con.getColor(R.color.submittedLabelColor));
                myViewHolder.tvButton.setText(R.string.to_pay);
                message=con.getString(R.string.requested_on);
            } else if (status.equals("Collected")) {
                myViewHolder.tvButton.setBackgroundResource(R.drawable.bg_approved_box);
                myViewHolder.tvButton.setTextColor(con.getColor(R.color.approvedLabelColor));
                myViewHolder.tvButton.setText(R.string.paid);
                message=con.getString(R.string.requested_on);
            } else if (status.equals("Returned")) {
                myViewHolder.tvButton.setBackgroundResource(R.drawable.bg_reject_box);
                myViewHolder.tvButton.setTextColor(con.getColor(R.color.rejectLabelColor));
                myViewHolder.tvButton.setText(R.string.returned);
                message=con.getString(R.string.requested_on);
            }

            //set date

           // myViewHolder.tvDate.setText(message+" "+formattedTime+" ("+list.get(position).getTransaction_type()+")");
            myViewHolder.tvDate.setText(message+" "+formattedTime);

            String transactionType=list.get(position).getTransaction_type();
            if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)||transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_CASH_PICKUP)){
                try{
                    String fullName=commonFunctions.generateFullName(list.get(position).getBeneficiaryDetails().getFirstname(),
                            list.get(position).getBeneficiaryDetails().getMiddleName(),
                            list.get(position).getBeneficiaryDetails().getLastname());
                    myViewHolder.tvMessage.setText(amountToShow+" "+sign+" to "+fullName);
                }catch (Exception e){
                    commonFunctions.logAValue("TID",list.get(position).getYdb_ref_id());
                    myViewHolder.tvMessage.setText(amountToShow+" "+sign+" to ---");
                }

            }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)){
                myViewHolder.tvMessage.setText(amountToShow+" "+sign+" to "+list.get(position).getSender_name());
            }

            //
            myViewHolder.llMain.setOnClickListener(view -> {
                 itemClickAdapterListener.itemClick(view,position);
            });

        }else if(holder instanceof ProgressViewHolder){
            final ProgressViewHolder progressViewHolder= (ProgressViewHolder) holder;
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
    }




}
