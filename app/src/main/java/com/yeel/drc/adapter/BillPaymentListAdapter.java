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
import com.yeel.drc.api.basicdetails.BillPaymentOptionsData;

import java.util.List;

public class BillPaymentListAdapter extends RecyclerView.Adapter<BillPaymentListAdapter.MyViewHolder> {

    private List<BillPaymentOptionsData> list;
    private final Context context;
    private ItemClickAdapterListener itemClickAdapterListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tv_name;
        ImageView ivIcon;
        LinearLayout llMain;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_name=itemView.findViewById(R.id.tv_name);
            this.ivIcon=itemView.findViewById(R.id.iv_icon);
            this.llMain=itemView.findViewById(R.id.ll_main);


        }
    }

    public BillPaymentListAdapter(Context context, List<BillPaymentOptionsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.context = context;
        this.itemClickAdapterListener = itemClickAdapterListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_bill_payment_style, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if(list.get(i).getId().equals("1")){
            myViewHolder.ivIcon.setImageResource(R.drawable.ic_digital_tv);
            myViewHolder.tv_name.setText(R.string.digital_tv);

        }else if(list.get(i).getId().equals("2")){
            myViewHolder.ivIcon.setImageResource(R.drawable.ic_utilities_new);
            myViewHolder.tv_name.setText(R.string.utilities);
        }
        else if(list.get(i).getId().equals("3")){
            myViewHolder.ivIcon.setImageResource(R.drawable.ic_education);
            myViewHolder.tv_name.setText(R.string.education);
        }
        else if(list.get(i).getId().equals("4")){
            myViewHolder.ivIcon.setImageResource(R.drawable.ic_gov_new);
            myViewHolder.tv_name.setText(R.string.government);
        }



        myViewHolder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickAdapterListener.itemClick(v,i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }


}
