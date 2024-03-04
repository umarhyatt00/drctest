package com.yeel.drc.adapter.contacts;

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
import com.yeel.drc.api.synccontact.SyncContactData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;


public class AdapterContactList extends RecyclerView.Adapter<AdapterContactList.MyViewHolder> {
    private List<SyncContactData> contactList;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context context;
    CommonFunctions commonFunctions;


    public AdapterContactList(Context context, List<SyncContactData> contactList, ItemClickAdapterListener itemClickAdapterListener) {
        this.contactList = contactList;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.context=context;
        commonFunctions=new CommonFunctions(context);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewUserName;
        TextView tvMobile;
        LinearLayout llMain;
        RelativeLayout ivYeelUser;
        TextView tvFirstLetter;
        ImageView ivProfileImage;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewUserName = (TextView) itemView.findViewById(R.id.mUserContactName);
            tvMobile = itemView.findViewById(R.id.tv_mobile);
            llMain=itemView.findViewById(R.id.ll_main);
            ivYeelUser=itemView.findViewById(R.id.iv_yeel_user);
            tvFirstLetter=itemView.findViewById(R.id.tv_first_letter);
            ivProfileImage=itemView.findViewById(R.id.iv_profile_image);

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_contact, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {


        final SyncContactData model = contactList.get(i);

        myViewHolder.mTextViewUserName.setText(model.getName());
        myViewHolder.tvMobile.setText(commonFunctions.formatAMobileNumber(model.getPhone(), SthiramValues.DEFAULT_COUNTY_MOBILE_CODE, SthiramValues.DEFAULT_MOBILE_NUMBER_FORMAT));
        if(model.getYeel_status()==1){
            myViewHolder.ivYeelUser.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.ivYeelUser.setVisibility(View.GONE);
        }
        String profileImage=contactList.get(i).getProfile_image();
        if(profileImage!=null&&!profileImage.equals("")){
            myViewHolder.tvFirstLetter.setVisibility(View.GONE);
            myViewHolder.ivProfileImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(profileImage)
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.gray_round)
                    .into(myViewHolder.ivProfileImage);
        }else{
            myViewHolder.tvFirstLetter.setVisibility(View.VISIBLE);
            myViewHolder.ivProfileImage.setVisibility(View.GONE);
            myViewHolder.tvFirstLetter.setText(String.valueOf(model.getName().charAt(0)));
        }

        myViewHolder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickAdapterListener.itemClick(v, i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);

        void itemMenu(View v, int position);
    }


}
