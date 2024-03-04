package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yeel.drc.R;
import com.yeel.drc.api.getmycurrencylist.CurrencyListData;
import java.util.List;

public class UserCurrencyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<CurrencyListData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;

    public UserCurrencyListAdapter(Context context, List<CurrencyListData> list, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 1) {
            //selected
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.settings_currency_list_selected, viewGroup, false);
            viewHolder = new SelectedListViewHolder(v);
        } else if (position == 0) {
            //not selected
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.settings_currency_list_unselected, viewGroup, false);
            viewHolder = new UnSelectedListViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder instanceof SelectedListViewHolder) {
            SelectedListViewHolder selectedListViewHolder = (SelectedListViewHolder) holder;
            selectedListViewHolder.tvCurrencyName.setText(list.get(position).getCurrency_name());
            selectedListViewHolder.tvCurrencyCharacterCode.setText(list.get(position).getCurrency_code());
            Log.e("Image URL",list.get(position).getCurrency_image());
            Glide.with(context)
                    .load(list.get(position).getCurrency_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(selectedListViewHolder.ivFlag);
            selectedListViewHolder.llMain.setOnClickListener(v -> onClickListener.itemClick(v,position));
        } else if (holder instanceof UnSelectedListViewHolder) {
            final UnSelectedListViewHolder unselectedListViewHolder = (UnSelectedListViewHolder) holder;
            unselectedListViewHolder.tvCurrencyName.setText(list.get(position).getCurrency_name());
            unselectedListViewHolder.tvCurrencyCharacterCode.setText(list.get(position).getCurrency_code());

            Glide.with(context)
                    .load(list.get(position).getCurrency_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(unselectedListViewHolder.ivFlag);

            unselectedListViewHolder.llMain.setOnClickListener(v -> onClickListener.itemClick(v,position));

        }
    }

    public static class UnSelectedListViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyCharacterCode;
        TextView tvCurrencyName;
        LinearLayout llMain;
        ImageView ivFlag;
        public UnSelectedListViewHolder(View v) {
            super(v);
            tvCurrencyCharacterCode=itemView.findViewById(R.id.tv_currency_character_code);
            tvCurrencyName=itemView.findViewById(R.id.tv_currency_name);
            llMain=v.findViewById(R.id.ll_main);
            ivFlag=v.findViewById(R.id.iv_flag);

        }
    }

    public int getItemViewType(int position) {
        int a = 0;
        if(list.get(position).getSelectedStatus()==1){
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

    public static class SelectedListViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyCharacterCode;
        LinearLayout llMain;
        ImageView ivFlag;
        TextView tvCurrencyName;
        public SelectedListViewHolder(View itemView) {
            super(itemView);
            tvCurrencyCharacterCode=itemView.findViewById(R.id.tv_currency_character_code);
            llMain=itemView.findViewById(R.id.ll_main);
            ivFlag=itemView.findViewById(R.id.iv_flag);
            tvCurrencyName=itemView.findViewById(R.id.tv_currency_name);
        }
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

    }
}
