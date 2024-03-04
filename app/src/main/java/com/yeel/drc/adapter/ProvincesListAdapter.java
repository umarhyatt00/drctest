package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.yeel.drc.R;
import com.yeel.drc.api.provinces.ProvincesData;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class ProvincesListAdapter extends RecyclerView.Adapter<ProvincesListAdapter.ViewHolder> {
    List<ProvincesData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public ProvincesListAdapter(List<ProvincesData> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.botton_list_item_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvHeading.setText(list.get(position).getProvincesName());
        holder.tvHeading.setTextColor(ContextCompat.getColor(context, R.color.textPrimary));
        holder.checked.setVisibility(View.GONE);
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
            tvHeading=itemView.findViewById(R.id.tv_income_name);
            checked=itemView.findViewById(R.id.checked);

        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
