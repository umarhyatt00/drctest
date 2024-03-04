package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.model.TransactionDateTypeModel;

import java.util.List;

public class DashboardFilterAdapter extends RecyclerView.Adapter<DashboardFilterAdapter.ViewHolder> {
    List<TransactionDateTypeModel> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public DashboardFilterAdapter(List<TransactionDateTypeModel> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_date_type, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvHeading.setText(list.get(position).getTransactionType());
        if (list.get(position).isSelected()) {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_medium);
            holder.tvHeading.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.tvHeading.setTypeface(typeface);
            holder.checked.setImageResource(R.drawable.ic_tick_check);
            holder.checked.setVisibility(View.VISIBLE);
        } else {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.roboto_regular);
            holder.tvHeading.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
            holder.tvHeading.setTypeface(typeface);
            holder.checked.setVisibility(View.GONE);
        }
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
        LinearLayout llMain;
        TextView tvHeading;
        ImageView checked;
        public ViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            tvHeading=itemView.findViewById(R.id.tv_language);
            checked=itemView.findViewById(R.id.checked);

        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
