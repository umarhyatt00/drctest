package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.model.HomeMenuData;

import java.util.List;

public class HomeMenuItemAdapter extends RecyclerView.Adapter<HomeMenuItemAdapter.ViewHolder> {
    List<HomeMenuData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public HomeMenuItemAdapter(List<HomeMenuData> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_menu_slider_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvHeading.setText(list.get(position).getHeading());
        holder.ivLogo.setImageResource(list.get(position).getImage());
        holder.llMain.setOnClickListener(view -> onClickListener.itemClick(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llMain;
        TextView tvHeading;
        ImageView ivLogo;
        public ViewHolder(View itemView) {
            super(itemView);
            llMain = itemView.findViewById(R.id.llMain);
            tvHeading = itemView.findViewById(R.id.tvHeading);
            ivLogo = itemView.findViewById(R.id.ivLogo);
        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(int position);
    }
}
