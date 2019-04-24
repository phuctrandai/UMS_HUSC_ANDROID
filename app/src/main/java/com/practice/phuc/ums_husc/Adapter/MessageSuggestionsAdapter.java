package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.MessageModule.DetailMessageActivity;
import com.practice.phuc.ums_husc.Model.TINNHAN;
import com.practice.phuc.ums_husc.R;

import java.util.ArrayList;
import java.util.List;

public class MessageSuggestionsAdapter extends RecyclerView.Adapter<MessageSuggestionsAdapter.SuggestionHolder> {

    private Context mContext;
    private List<TINNHAN> mSuggestions;
    private List<TINNHAN> mLocalData;

    public MessageSuggestionsAdapter(Context context, List<TINNHAN> localData) {
        mContext = context;
        mLocalData = localData;
        mSuggestions = new ArrayList<>();
    }

    public void onNotifySearchResultChanged(List<TINNHAN> newResult) {
        mSuggestions.clear();
        mSuggestions.addAll(newResult);
        notifyDataSetChanged();
    }

    public void onFilter(String query) {
        mSuggestions.clear();
        if (!query.equals("")) {
            for (TINNHAN item : mLocalData) {
                if (item.TieuDe.toLowerCase().contains(query.toLowerCase())
                    || item.HoTenNguoiGui.toLowerCase().contains(query.toLowerCase()))
                    mSuggestions.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionHolder holder, int i) {
        holder.tvTieuDe.setText(mSuggestions.get(i).TieuDe);
        holder.tvNguoiGui.setText(mSuggestions.get(i).HoTenNguoiGui);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, boolean isLongClick) {
                TINNHAN tinNhan = mSuggestions.get(position);
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra(Reference.BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinNhan));
                mContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_suggestion, viewGroup, false);
        return new SuggestionHolder(view);
    }

    @Override
    public int getItemCount() {
        return mSuggestions == null ? 0 : mSuggestions.size();
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTieuDe;
        private TextView tvNguoiGui;

        private SuggestionHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
        }

        ItemClickListener itemClickListener;

        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClickListener(v, getAdapterPosition(), false);
        }
    }

    interface ItemClickListener{
        void onItemClickListener(View view, int position, boolean isLongClick);
    }
}
