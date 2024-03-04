package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.model.cashpickup.CashPickupFilterModel;

import java.util.List;

public class AgentCashPickupFilterItemAdapter extends RecyclerView.Adapter<AgentCashPickupFilterItemAdapter.ViewHolder> {
    List<CashPickupFilterModel> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public AgentCashPickupFilterItemAdapter(List<CashPickupFilterModel> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_agent_cash_pickup_filter, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textName.setText(list.get(position).getName());
        holder.textNameSelected.setText(list.get(position).getName());
        holder.checkBox.setChecked(list.get(position).isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                list.get(position).setSelected(true);
            } else {
                list.get(position).setSelected(false);
            }
            notifyDataSetChanged();
        });
        if (list.get(position).isSelected()) {
            holder.textName.setVisibility(View.GONE);
            holder.textNameSelected.setVisibility(View.VISIBLE);
        } else {
            holder.textName.setVisibility(View.VISIBLE);
            holder.textNameSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        //        LinearLayout llMain;
        TextView textName, textNameSelected;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
//            llMain = itemView.findViewById(R.id.ll_main);
            textName = itemView.findViewById(R.id.text_name);
            textNameSelected = itemView.findViewById(R.id.text_name_selected);
            checkBox = itemView.findViewById(R.id.checkBox);

        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
