package com.yeel.drc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.notification.NotificationData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvDate;
        LinearLayout llMain;
        ImageView ivArrow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage=itemView.findViewById(R.id.rec_alert_message);
            tvDate=itemView.findViewById(R.id.rec_alert_date);
            llMain=itemView.findViewById(R.id.ll_main);
            ivArrow=itemView.findViewById(R.id.iv_arrow);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }


    public NotificationsAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<NotificationData> list, ItemClickAdapterListener itemClickAdapterListener) {
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_list_item_style, viewGroup, false);
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
            MyViewHolder myViewHolder =(MyViewHolder) holder;
            final NotificationData model = list.get(i);
            myViewHolder.tvMessage.setText(model.getMessage());
            myViewHolder.tvDate.setText(commonFunctions.getGalilioDateFormat(list.get(i).getUpdatedTime(),"time"));
            if(model.getReadStatus().equals("0")){
                myViewHolder.tvMessage.setTextColor(Color.parseColor("#5463E8"));
                myViewHolder.llMain.setBackgroundColor(Color.parseColor("#F4F6FC"));
            }else{
                myViewHolder.tvMessage.setTextColor(Color.parseColor("#4E576D"));
                myViewHolder.llMain.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            if(list.get(i).getType().equals("Debit")||list.get(i).getType().equals("Credit")){
                myViewHolder.ivArrow.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.ivArrow.setVisibility(View.GONE);
            }

            myViewHolder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickAdapterListener.itemClick(v,i);
                }
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
        void sendAgain(View v, int position);
    }




}
