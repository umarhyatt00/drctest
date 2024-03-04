package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yeel.drc.R;
import com.yeel.drc.api.recenttransactions.TransactionsData;

import java.util.List;

public class QuickPayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    ItemClickAdapterListener itemClickAdapterListener;
    Context context;
    public QuickPayListAdapter( List<TransactionsData> list, Context context, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.context = context;
        this.itemClickAdapterListener=itemClickAdapterListener;
    }
    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_quickpay, viewGroup, false);
            viewHolder = new ViewHolderMainItem(v);
        } else if (position == 1) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.benificiary_list_last_item, viewGroup, false);
            viewHolder = new ViewHolderLastItem(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof ViewHolderMainItem) {

            ViewHolderMainItem viewHolderMainItem = (ViewHolderMainItem) holder;
            if(list.get(position).getId().equals("")){
                viewHolderMainItem.tvName.setText(" ");
                viewHolderMainItem.tvFirstLetter.setText("");
            }else{
                String receiverImage=list.get(position).getProfile_image();
                if(receiverImage!=null&&!receiverImage.equals("")&&!receiverImage.equals("null")){
                    viewHolderMainItem.mRLFirstLetter.setVisibility(View.GONE);
                    viewHolderMainItem.mRLUserImage.setVisibility(View.VISIBLE);

                    Glide.with(viewHolderMainItem.imgUserPicture.getContext())
                            .load(list.get(position).getProfile_image())
                            .apply(new RequestOptions().circleCrop())
                            .into(viewHolderMainItem.imgUserPicture);
                }else{
                    viewHolderMainItem.mRLFirstLetter.setVisibility(View.VISIBLE);
                    viewHolderMainItem.mRLUserImage.setVisibility(View.GONE);
                    String firstLetter=String.valueOf(list.get(position).getReceiver_name().charAt(0));
                    viewHolderMainItem.tvFirstLetter.setText(firstLetter);
                }

                viewHolderMainItem.tvName.setText(list.get(position).getReceiver_name());

                viewHolderMainItem.llMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickAdapterListener.itemClick(v,position,"first");
                    }
                });
            }
        } else if (holder instanceof ViewHolderLastItem) {
            ViewHolderLastItem viewHolderLastItem= (ViewHolderLastItem) holder;
           /* if(from.equals("Cash Pickup")){
                viewHolderLastItem.tvName.setText(context.getString(R.string.cashpickup_contacts));
            }else if(from.equals("Yeel")){
                viewHolderLastItem.tvName.setText(context.getString(R.string.yeel_contacts));

            }else {
                viewHolderLastItem.tvName.setText(context.getString(R.string.mobilepay_contacts));
            }*/

            viewHolderLastItem.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickAdapterListener.itemClick(v,position,"last");
                }
            });
        }

    }

    public int getItemViewType(int position) {
        int a = 0;
        if (position < list.size()) {
            a = 0;
        } else {
            a = 1;
        }
        return a;
    }

    @Override
    public int getItemCount() {
        int size;
        size = list.size() + 1;
        return size;
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position,String from);

    }


    public class ViewHolderMainItem extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvFirstLetter;
        LinearLayout llMain;
        ImageView imgUserPicture;
        RelativeLayout mRLFirstLetter,mRLUserImage;



        public ViewHolderMainItem(@NonNull View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.tv_name);
            llMain=itemView.findViewById(R.id.ll_main);
            mRLFirstLetter= itemView.findViewById(R.id.rl_tv_image);
            mRLUserImage= itemView.findViewById(R.id.rl_iv_image);
            imgUserPicture = itemView.findViewById(R.id.tv_logo);
            tvFirstLetter = itemView.findViewById(R.id.tv_image);


        }
    }

    public static class ViewHolderLastItem extends RecyclerView.ViewHolder {
        LinearLayout ll_main;
        TextView tvName;

        public ViewHolderLastItem(View v) {
            super(v);
            ll_main = v.findViewById(R.id.ll_main);
            tvName=itemView.findViewById(R.id.tv_name);
        }
    }



}
