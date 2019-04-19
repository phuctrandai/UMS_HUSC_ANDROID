package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.TaiKhoan;

import java.util.ArrayList;
import java.util.List;

public class SearchAccountAdapter extends RecyclerView.Adapter<SearchAccountAdapter.DataViewHolder> {
    private Context mContext;
    private List<TaiKhoan> mAccountList;
    private List<TaiKhoan> mSearchResult;

    public SearchAccountAdapter(Context context, List<TaiKhoan> accountList) {
        mContext = context;
        mAccountList = accountList;
        mSearchResult = new ArrayList<>();
    }

    public void onFilter(String query) {
        mSearchResult.clear();
        if (!query.equals("")) {
            for (TaiKhoan item : mAccountList) {
                if (item.HoVaTen.toLowerCase().contains(query.toLowerCase()))
                    mSearchResult.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public List<TaiKhoan> getDataSet() { return mSearchResult; }

    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvAccountId;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvAccountId = itemView.findViewById(R.id.tv_accountName);
        }

        private ItemClickListener itemClickListener;
        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClickListener(v, getAdapterPosition(), false);
        }
    }

    interface ItemClickListener {
        void onItemClickListener(View view, int position, boolean isLongClick);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int i) {
        holder.tvAccountId.setText(mSearchResult.get(i).HoVaTen);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, boolean isLongClick) {
                Log.d("DEBUG", "CHOOSE THIS ACCOUNT: " + mSearchResult.get(position).HoVaTen);
            }
        });
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_account, viewGroup, false);
        return new DataViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mSearchResult == null ? 0 : mSearchResult.size();
    }
}
