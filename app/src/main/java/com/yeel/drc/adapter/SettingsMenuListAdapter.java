package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;

import java.util.List;


public class SettingsMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> list;
    public ItemClickAdapterListener onClickListener;

    public SettingsMenuListAdapter(List<String> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_list_style, viewGroup, false);
            viewHolder = new ListHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ListHolder) {
            ListHolder vh1 = (ListHolder) holder;
            vh1.tvLag.setText(list.get(position));
            vh1.tvLag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v,position);
                }
            });
        }
    }

    public int getItemViewType(int position) {
        int a = 0;
        return a;
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    public static class ListHolder extends RecyclerView.ViewHolder {
        TextView tvLag;
        public ListHolder(View itemView) {
            super(itemView);
            tvLag=itemView.findViewById(R.id.tv_title);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

    }
}
