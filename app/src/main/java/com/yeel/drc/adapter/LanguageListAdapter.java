package com.yeel.drc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;


public class LanguageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<String> list;
    public ItemClickAdapterListener onClickListener;

    public LanguageListAdapter(List<String> list, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.language_list_style, viewGroup, false);
            viewHolder = new ListHolder(v);
        } else if (position == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.language_list_style_selected, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ListHolder) {
            ListHolder vh1 = (ListHolder) holder;
            if(list.get(position).equals("Arabic")){
                vh1.tvLag.setText("عربي");
            }else if(list.get(position).equals("Turkish")){
                vh1.tvLag.setText("Türk");
            }else {
                vh1.tvLag.setText(list.get(position));
            }

            vh1.tvLag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v,position);
                }
            });
        } else if (holder instanceof ProgressViewHolder) {
            final ProgressViewHolder progressViewHolder = (ProgressViewHolder) holder;
            if(list.get(position).equals("Arabic")){
                progressViewHolder.tvLag.setText("عربي");
            }else if(list.get(position).equals("Turkish")){
                progressViewHolder.tvLag.setText("Türk");
            }else{
                progressViewHolder.tvLag.setText(list.get(position));
            }
            progressViewHolder.tvLag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v,position);
                }
            });

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView tvLag;
        public ProgressViewHolder(View v) {
            super(v);
            tvLag=itemView.findViewById(R.id.tv_language);

        }
    }

    public int getItemViewType(int position) {
        int a = 0;
        if(list.get(position).equals(SthiramValues.SELECTED_LANGUAGE_NAME)){
            a = 1;
        }else{
            a = 0;
        }
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
            tvLag=itemView.findViewById(R.id.tv_language);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

    }
}
