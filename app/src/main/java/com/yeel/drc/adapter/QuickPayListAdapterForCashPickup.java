package com.yeel.drc.adapter;

import static com.yeel.drc.utils.SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP;

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
import com.yeel.drc.utils.CommonFunctions;

import java.util.List;

public class QuickPayListAdapterForCashPickup extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    ItemClickAdapterListener itemClickAdapterListener;
    Context context;
    CommonFunctions commonFunctions;
    public QuickPayListAdapterForCashPickup(List<TransactionsData> list, Context context, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.context = context;
        this.itemClickAdapterListener=itemClickAdapterListener;
        commonFunctions = new CommonFunctions(context);
    }
    @NonNull
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        RecyclerView.ViewHolder viewHolder = null;
        if (position == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_quickpay, viewGroup, false);
            viewHolder = new ViewHolderMainItem(v);
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

                String name = "";

                if(list.get(position).getTransaction_type().equals(TRANSACTION_TYPE_USER_CASH_PICKUP)){
                    name = commonFunctions.generateFullName( list.get(position).getBeneficiaryDetails().getFirstname(), list.get(position).getBeneficiaryDetails().getMiddleName(), list.get(position).getBeneficiaryDetails().getLastname());
                }else {
                    name =  list.get(position).getReceiver_name();
                }
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
                    String firstLetter=String.valueOf(name.charAt(0));
                    viewHolderMainItem.tvFirstLetter.setText(firstLetter);
                }

                viewHolderMainItem.tvName.setText(name);

                viewHolderMainItem.llMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickAdapterListener.itemClick(v,position);
                    }
                });
            }
        }

    }

    public int getItemViewType(int position) {
        int a = 0;
        return a;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

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


}
