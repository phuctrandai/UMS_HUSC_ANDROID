package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.phuc.ums_husc.R;
import com.practice.phuc.ums_husc.ViewModel.TaiKhoan;

import java.util.ArrayList;
import java.util.List;

public class SearchAccountAdapter extends RecyclerView.Adapter<SearchAccountAdapter.DataViewHolder> {
    private Context mContext;
    private List<TaiKhoan> mLocalAccountList;
    private List<TaiKhoan> mSearchResult;
    private ReceiverAdapter mReceiverAdapter;

    public SearchAccountAdapter(Context context, List<TaiKhoan> accountList, ReceiverAdapter receiverAdapter) {
        mContext = context;
        mLocalAccountList = accountList;
        mReceiverAdapter = receiverAdapter;
        mSearchResult = new ArrayList<>();
    }

    public void onNotifySearchResultChanged(List<TaiKhoan> newResult) {
        mSearchResult.clear();
        mSearchResult.addAll(newResult);
        notifyDataSetChanged();
    }

    public void onFilter(String query) {
        mSearchResult.clear();
        if (!query.equals("")) {
            for (TaiKhoan item : mLocalAccountList) {
                if (item.HoTen.toLowerCase().contains(query.toLowerCase()))
                    mSearchResult.add(item);
            }
        }
        notifyDataSetChanged();
    }

    private void onItemClick(DataViewHolder holder, int postion) {
        TaiKhoan item = mSearchResult.get(postion);
        boolean isInserted = mReceiverAdapter.updateReceiverList(item);

        if (isInserted) {
            holder.ivChecked.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle_primary_24));

        } else {
            holder.ivChecked.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle_grey_24));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final DataViewHolder holder, final int i) {
        TaiKhoan item = mSearchResult.get(i);

        if (mReceiverAdapter.isInReceiverList(item)) {
            holder.ivChecked.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle_primary_24));

        } else {
            holder.ivChecked.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle_grey_24));

        }

        if (item.MaSinhVien != null) {
            String desc = !item.MaSinhVien.equals("") ? item.MaSinhVien : "Giáo viên";
            holder.tvAccountDesc.setText(desc);
        }

        holder.tvAccountName.setText(mSearchResult.get(i).HoTen);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, boolean isLongClick) {
                onItemClick(holder, holder.getAdapterPosition());
            }
        });
    }

    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvAccountName;
        private TextView tvAccountDesc;
        private ImageView ivChecked;

        DataViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvAccountName = itemView.findViewById(R.id.tv_accountName);
            tvAccountDesc = itemView.findViewById(R.id.tv_accountDesc);
            ivChecked = itemView.findViewById(R.id.iv_checked);
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

    interface ItemClickListener {
        void onItemClickListener(View view, int position, boolean isLongClick);
    }
}
