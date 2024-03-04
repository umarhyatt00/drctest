package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yeel.drc.R;
import com.yeel.drc.model.LanguageSelectionModel;

import java.util.List;

public class FirstTimeLanguageSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<LanguageSelectionModel> list;
    public ItemClickAdapterListener onClickListener;

    public FirstTimeLanguageSelectionAdapter(List<LanguageSelectionModel> list, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_layout_language_selected, viewGroup, false);
            viewHolder = new ListHolder(v);
        } else if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.custom_layout_language_un_selected, viewGroup, false);
            viewHolder = new UnSelectedLanguageViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof ListHolder) {
            ListHolder vh1 = (ListHolder) holder;
            vh1.textLanguageName.setText(list.get(position).getLanguageName());
            vh1.textLanguageCode.setText(list.get(position).getLanguageIcon());
            vh1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v, position);
                }
            });
        } else if (holder instanceof UnSelectedLanguageViewHolder) {
            final UnSelectedLanguageViewHolder unVH = (UnSelectedLanguageViewHolder) holder;
            unVH.textLanguageName.setText(list.get(position).getLanguageName());
            unVH.textLanguageCode.setText(list.get(position).getLanguageIcon());
//            progressViewHolder.tvValue.setText(list.get(position).getName());
            unVH.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.itemClick(v, position);
                }
            });

        }
    }

    public static class UnSelectedLanguageViewHolder extends RecyclerView.ViewHolder {
        TextView textLanguageName, textLanguageCode;
//        LinearLayout llMain;

        public UnSelectedLanguageViewHolder(View v) {
            super(v);
            textLanguageName = itemView.findViewById(R.id.text_language_name);
            textLanguageCode = itemView.findViewById(R.id.text_language_code);

        }
    }

    public int getItemViewType(int position) {
        int selectedStatus = 0;
        if (list.get(position).isSelected()) {
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
        TextView textLanguageName, textLanguageCode;
//        LinearLayout llMain;

        public ListHolder(View itemView) {
            super(itemView);
            textLanguageName = itemView.findViewById(R.id.text_language_name);
            textLanguageCode = itemView.findViewById(R.id.text_language_code);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

    }
}
