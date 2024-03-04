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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yeel.drc.R;
import com.yeel.drc.api.countylist.CountryListData;
import com.yeel.drc.utils.SthiramValues;
import java.util.List;
public class CountrySelectionListAdapter extends RecyclerView.Adapter<CountrySelectionListAdapter.ViewHolder> {
    List<CountryListData> list;
    public ItemClickAdapterListener onClickListener;
    Context context;
    String type;

    public CountrySelectionListAdapter(List<CountryListData> list,String type, Context context, ItemClickAdapterListener onClickListener) {
        this.list = list;
        this.onClickListener = onClickListener;
        this.context=context;
        this.type=type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.county_list_item_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tvHeading.setText(list.get(position).getCountryName());
        Glide.with(context)
                .load(SthiramValues.URL_FLAG_IMAGE+list.get(position).getFlag())
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.gray_round)
                .into(holder.ivFlag);
        if(type.equals("kyc")){
           holder.tvMobileNumberCode.setVisibility(View.INVISIBLE);

        }else{
            holder.tvMobileNumberCode.setVisibility(View.VISIBLE);
            holder.tvMobileNumberCode.setText(list.get(position).getCountryMobileNumberCode());
        }
        holder.llMain.setOnClickListener(v -> onClickListener.itemClick(v,position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llMain;
        TextView tvHeading;
        ImageView ivFlag;
        TextView tvMobileNumberCode;

        public ViewHolder(View itemView) {
            super(itemView);
            llMain=itemView.findViewById(R.id.ll_main);
            tvHeading=itemView.findViewById(R.id.tv_county_name);
            ivFlag=itemView.findViewById(R.id.iv_flag);
            tvMobileNumberCode=itemView.findViewById(R.id.tv_mobile_number_code);
        }

    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);


    }
}
