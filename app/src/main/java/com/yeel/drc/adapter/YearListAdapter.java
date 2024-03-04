package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.model.YearModelModel;

import java.util.List;


public class YearListAdapter extends RecyclerView.Adapter<YearListAdapter.ViewHolder> {
    List<YearModelModel> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public YearListAdapter(List<YearModelModel> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvHeading.setText(list.get(position).getYearName());
        if (list.get(position).isSelected()) {
            holder.llMain.setBackgroundResource(R.drawable.bg_selected_month);
            holder.tvHeading.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.llMain.setBackgroundResource(R.drawable.bg_not_selected_month);
            holder.tvHeading.setTextColor(ContextCompat.getColor(context, R.color.textSecondary));
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
        public ViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            tvHeading=itemView.findViewById(R.id.tv_heading);
        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
