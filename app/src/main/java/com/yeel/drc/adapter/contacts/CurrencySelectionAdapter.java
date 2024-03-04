package com.yeel.drc.adapter.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class CurrencySelectionAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<CurrencyListData> list;
    public CurrencySelectionAdapter.ItemClickAdapterListener onClickListener;
    Context context;

    public CurrencySelectionAdapter(Context context, List<CurrencyListData> list, CurrencySelectionAdapter.ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        //TODO: CHANGE THE LOGIC ONCE U CALL THE API
        if (position == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.bank_currency_selected, viewGroup, false);
            viewHolder = new CurrencySelectionAdapter.ListHolder(v);
        } else if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.bank_currency_unselected, viewGroup, false);
            viewHolder = new CurrencySelectionAdapter.ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof CurrencySelectionAdapter.ListHolder) {
            CurrencySelectionAdapter.ListHolder vh1 = (CurrencySelectionAdapter.ListHolder) holder;

            vh1.tvCurrencyName.setText(list.get(position).getCurrency_name());
            vh1.tvCurrencyCharacterCode.setText(list.get(position).getCurrency_code());

            Glide.with(context)
                    .load(list.get(position).getCurrency_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(vh1.ivFlag);


            vh1.llMain.setOnClickListener(v -> onClickListener.itemClick(v,position));
        } else if (holder instanceof CurrencySelectionAdapter.ProgressViewHolder) {
            final CurrencySelectionAdapter.ProgressViewHolder progressViewHolder = (CurrencySelectionAdapter.ProgressViewHolder) holder;
            progressViewHolder.tvCurrencyName.setText(list.get(position).getCurrency_name());
            progressViewHolder.tvCurrencyCharacterCode.setText(list.get(position).getCurrency_code());

            Glide.with(context)
                    .load(list.get(position).getCurrency_image())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(progressViewHolder.ivFlag);

            progressViewHolder.llMain.setOnClickListener(v -> onClickListener.itemClick(v,position));

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyCharacterCode;
        TextView tvCurrencyName;
        LinearLayout llMain;
        ImageView ivFlag;
        public ProgressViewHolder(View v) {
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

    public static class ListHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyCharacterCode;
        LinearLayout llMain;
        ImageView ivFlag;
        TextView tvCurrencyName;
        public ListHolder(View itemView) {
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
