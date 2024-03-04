package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.api.cashpickuppurpose.PurposeListData;

import java.util.List;

public class CashPickupPurposeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<PurposeListData> list;
    public ItemClickAdapterListener onClickListener;

    public CashPickupPurposeListAdapter(List<PurposeListData> list, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.remit_purpose_list_style, viewGroup, false);
            viewHolder = new ListHolder(v);
        } else if (position == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.remit_purpose_list_style_selected, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ListHolder) {
            ListHolder vh1 = (ListHolder) holder;
            vh1.tvValue.setText(list.get(position).getName());
            vh1.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v, position);
                }
            });
        } else if (holder instanceof ProgressViewHolder) {
            final ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            progressViewHolder.tvValue.setText(list.get(position).getName());
            progressViewHolder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v, position);
                }
            });

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView tvValue;
        LinearLayout llMain;

        public ProgressViewHolder(View v) {
            super(v);
            tvValue = itemView.findViewById(R.id.tv_value);
            llMain = itemView.findViewById(R.id.ll_main);

        }
    }

    public int getItemViewType(int position) {
        int selectedStatus = 0;
        if (list.get(position).getSelectedStatus() == 1) {
            selectedStatus = 1;
        } else {
            selectedStatus = 0;
        }
        return selectedStatus;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ListHolder extends RecyclerView.ViewHolder {
        TextView tvValue;
        LinearLayout llMain;

        public ListHolder(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.tv_value);
            llMain = itemView.findViewById(R.id.ll_main);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

    }
}
