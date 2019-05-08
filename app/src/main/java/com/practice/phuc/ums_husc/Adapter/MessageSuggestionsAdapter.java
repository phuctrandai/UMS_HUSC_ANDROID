package com.practice.phuc.ums_husc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.phuc.ums_husc.Helper.DateHelper;
import com.practice.phuc.ums_husc.Helper.Reference;
import com.practice.phuc.ums_husc.Helper.StringHelper;
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

    public void onNotifyInsertMore(List<TINNHAN> newResult) {
        mSuggestions.addAll(newResult);
        notifyItemRangeChanged(getItemCount(), newResult.size());
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
        String thoiDiemGui = mSuggestions.get(i).ThoiDiemGui;
        String thoiDiemGuiStr = thoiDiemGui;
        if (thoiDiemGui.length() > 16) {
            String ngayDang = DateHelper.formatYMDToDMY(thoiDiemGui.substring(0, 10));
            String gioDang = thoiDiemGui.substring(11, 16);
            thoiDiemGuiStr = ngayDang + " " + gioDang;
        }
        String tenNguoiNhanCollapse = mSuggestions.get(i).getTenNguoiNhanCollapse();
        holder.tvThoiDiemGui.setText(thoiDiemGuiStr);
        holder.tvTieuDe.setText(mSuggestions.get(i).TieuDe);
        holder.tvNguoiGui.setText(mSuggestions.get(i).HoTenNguoiGui);
        holder.tvNguoiNhan.setText(tenNguoiNhanCollapse);
        holder.tvNguoiGuiLabel.setText(StringHelper.getFirstCharToCap(mSuggestions.get(i).TieuDe));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, boolean isLongClick) {
                TINNHAN tinNhan = mSuggestions.get(position);
                Intent intent = new Intent(mContext, DetailMessageActivity.class);
                intent.putExtra(Reference.getInstance().BUNDLE_EXTRA_MESSAGE, TINNHAN.toJson(tinNhan));
                mContext.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public SuggestionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message, viewGroup, false);
        return new SuggestionHolder(view);
    }

    @Override
    public int getItemCount() {
        return mSuggestions == null ? 0 : mSuggestions.size();
    }

    static class SuggestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTieuDe;
        private TextView tvThoiDiemGui;
        private TextView tvNguoiGui;
        private TextView tvNguoiGuiLabel;
        private TextView tvNguoiNhan;

        private SuggestionHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTieuDe = itemView.findViewById(R.id.tv_tieuDe);
            tvNguoiGui = itemView.findViewById(R.id.tv_nguoiGui);
            tvThoiDiemGui = itemView.findViewById(R.id.tv_thoiDiemGui);
            tvNguoiGuiLabel = itemView.findViewById(R.id.tv_nguoiGuiLabel);
            tvNguoiNhan = itemView.findViewById(R.id.tv_nguoiNhan);
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
