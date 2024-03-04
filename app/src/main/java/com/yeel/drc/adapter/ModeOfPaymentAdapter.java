package com.yeel.drc.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yeel.drc.R;
import com.yeel.drc.api.basicdetails.PaymentMethodsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class ModeOfPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PaymentMethodsData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    CommonFunctions commonFunctions;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        LinearLayout llMain;
        ImageView ivIcon;
        ImageView ivTick;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            llMain=itemView.findViewById(R.id.ll_main);
            ivIcon=itemView.findViewById(R.id.iv_icon);
            ivTick=itemView.findViewById(R.id.iv_tick);
        }
    }

    public ModeOfPaymentAdapter(CommonFunctions commonFunctions, Context con, List<PaymentMethodsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.con=con;
        this.commonFunctions=commonFunctions;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mode_of_payement_list_item_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder =(MyViewHolder) holder;

            String methodCode=list.get(i).getMethodCode();
            if(methodCode.equals(SthiramValues.SEND_VIA_YEEL_CODE)){
                myViewHolder.tvName.setText(R.string.send_via_yeel);
                myViewHolder.ivIcon.setImageResource(R.drawable.ic_y_to_y_logo);
            }else if(methodCode.equals(SthiramValues.CASH_PICKUP_CODE)){
                myViewHolder.tvName.setText(R.string.cash_pickup);
                myViewHolder.ivIcon.setImageResource(R.drawable.ic_cash_pickup_logo);
            }else if(methodCode.equals(SthiramValues.MPESA_KENYA_CODE)){
                myViewHolder.tvName.setText(R.string.m_pesa_kenya);
                myViewHolder.ivIcon.setImageResource(R.drawable.ic_m_pessa_new);
            }else if((methodCode.equals(SthiramValues.AIRTAL_UGANDA_CODE))){
                myViewHolder.tvName.setText(R.string.airtel_uganda);
                myViewHolder.ivIcon.setImageResource(R.drawable.ic_airtel_uganda);
            }else if((methodCode.equals(SthiramValues.MTN_UGANDA_CODE))){
                myViewHolder.tvName.setText(R.string.mtn_uganda);
                myViewHolder.ivIcon.setImageResource(R.drawable.ic_mtn_uganda);
            }

            myViewHolder.llMain.setOnClickListener(view -> {
                itemClickAdapterListener.itemClick(view,i);
            });

        }

    }

    public int getItemViewType(int position) {
        int a = 0;
        return a;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }
}
