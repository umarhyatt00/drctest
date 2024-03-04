package com.yeel.drc.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yeel.drc.R;
import com.yeel.drc.api.providersList.ProvidersListData;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class ProvidersListAdapter extends RecyclerView.Adapter<ProvidersListAdapter.MyViewHolder> {
    private List<ProvidersListData> list;
    private final Context context;
    private ItemClickAdapterListener itemClickAdapterListener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlMain;
        TextView tvName;
        ImageView ivImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rlMain=itemView.findViewById(R.id.rl_main);
            tvName=itemView.findViewById(R.id.tv_utilityName);
            ivImage=itemView.findViewById(R.id.iv_image);

        }
    }

    public ProvidersListAdapter(Context context, List<ProvidersListData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.context = context;
        this.itemClickAdapterListener = itemClickAdapterListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.provider_list_style, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        Glide.with(myViewHolder.ivImage.getContext()).load(SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL+""+list.get(i).getImage())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_gray_rectangle)
                .into(myViewHolder.ivImage);
        myViewHolder.tvName.setText(list.get(i).getProvider_name());
        myViewHolder.rlMain.setOnClickListener(v -> itemClickAdapterListener.itemClick(v,i));
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }
}
