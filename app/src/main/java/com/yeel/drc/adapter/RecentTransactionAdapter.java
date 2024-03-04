package com.yeel.drc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yeel.drc.R;
import com.yeel.drc.api.recenttransactions.BeneficiaryData;
import com.yeel.drc.api.recenttransactions.TransactionsData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;

import java.util.List;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TransactionsData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvAmount;
        TextView tvMessage;
        TextView tvDate;
        LinearLayout llMain;
        TextView tvFirstLetter;
        ImageView ivProfileImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName=itemView.findViewById(R.id.tv_user_name);
            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvMessage=itemView.findViewById(R.id.tv_message);
            llMain=itemView.findViewById(R.id.ll_main);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvFirstLetter=itemView.findViewById(R.id.tvFirstLetter);
            ivProfileImage=itemView.findViewById(R.id.ivProfileImage);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }


    public RecentTransactionAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<TransactionsData> list, ItemClickAdapterListener itemClickAdapterListener) {
        this.list = list;
        this.itemClickAdapterListener = itemClickAdapterListener;
        this.con=con;
        this.loadMore=loadMore;
        this.commonFunctions=commonFunctions;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder = null;
        if (i == 0) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_recent_transactions_list_item_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.load_more_style, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder =(MyViewHolder) holder;

            //sender or receiver
            String sign= SthiramValues.SELECTED_CURRENCY_SYMBOL;
            String transactionType=list.get(i).getTransaction_type();
            String formattedTime=commonFunctions.getGalilioDateFormat(list.get(i).getTransaction_date(),"time");
            String amountToShow=commonFunctions.setAmount(list.get(i).getAmount());
            String nameToDisplay="";
            String firstLetter="";
            String message="";
            String debitOrCredit="";
            String profileImage="";
            if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_USER_CASH_PICKUP)){
                //debit transaction
                message=con.getString(R.string.sent_on);
                debitOrCredit="debit";
                BeneficiaryData  beneficiaryData=list.get(i).getBeneficiaryDetails();
                try {
                    nameToDisplay=commonFunctions.generateFullName(beneficiaryData.getFirstname(),beneficiaryData.getMiddleName(), beneficiaryData.getLastname());

                }catch (Exception e){

                }
                profileImage="";

            }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_CASH_OUT)){
                //cash out transaction is a debit transaction
                message=con.getString(R.string.cash_out_on);
                debitOrCredit="debit";
                nameToDisplay=commonFunctions.toTitleCase(list.get(i).getReceiver_name());
                profileImage=list.get(i).getProfile_image();
            }else if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_AGENT_SEND_VIA_YEEL)){
                profileImage="";
                debitOrCredit = "credit";
                message = con.getString(R.string.received_on);
                String fullName=commonFunctions.generateFullName(list.get(i).getRemitterDetails().getFirstname(),list.get(i).getRemitterDetails().getMiddleName(),list.get(i).getRemitterDetails().getLastname());
                nameToDisplay=commonFunctions.toTitleCase(fullName);
            } else if (transactionType.equals(SthiramValues.TRANSACTION_TYPE_EXCHANGE)){
                if(SthiramValues.SELECTED_CURRENCY_SYMBOL.equals("SSP")){
                    message = con.getString(R.string.received_on);
                    debitOrCredit = "credit";
                    nameToDisplay= list.get(i).getSender_name();
                }else{
                    message=con.getString(R.string.sent_on);
                    debitOrCredit="debit";
                    nameToDisplay= list.get(i).getReceiver_name();
                }
            }else{
                profileImage=list.get(i).getProfile_image();
                if(commonFunctions.getUserId().equals(list.get(i).getSender_id())){
                    //sender
                    message=con.getString(R.string.sent_on);
                    debitOrCredit="debit";
                    nameToDisplay=commonFunctions.toTitleCase(list.get(i).getReceiver_name());
                }else{
                    //receiver
                    debitOrCredit = "credit";
                    if(list.get(i).getPrevious_transactionId()!=null){
                        message = con.getString(R.string.reversed_on);
                        nameToDisplay = commonFunctions.toTitleCase(list.get(i).getSender_name());
                    }else{
                        nameToDisplay = commonFunctions.toTitleCase(list.get(i).getSender_name());
                        message = con.getString(R.string.received_on);
                    }
                }
            }

            //set amount
            if(debitOrCredit.equals("credit")){
                myViewHolder.tvAmount.setTextColor(con.getResources().getColor(R.color.green));
                if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
                    myViewHolder.tvAmount.setText(sign+" "+amountToShow+" +");
                }else{
                    myViewHolder.tvAmount.setText("+ "+amountToShow+" "+sign);
                }
            }else{
                myViewHolder.tvAmount.setTextColor(con.getResources().getColor(R.color.red));
                if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
                    myViewHolder.tvAmount.setText(sign+" "+amountToShow+" -");
                }else{
                    myViewHolder.tvAmount.setText("- "+amountToShow+" "+sign);
                }
            }

            //status color
            if(list.get(i).getStatus().toLowerCase().equals(SthiramValues.SUCCESS)) {
            }else if(list.get(i).getStatus().toLowerCase().equals(SthiramValues.PENDING)){
                message=con.getString(R.string.inprogress_on);
            }else{
                message=con.getString(R.string.cancelled_on);
            }



            //set message
            myViewHolder.tvMessage.setText(message);
            myViewHolder.tvDate.setText(" " + formattedTime + " ");

            //show name and first letter
            try {
                myViewHolder.tvUserName.setText(nameToDisplay);
                firstLetter= String.valueOf(nameToDisplay.charAt(0));
            }catch (Exception ignored){

            }

            if(debitOrCredit.equals("debit")){
                if(transactionType.equals(SthiramValues.TRANSACTION_TYPE_UTILITIES_PAYMENTS)){
                    if(list.get(i).getProviderData()!=null){
                        profileImage= SthiramValues.BILL_PAYMENT_IMAGE_BASE_URL+""+list.get(i).getProviderData().getImage();
                    }
                }
            }

            //show profile image
            if(profileImage!=null&&!profileImage.equals("")){
                myViewHolder.tvFirstLetter.setText("");
                Glide.with(con)
                        .load(profileImage)
                        .apply(new RequestOptions().circleCrop())
                        .placeholder(R.drawable.gray_round)
                        .into(myViewHolder.ivProfileImage);
            }else{
                myViewHolder.tvFirstLetter.setText(firstLetter);
            }

            myViewHolder.llMain.setOnClickListener(v -> itemClickAdapterListener.itemClick(v, i));

        }else if(holder instanceof ProgressViewHolder){
            final ProgressViewHolder progressViewHolder= (ProgressViewHolder) holder;
            progressViewHolder.progressBar.setIndeterminate(true);
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
        if(loadMore){
            size=list.size()+1;
        }else{
            size= list.size();
        }
        return size;
    }

    public interface ItemClickAdapterListener {
        void itemClick(View v, int position);
    }




}
