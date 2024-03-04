package com.yeel.drc.adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yeel.drc.R;
import com.yeel.drc.api.accountsummary.AccountSummaryData;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import java.util.List;

public class AccountSummaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<AccountSummaryData> list;
    private ItemClickAdapterListener itemClickAdapterListener;
    Context con;
    boolean loadMore;
    CommonFunctions commonFunctions;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvTransactionName;
        TextView tvTransactionDebit;
        TextView tvTransactionCredit;
        TextView tvBalance;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime=itemView.findViewById(R.id.tv_transaction_date);
            tvTransactionName=itemView.findViewById(R.id.tv_transaction_name);
            tvTransactionDebit=itemView.findViewById(R.id.tv_transaction_debit);
            tvTransactionCredit=itemView.findViewById(R.id.tv_transaction_credit);
            tvBalance=itemView.findViewById(R.id.tv_transaction_balance);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }


    public AccountSummaryListAdapter(CommonFunctions commonFunctions, boolean loadMore, Context con, List<AccountSummaryData> list, ItemClickAdapterListener itemClickAdapterListener) {
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_summary_list_style, viewGroup, false);
            viewHolder = new MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.load_more_style, viewGroup, false);
            viewHolder = new ProgressViewHolder(v);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder =(MyViewHolder) holder;
            myViewHolder.tvTime.setText(commonFunctions.getGalilioDateFormat(list.get(i).getTransaction_date(),"date"));
            myViewHolder.tvTransactionName.setText(list.get(i).getBeneficiary_remitter_name());
            if(list.get(i).getEntry_type().equals("credit")){
              //  myViewHolder.tvTransactionDebit.setText("-");
                myViewHolder.tvTransactionDebit.setText(" ");
                myViewHolder.tvTransactionDebit.setTextColor(Color.parseColor("#4E576D"));
                if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
                    myViewHolder.tvTransactionCredit.setText(commonFunctions.setAmount(list.get(i).getAmount())+" +");
                }else{
                    myViewHolder.tvTransactionCredit.setText("+ "+commonFunctions.setAmount(list.get(i).getAmount()));
                }

                myViewHolder.tvTransactionCredit.setTextColor(Color.parseColor("#4EC07C"));

            }else{
                if(SthiramValues.SELECTED_LANGUAGE_ID.equals("2")){
                    myViewHolder.tvTransactionDebit.setText(commonFunctions.setAmount(list.get(i).getAmount())+" -");
                }else{
                    myViewHolder.tvTransactionDebit.setText("- "+commonFunctions.setAmount(list.get(i).getAmount()));
                }

                myViewHolder.tvTransactionDebit.setTextColor(Color.parseColor("#F97D55"));
               // myViewHolder.tvTransactionCredit.setText("-");
                myViewHolder.tvTransactionCredit.setText(" ");
                myViewHolder.tvTransactionCredit.setTextColor(Color.parseColor("#4E576D"));
            }
            myViewHolder.tvBalance.setText(commonFunctions.setAmount(list.get(i).getNew_balance()));


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
